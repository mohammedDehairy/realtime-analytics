package com.eldoheiri.realtime_analytics.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private Environment environment;

    @Bean
    SecurityFilterChain apiKeyfilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/v*/{applicationId}/sessions", "/api/v*/{applicationId}/devices")
        .addFilterBefore(new APIKeyFilter(environment), UsernamePasswordAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }

    @Bean
    SecurityFilterChain sessionJwtFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/v*/{applicationId}/sessions/{sessionId}/heartBeats")
        .addFilterBefore(new SessionJwtRequestFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }

    @Bean
    SecurityFilterChain adminJwtFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/v*/{applicationId}/session/cleanup")
        .addFilterBefore(new AdminJwtRequestFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }
}
