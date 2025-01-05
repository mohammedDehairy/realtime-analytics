package com.eldoheiri.messaging.messages;

import java.util.Objects;

public final class TimestampedWrapper<T> {
    private final T object;
    private final long timestamp;

    public TimestampedWrapper(T object, long timestamp) {
        this.object = object;
        this.timestamp = timestamp;
    }

    public T getObject() {
        return object;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimestampedWrapper<?> that = (TimestampedWrapper<?>) o;
        return timestamp == that.timestamp && Objects.equals(object, that.object);
    }
}
