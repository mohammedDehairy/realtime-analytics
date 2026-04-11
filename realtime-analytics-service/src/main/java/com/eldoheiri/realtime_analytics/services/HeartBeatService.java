package com.eldoheiri.realtime_analytics.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eldoheiri.realtime_analytics.dataobjects.events.ApplicationEventDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;
import com.eldoheiri.realtime_analytics.kafka.producer.MessageQueue;
import com.eldoheiri.realtime_analytics.security.idgeneration.IdentifierUtil;
import com.eldoheiri.realtime_analytics.dataobjects.Application;

import io.jsonwebtoken.Claims;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HeartBeatService {

    @Autowired
    private IdentifierUtil identifierUtil;

    @Autowired
    private MessageQueue<Map<String, Object>> heartBeatMessageQueue;

    public void send(Map<String, Object> message) {
        try {
            heartBeatMessageQueue.send(message);
        } catch (MessageQueueException e) {
            e.printStackTrace();
        }
    }

    public void processAuthenticatedHeartbeat(HeartBeatDTO sessionHeartBeat, String pathSessionId, String pathApplicationId)
            throws HeartBeatException, InvalidKeyException, NoSuchAlgorithmException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Claims claims)) {
            throw new HeartBeatException("Missing authenticated session claims");
        }

        String sessionId = claims.getSubject();
        String applicationId = claims.get("applicationId", String.class);
        String deviceId = claims.get("deviceId", String.class);

        if (!pathSessionId.equals(sessionId) || !pathApplicationId.equals(applicationId)) {
            throw new HeartBeatException("Token does not match request path");
        }

        processTrustedHeartbeat(sessionHeartBeat, sessionId, applicationId, deviceId);
    }

    public void processInitialHeartbeat(HeartBeatDTO sessionHeartBeat, String sessionId, String applicationId, String deviceId)
            throws HeartBeatException, InvalidKeyException, NoSuchAlgorithmException {
        processTrustedHeartbeat(sessionHeartBeat, sessionId, applicationId, deviceId);
    }

    private void processTrustedHeartbeat(HeartBeatDTO sessionHeartBeat, String sessionId, String applicationId, String deviceId) throws HeartBeatException, InvalidKeyException, NoSuchAlgorithmException {
        if (Application.fromId(applicationId) == null) {
            throw new HeartBeatException("Application not found");
        }

        if (!identifierUtil.validateIdentifier(applicationId, deviceId)) {
            throw new HeartBeatException("Invalid device id");
        }

        if (!identifierUtil.validateIdentifier(deviceId, sessionId)) {
            throw new HeartBeatException("Invalid session id");
        }

        for (ApplicationEventDTO eventDTO : sessionHeartBeat.getEvents()) {
            Map<String, Object> eventMessage = new HashMap<>();
            eventMessage.put("timestamp", eventDTO.getTimestamp().getTime());
            eventMessage.put("sessionId", sessionId);
            eventMessage.put("applicationId", applicationId);
            eventMessage.put("deviceId", deviceId);
            eventMessage.put("eventType", eventDTO.getType());
            if (eventDTO.getAttributes() != null) {
                eventMessage.putAll(eventDTO.getAttributes());
            }
            send(eventMessage);
        }
    }
}
