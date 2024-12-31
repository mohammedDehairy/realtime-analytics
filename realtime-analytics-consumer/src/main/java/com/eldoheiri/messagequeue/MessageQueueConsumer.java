package com.eldoheiri.messagequeue;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;

/**
 * Hello world!
 *
 */
public class MessageQueueConsumer {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    public MessageQueueConsumer() {
        this.topic = System.getenv("KAFKA_TOPIC");
        this.consumer = createConsumer();
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public static void main( String[] args ){
        System.out.println( "Hello World!" );
    }

    public void consumeMessages() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            
        }
    }

    private static KafkaConsumer<String, String> createConsumer() {
        String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String groupId = System.getenv("KAFKA_GROUP_ID");

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(properties);
    }

    
}
