package com.eldoheiri.messaging.messages;

import java.util.List;

import com.eldoheiri.messaging.dataobjects.ApplicationEvent;

public final class HeartBeatMessage {
    private String sessionId;
    private String applicationId;
    private String deviceId;
    private long timestamp;
    private List<ApplicationEvent> events;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<ApplicationEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ApplicationEvent> events) {
        this.events = events;
    }
}


