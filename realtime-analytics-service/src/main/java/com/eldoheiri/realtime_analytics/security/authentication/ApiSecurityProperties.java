package com.eldoheiri.realtime_analytics.security.authentication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.security")
public class ApiSecurityProperties {
    private Map<String, String> applicationKeys = new HashMap<>();

    public Map<String, String> getApplicationKeys() {
        return applicationKeys;
    }

    public void setApplicationKeys(Map<String, String> applicationKeys) {
        this.applicationKeys = applicationKeys;
    }
}
