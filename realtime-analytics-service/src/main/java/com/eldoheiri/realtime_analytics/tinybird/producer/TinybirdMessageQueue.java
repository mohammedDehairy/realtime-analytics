package com.eldoheiri.realtime_analytics.tinybird.producer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;
import com.eldoheiri.realtime_analytics.messaging.AnalyticsEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TinybirdMessageQueue<MessageType> implements AnalyticsEventPublisher<MessageType> {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String eventsUrl;
    private final String token;
    private final String datasourceName;

    public TinybirdMessageQueue(String eventsUrl, String token, String datasourceName) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.eventsUrl = Objects.requireNonNull(eventsUrl, "Tinybird events URL must be set");
        this.token = Objects.requireNonNull(token, "Tinybird token must be set");
        this.datasourceName = Objects.requireNonNull(datasourceName, "Tinybird datasource name must be set");
    }

    @Override
    public void send(List<MessageType> messages) throws MessageQueueException {
        try {
            StringBuilder ndjsonPayload = new StringBuilder();
            for (MessageType message : messages) {
                ndjsonPayload.append(objectMapper.writeValueAsString(message)).append('\n');
            }

            URI uri = URI.create(eventsUrl + "/v0/events?name=" + datasourceName + "&format=json");

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/x-ndjson")
                    .POST(HttpRequest.BodyPublishers.ofString(ndjsonPayload.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 300) {
                throw new MessageQueueException(
                        "Error while sending message to Tinybird: " + statusCode + " " + response.body());
            }
        } catch (JsonProcessingException e) {
            throw new MessageQueueException("Error while serializing message for Tinybird", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessageQueueException("Error while sending message to Tinybird", e);
        } catch (IOException e) {
            throw new MessageQueueException("Error while sending message to Tinybird", e);
        }
    }
}
