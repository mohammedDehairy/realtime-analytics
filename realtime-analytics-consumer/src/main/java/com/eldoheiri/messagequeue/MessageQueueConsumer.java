package com.eldoheiri.messagequeue;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Properties;

import com.eldoheiri.messagequeue.serialization.JsonSerde;
import com.eldoheiri.messaging.messages.ApplicationLongKey;
import com.eldoheiri.messaging.messages.ApplicationStringKey;
import com.eldoheiri.messaging.messages.DailyMetricsRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Materialized;
import com.eldoheiri.messaging.messages.HeartBeatMessage;

public class MessageQueueConsumer {

    public static void main( String[] args ){
        String topic = System.getenv("KAFKA_TOPIC");
        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<String, String> inputStream = streamBuilder.stream(topic);
        KStream<ApplicationLongKey, Integer> averageDailySessionsPerDeviceStream = calculateAverageSessionsPerDevicePerApplicationPerDay(inputStream);
        KStream<ApplicationLongKey, Integer> totalDailySessionsStream = calculateTotalSessionsPerDayPerApplication(inputStream);
        KStream<ApplicationLongKey, Integer> activeDevicesStream = calculateDailyActiveDevices(inputStream);
        KStream<ApplicationLongKey, DailyMetricsRecord> partialStream1 = averageDailySessionsPerDeviceStream.join(
                totalDailySessionsStream,
                (applicationKey, averageDailySessions, totalDailySessions) -> {
                    DailyMetricsRecord record = new DailyMetricsRecord(applicationKey.key(), applicationKey.applicationId());
                    record.setAverageSessionsPerDevice(averageDailySessions);
                    record.setTotalSessions(totalDailySessions);
                    return record;
                },
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofDays(1))
                );
        KStream<ApplicationLongKey, DailyMetricsRecord> finalStream = partialStream1.join(
                activeDevicesStream,
                (applicationKey, dailyRecord, activeDevices) -> {
                    dailyRecord.setActiveDevices(activeDevices);
                    return dailyRecord;
                },
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofDays(1))
        );

        finalStream.foreach((timestamp, dailyRecord) -> {
            System.out.println(dailyRecord.toString());
        });


        try (KafkaStreams streams = new KafkaStreams(streamBuilder.build(), createConfig())) {
            streams.start();
        }
    }

    public static KStream<ApplicationLongKey, Integer> calculateAverageSessionsPerDevicePerApplicationPerDay(KStream<String, String> inputStream) {
        return inputStream.map((key, value) -> {
                    HeartBeatMessage message = parse(value);
                    String deviceId = message.getDeviceId();
                    String applicationId = message.getApplicationId();
                    String sessionId = message.getSessionId();
                    long timestamp = message.getTimestamp();
                    ApplicationStringKey newKey = new ApplicationStringKey(applicationId, String.format("%s|%s", deviceId, Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
                    return new KeyValue<>(newKey, sessionId);
        })
        .groupByKey(Grouped.with(new JsonSerde<>(ApplicationStringKey.class), Serdes.String()))
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                .aggregate(
                        HashMap::new,
                        (key, sessionId, map) -> {
                            HashSet<String> sessionIds;
                            if (map.get(key) instanceof HashSet sessions) {
                                sessionIds = sessions;
                            } else {
                                sessionIds = new HashSet<>();
                            }
                            sessionIds.add(sessionId);
                            map.put(key, sessionIds);
                            return map;
                        },
                        Materialized.with(new JsonSerde<>(ApplicationStringKey.class), new JsonSerde<>(HashMap.class))
                )
                .mapValues(map -> {
                    int sum = 0;
                    for (var key: map.keySet()) {
                        var value = map.get(key);
                        if (!(value instanceof HashSet set)) {
                            continue;
                        }
                        sum += set.size();
                    }
                    return sum / map.size();
                })
                .toStream()
                .map((windowedKey, avgSessions) -> {
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(), windowedKey.window().startTime().toEpochMilli()) ;
                    return new KeyValue<>(key, avgSessions);
                });
    }

    public static KStream<ApplicationLongKey, Integer> calculateTotalSessionsPerDayPerApplication(KStream<String, String> inputStream) {
        return inputStream.map((key, value) -> {
            HeartBeatMessage message = parse(value);
            String applicationId = message.getApplicationId();
            String sessionId = message.getSessionId();
            long timestamp = message.getTimestamp();
            ApplicationStringKey newKey = new ApplicationStringKey(applicationId, String.format("%s", Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
            return new KeyValue<>(newKey, sessionId);
        })
                .groupByKey(Grouped.with(new JsonSerde<>(ApplicationStringKey.class), Serdes.String()))
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                .aggregate(
                        HashSet::new,
                        (key, sessionId, sessionsSet) -> {
                            sessionsSet.add(sessionId);
                            return sessionsSet;
                        },
                        Materialized.with(new JsonSerde<>(ApplicationStringKey.class), new JsonSerde<>(HashSet.class))
                )
                .mapValues(sessionsSet -> sessionsSet.size())
                .toStream()
                .map((windowedKey, uniqueSessionsCount) -> {
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(), windowedKey.window().startTime().toEpochMilli()) ;
                    return new KeyValue<>(key, uniqueSessionsCount);
                });
               // .to("total_sessions_per_application_per_day");
    }

    public static KStream<ApplicationLongKey, Integer> calculateDailyActiveDevices(KStream<String, String> inputStream) {
        return inputStream.map((key, value) -> {
            HeartBeatMessage message = parse(value);
            String deviceId = message.getDeviceId();
            String applicationId = message.getApplicationId();
            long timestamp = message.getTimestamp();
            ApplicationStringKey newKey = new ApplicationStringKey(applicationId, String.format("%s", Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
            return new KeyValue<>(newKey, deviceId);
        })
                .groupByKey(Grouped.with(new JsonSerde<>(ApplicationStringKey.class), Serdes.String()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                .aggregate(
                        HashSet::new,
                        (key, deviceId, devicesSet) -> {
                            devicesSet.add(deviceId);
                            return devicesSet;
                        },
                        Materialized.with(new JsonSerde<>(ApplicationStringKey.class), new JsonSerde<>(HashSet.class))
                )
                .mapValues(devicesSet -> devicesSet.size())
                .toStream()
                .map((windowedKey, uniqueDevices) -> {
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(), windowedKey.window().startTime().toEpochMilli()) ;
                    return new KeyValue<>(key, uniqueDevices);
                });
    }

    private static HeartBeatMessage parse(String json) {
        HeartBeatMessage message = null;
        try {
            return new ObjectMapper().readValue(json, HeartBeatMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties createConfig() {
        String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String groupId = System.getenv("KAFKA_GROUP_ID");

        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, groupId);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

    
}
