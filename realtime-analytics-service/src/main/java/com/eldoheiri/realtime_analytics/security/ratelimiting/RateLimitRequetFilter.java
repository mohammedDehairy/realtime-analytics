package com.eldoheiri.realtime_analytics.security.ratelimiting;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eldoheiri.realtime_analytics.dataobjects.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimitRequetFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!TokenBucketRateLimiter.check("real-time-analytics")) {
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(1));
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(HttpStatus.TOO_MANY_REQUESTS.value());
            errorResponse.setMessage("You have exhausted your API Request Quota");
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), convertyObjectToJson(errorResponse));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String convertyObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
    
}
