package com.eldoheiri.realtime_analytics.services;

import com.eldoheiri.databaseaccess.dataobjects.ApplicationEvent;
import com.eldoheiri.realtime_analytics.dataobjects.events.ApplicationEventDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;

import java.util.List;
import java.util.ArrayList;

public class DataFactory {

    static ApplicationEvent createHeartBeatEvent(HeartBeatDTO dto, Integer sessionId) {
        if (dto == null) {
            return null;
        }
        var heartBeat = new ApplicationEvent();
        heartBeat.setSessionId(sessionId);
        heartBeat.setTimestamp(dto.getTimestamp());
        heartBeat.setPayload(dto.getAttributes());
        heartBeat.setType("heart_beat");
        return heartBeat;
    }

    static List<ApplicationEvent> createApplicationEvents(HeartBeatDTO heartBeat, Integer sessionId) {
        if (heartBeat == null || heartBeat.getEvents() == null) {
            return new ArrayList<>();
        }
        ArrayList<ApplicationEvent> result = new ArrayList<>();
        for (ApplicationEventDTO dto : heartBeat.getEvents()) {
            var applicationEvent = new ApplicationEvent();
            applicationEvent.setPayload(dto.getAttributes());
            applicationEvent.setSessionId(sessionId);
            applicationEvent.setTimestamp(dto.getTimestamp());
            applicationEvent.setType(dto.getType().name());
            result.add(applicationEvent);
        }
        return result;
    }
}
