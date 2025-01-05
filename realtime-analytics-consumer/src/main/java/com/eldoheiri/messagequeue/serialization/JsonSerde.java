package com.eldoheiri.messagequeue.serialization;

import org.apache.kafka.common.serialization.Serdes;

public class JsonSerde<T> extends Serdes.WrapperSerde<T> {

    public JsonSerde(Class<T> clazz) {
        super(new JsonSerializer<T>(), new JsonDeserializer<>(clazz));
    }
}
