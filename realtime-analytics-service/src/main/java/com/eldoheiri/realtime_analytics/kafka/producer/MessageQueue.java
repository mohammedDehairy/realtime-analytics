package com.eldoheiri.realtime_analytics.kafka.producer;

import java.util.Properties;
import java.util.List;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;
import com.eldoheiri.realtime_analytics.messaging.AnalyticsEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MessageQueue<MessageType> implements AnalyticsEventPublisher<MessageType> {
    private final KafkaProducer<String, String> producer;
    private final String topic;

    public MessageQueue(String topic) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        properties.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
        properties.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
        properties.put("acks", "all");

        this.producer = new KafkaProducer<>(properties);
        this.topic = topic;
    }

    public void send(List<MessageType> messages) throws MessageQueueException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (MessageType message : messages) {
                String jsonString = objectMapper.writeValueAsString(message);
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonString);
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                        return;
                    }

                    System.out.println("Record sent to partition " + metadata.partition() + " with offset " + metadata.offset());
                });
            }
        } catch (JsonProcessingException e) {
            throw new MessageQueueException("Error while sending message to Kafka", e);
        }
    }
}
