package com.eldoheiri.realtime_analytics.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eldoheiri.realtime_analytics.dataobjects.events.ApplicationEventDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;
import com.eldoheiri.realtime_analytics.messaging.AnalyticsEventPublisher;
import com.eldoheiri.realtime_analytics.security.idgeneration.IdentifierUtil;
import com.eldoheiri.realtime_analytics.dataobjects.Application;
import io.jsonwebtoken.Claims;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartBeatService {

    @Autowired
    private IdentifierUtil identifierUtil;

    @Autowired
    private AnalyticsEventPublisher<Map<String, Object>> heartBeatMessageQueue;

    public void send(List<Map<String, Object>> messages) throws MessageQueueException {
        try {
            heartBeatMessageQueue.send(messages);
        } catch (MessageQueueException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void processAuthenticatedHeartbeat(HeartBeatDTO sessionHeartBeat, String pathSessionId, String pathApplicationId)
            throws HeartBeatException, InvalidKeyException, NoSuchAlgorithmException, MessageQueueException {
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
            throws HeartBeatException, InvalidKeyException, NoSuchAlgorithmException, MessageQueueException {
        processTrustedHeartbeat(sessionHeartBeat, sessionId, applicationId, deviceId);
    }

    private void processTrustedHeartbeat(HeartBeatDTO sessionHeartBeat, String sessionId, String applicationId, String deviceId) throws HeartBeatException, InvalidKeyException, NoSuchAlgorithmException, MessageQueueException {
        if (Application.fromId(applicationId) == null) {
            throw new HeartBeatException("Application not found");
        }

        if (!identifierUtil.validateIdentifier(applicationId, deviceId)) {
            throw new HeartBeatException("Invalid device id");
        }

        if (!identifierUtil.validateIdentifier(deviceId, sessionId)) {
            throw new HeartBeatException("Invalid session id");
        }

        List<Map<String, Object>> messages = new ArrayList<>();

        for (ApplicationEventDTO eventDTO : sessionHeartBeat.getEvents()) {
            Map<String, Object> eventMessage = new HashMap<>();
            eventMessage.put("timestamp", eventDTO.getTimestamp().getTime());
            eventMessage.put("session_id", sessionId);
            eventMessage.put("application_id", applicationId);
            eventMessage.put("device_id", deviceId);
            eventMessage.put("event_type", eventDTO.getType());
            eventMessage.put("application_version", sessionHeartBeat.getApplicationVersion());
            eventMessage.put("release_channel", sessionHeartBeat.getReleaseChannel());
            eventMessage.put("platform", sessionHeartBeat.getPlatform());
            eventMessage.put("os_version", sessionHeartBeat.getOsVersion());
            eventMessage.put("device_model", sessionHeartBeat.getDeviceModel());
            eventMessage.put("device_brand", sessionHeartBeat.getDeviceBrand());
            eventMessage.put("device_locale", sessionHeartBeat.getDeviceLocale());
            eventMessage.put("additional_attributes", buildAdditionalAttributes(eventDTO));
            messages.add(eventMessage);
        }

        if (!messages.isEmpty()) {
            send(messages);
        }
    }

    private Map<String, String> buildAdditionalAttributes(ApplicationEventDTO eventDTO) {
        Map<String, String> additionalAttributes = eventDTO.getAttributes();
        if (additionalAttributes == null || additionalAttributes.isEmpty()) {
            return Map.of();
        }

        return additionalAttributes;
    }
}
