package com.eldoheiri.messagequeue.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public final class JsonDeserializer<T> implements Deserializer<T> {
    private final ObjectMapper objectMapper =  new ObjectMapper();
    private final Class<T> clazz;

    public JsonDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(new String(data, "UTF-8"), clazz);
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new SerializationException(String.format("Error when deserializing byte[] to '%s'", clazz.getName()), e);
        }
    }
}
