package com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions;

import java.io.Serial;

public class DeviceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DeviceException(Throwable cause) {
        super(cause);
    }

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceException() {
        super();
    }
}
