package com.eldoheiri.realtime_analytics.messaging;

import java.util.List;

import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;

public interface AnalyticsEventPublisher<MessageType> {
    void send(List<MessageType> messages) throws MessageQueueException;
}
