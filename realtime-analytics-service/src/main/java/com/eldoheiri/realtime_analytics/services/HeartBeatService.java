package com.eldoheiri.realtime_analytics.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eldoheiri.realtime_analytics.dataobjects.events.ApplicationEventDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;
import com.eldoheiri.realtime_analytics.kafka.producer.MessageQueue;
import com.eldoheiri.realtime_analytics.security.idgeneration.IdentifierUtil;
import com.eldoheiri.messaging.messages.HeartBeatMessage;
import com.eldoheiri.messaging.dataobjects.Application;
import com.eldoheiri.messaging.dataobjects.ApplicationEvent;

import io.jsonwebtoken.Claims;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class HeartBeatService {

    @Autowired
    private IdentifierUtil identifierUtil;

    @Autowired
    private MessageQueue<HeartBeatMessage> heartBeatMessageQueue;

    public void send(HeartBeatMessage message) throws HeartBeatException {
        try {
            heartBeatMessageQueue.send(message);
        } catch (MessageQueueException e) {
            e.printStackTrace();
            throw new HeartBeatException(e);
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

        List<ApplicationEvent> applicationEvents = new ArrayList<>();
        for (ApplicationEventDTO eventDTO : sessionHeartBeat.getEvents()) {
            ApplicationEvent applicationEvent = new ApplicationEvent();
            applicationEvent.setTimestamp(eventDTO.getTimestamp().getTime());
            applicationEvent.setType(eventDTO.getType());
            applicationEvent.setAttributes(eventDTO.getAttributes());
            applicationEvents.add(applicationEvent);
        }
        HeartBeatMessage heartBeatMessage = new HeartBeatMessage();
        heartBeatMessage.setSessionId(sessionId);
        heartBeatMessage.setApplicationId(applicationId);
        heartBeatMessage.setDeviceId(deviceId);
        heartBeatMessage.setEvents(applicationEvents);
        send(heartBeatMessage);
    }
}
