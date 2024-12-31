package com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions;

import java.io.Serial;

public class DeviceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DeviceException(Throwable cause) {
        super(cause);
    }

    public DeviceException() {
        super();
    }
}
