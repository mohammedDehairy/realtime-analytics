package com.eldoheiri.realtime_analytics.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Bean
    SecurityFilterChain apiKeyfilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/v*/{applicationId}/session", "/api/v*/{applicationId}/device")
        .addFilterBefore(new APIKeyFilter(), UsernamePasswordAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }

    @Bean
    SecurityFilterChain sessionJwtFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/v*/{applicationId}/session/{sessionId}/heartBeat")
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