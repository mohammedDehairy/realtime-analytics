package com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat;

import java.io.Serial;

public class HeartBeatException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public HeartBeatException(Throwable cause) {
        super(cause);
    }

    public HeartBeatException() {
        super();
    }
}
