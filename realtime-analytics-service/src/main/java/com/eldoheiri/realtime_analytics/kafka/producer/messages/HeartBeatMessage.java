package com.eldoheiri.realtime_analytics.kafka.producer.messages;

import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;

public final class HeartBeatMessage {
    private final Integer sessionId;
    private final HeartBeatDTO heartBeat;

    public HeartBeatMessage(Integer sessionId, HeartBeatDTO heartBeat) {
        this.sessionId = sessionId;
        this.heartBeat = heartBeat;
    }

    public Integer getSessionId() {
        return sessionId;
    }
    public HeartBeatDTO getHeartBeat() {
        return heartBeat;
    }
}
