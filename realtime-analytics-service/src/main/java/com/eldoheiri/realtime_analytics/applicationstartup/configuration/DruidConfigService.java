package com.eldoheiri.realtime_analytics.applicationstartup.configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.common.lang.NonNull;

@Service
public class DruidConfigService {

    private final WebClient webClient;
    private final String kafkaSupervisorSpec;
    private final String oneDayRetentionRule;

    public DruidConfigService(ResourceLoader resourceLoader) throws IOException {
        String druidBaseUrl = System.getenv("DRUID_ROUTER_URL");
        if (druidBaseUrl == null) {
            throw new IllegalStateException("DRUID_ROUTER_URL environment variable is not provided");
        }
        this.webClient = WebClient.builder().baseUrl(druidBaseUrl).build();
        this.kafkaSupervisorSpec = readResourceFile(resourceLoader, "classpath:kafka_supervisor_spec.json");
        this.oneDayRetentionRule = readResourceFile(resourceLoader, "classpath:realtime_analytics_data.json");
    }

    private String readResourceFile(@NonNull ResourceLoader resourceLoader, @NonNull String location) throws IOException{
        try (Reader reader = new InputStreamReader(resourceLoader.getResource(Objects.requireNonNull(location)).getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void configureSupervisor() {
        if (kafkaSupervisorSpec == null || kafkaSupervisorSpec.isEmpty()) {
            throw new IllegalStateException("kafkaSupervisorSpec is empty or null");
        }

        this.webClient.post().uri("/druid/indexer/v1/supervisor")
            .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
            .bodyValue(Objects.requireNonNull(kafkaSupervisorSpec))
            .retrieve()
            .toBodilessEntity()
            .block();
    }

    public void configureRetentionPolicy() {
        if (oneDayRetentionRule == null || oneDayRetentionRule.isEmpty()) {
            throw new IllegalStateException("oneDayRetentionRule is empty or null");
        }
        String dataSourceName = System.getenv("DATASOURCE_NAME");
        this.webClient.post().uri("/druid/coordinator/v1/rules/{dataSourceName}", dataSourceName)
        .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
        .bodyValue(Objects.requireNonNull(oneDayRetentionRule))
        .retrieve()
        .toBodilessEntity()
        .block();
    }
    
}
