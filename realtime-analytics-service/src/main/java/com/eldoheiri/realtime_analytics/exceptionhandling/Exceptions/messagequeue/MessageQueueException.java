package com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue;

public class MessageQueueException extends Exception {
    public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageQueueException(String message) {
        super(message);
    }

    public MessageQueueException() {
        super();
    }
}
