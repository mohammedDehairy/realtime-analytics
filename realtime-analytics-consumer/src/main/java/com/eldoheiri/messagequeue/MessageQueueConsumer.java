package com.eldoheiri.messagequeue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import com.eldoheiri.messaging.dataobjects.ApplicationEvent;
import com.eldoheiri.messagequeue.serialization.JsonSerde;
import com.eldoheiri.messaging.messages.ApplicationLongKey;
import com.eldoheiri.messaging.messages.ApplicationStringKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import com.eldoheiri.messaging.messages.HeartBeatMessage;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;

public class MessageQueueConsumer {

    private static final Gauge dailyAggregatedMetrics = Gauge.build()
            .name("daily_aggregated_metrics")
            .help("Daily aggregated metrics like average sessions per device, total sessions and active devices")
            .labelNames("topic", "window_start_time")
            .register();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        String topic = System.getenv("KAFKA_TOPIC");
        createTopicIfDoesntExist(topic);
        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<String, String> inputStream = streamBuilder.stream(topic);
        calculateAverageSessionsPerDevicePerApplicationPerDay(
                inputStream)
                .foreach((key, value) -> {
                    System.out.println(key.toString() + " " + value);
                    dailyAggregatedMetrics.labels("average_daily_sessions_per_device_" + key.applicationId(),
                            Instant.ofEpochMilli(key.timestampe()).toString()).set(value);
                });
        calculateTotalSessionsPerDayPerApplication(
                inputStream)
                .foreach((key, value) -> {
                    System.out.println(key.toString() + " " + value);
                    dailyAggregatedMetrics.labels("total_daily_sessions_" + key.applicationId(),
                            Instant.ofEpochMilli(key.timestampe()).toString()).set(value);
                });
        calculateDailyActiveDevices(inputStream)
                .foreach((key, value) -> {
                    System.out.println(key.toString() + " " + value);
                    dailyAggregatedMetrics.labels("daily_active_devices_" + key.applicationId(),
                            Instant.ofEpochMilli(key.timestampe()).toString()).set(value);
                });
        calculateDailyCountOfEventType(inputStream)
                .foreach((key, value) -> {
                    System.out.println(key.toString() + " " + value);
                    dailyAggregatedMetrics.labels("daily_" + key.eventType() + "_count_" + key.applicationId(),
                            Instant.ofEpochMilli(key.timestampe()).toString()).set(value);
                });
        // KStream<ApplicationLongKey, DailyMetricsRecord> partialStream1 =
        // averageDailySessionsPerDeviceStream.join(
        // totalDailySessionsStream,
        // (applicationKey, averageDailySessions, totalDailySessions) -> {
        // DailyMetricsRecord record = new
        // DailyMetricsRecord(applicationKey.timestampe(),
        // applicationKey.applicationId());
        // record.setAverageSessionsPerDevice(averageDailySessions);
        // record.setTotalSessions(totalDailySessions);
        // return record;
        // },
        // JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofDays(1)));
        // KStream<ApplicationLongKey, DailyMetricsRecord> finalStream =
        // partialStream1.join(
        // activeDevicesStream,
        // (applicationKey, dailyRecord, activeDevices) -> {
        // dailyRecord.setActiveDevices(activeDevices);
        // return dailyRecord;
        // },
        // JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofDays(1)));

        try (KafkaStreams streams = new KafkaStreams(streamBuilder.build(), createConfig());
                // Start Prometheus HTTP server
                HTTPServer server = new HTTPServer(9292)) {
            streams.start();
        }
    }

    private static void createTopicIfDoesntExist(String topicName) throws InterruptedException, ExecutionException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));

        try (AdminClient adminClient = AdminClient.create(properties)) {
            if (adminClient.listTopics().names().get().contains(topicName)) {
                return;
            }

            NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static KStream<ApplicationLongKey, Integer> calculateAverageSessionsPerDevicePerApplicationPerDay(
            KStream<String, String> inputStream) {
        return inputStream.map((key, value) -> {
            HeartBeatMessage message = parse(value);
            String deviceId = message.getDeviceId();
            String applicationId = message.getApplicationId();
            String sessionId = message.getSessionId();
            long timestamp = message.getTimestamp();
            ApplicationStringKey newKey = new ApplicationStringKey(applicationId, "session",
                    String.format("%s|%s", deviceId, Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
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
                        Materialized.with(new JsonSerde<>(ApplicationStringKey.class), new JsonSerde<>(HashMap.class)))
                .mapValues(map -> {
                    int sum = 0;
                    for (var key : map.keySet()) {
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
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(),
                            windowedKey.key().eventType(),
                            windowedKey.window().startTime().toEpochMilli());
                    return new KeyValue<>(key, avgSessions);
                });
    }

    private static KStream<ApplicationLongKey, Integer> calculateTotalSessionsPerDayPerApplication(
            KStream<String, String> inputStream) {
        return inputStream.map((key, value) -> {
            HeartBeatMessage message = parse(value);
            String applicationId = message.getApplicationId();
            String sessionId = message.getSessionId();
            long timestamp = message.getTimestamp();
            ApplicationStringKey newKey = new ApplicationStringKey(applicationId, "session",
                    String.format("%s", Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
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
                        Materialized.with(new JsonSerde<>(ApplicationStringKey.class), new JsonSerde<>(HashSet.class)))
                .mapValues(sessionsSet -> sessionsSet.size())
                .toStream()
                .map((windowedKey, uniqueSessionsCount) -> {
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(),
                            windowedKey.key().eventType(),
                            windowedKey.window().startTime().toEpochMilli());
                    return new KeyValue<>(key, uniqueSessionsCount);
                });
    }

    private static KStream<ApplicationLongKey, Integer> calculateDailyActiveDevices(
            KStream<String, String> inputStream) {
        return inputStream.map((key, value) -> {
            HeartBeatMessage message = parse(value);
            String deviceId = message.getDeviceId();
            String applicationId = message.getApplicationId();
            long timestamp = message.getTimestamp();
            ApplicationStringKey newKey = new ApplicationStringKey(applicationId, "session",
                    String.format("%s", Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
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
                        Materialized.with(new JsonSerde<>(ApplicationStringKey.class), new JsonSerde<>(HashSet.class)))
                .mapValues(devicesSet -> devicesSet.size())
                .toStream()
                .map((windowedKey, uniqueDevices) -> {
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(),
                            windowedKey.key().eventType(),
                            windowedKey.window().startTime().toEpochMilli());
                    return new KeyValue<>(key, uniqueDevices);
                });
    }

    private static KStream<ApplicationLongKey, Integer> calculateDailyCountOfEventType(
            KStream<String, String> inputStream) {
        return inputStream.flatMap((key, value) -> {
            List<KeyValue<ApplicationStringKey, ApplicationEvent>> result = new ArrayList<>();
            HeartBeatMessage message = parse(value);
            List<ApplicationEvent> events = message.getEvents();
            for (ApplicationEvent event : events) {
                String applicationId = message.getApplicationId();
                String eventType = event.getType();
                long timestamp = event.getTimestamp();
                ApplicationStringKey newKey = new ApplicationStringKey(applicationId, eventType, String.format("%s",
                        Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.DAYS)));
                result.add(new KeyValue<>(newKey, event));
            }
            return result;
        })
                .groupByKey(Grouped.with(new JsonSerde<>(ApplicationStringKey.class),
                        new JsonSerde<>(ApplicationEvent.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                .count()
                .toStream()
                .map((windowedKey, eventCount) -> {
                    ApplicationLongKey key = new ApplicationLongKey(windowedKey.key().applicationId(),
                            windowedKey.key().eventType(),
                            windowedKey.window().startTime().toEpochMilli());
                    return new KeyValue<>(key, eventCount.intValue());
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
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return properties;
    }

}
