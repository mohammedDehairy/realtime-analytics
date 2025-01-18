package com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.session;

import java.io.Serial;

public class SessionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SessionException(Throwable cause) {
        super(cause);
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException() {
        super();
    }
}
