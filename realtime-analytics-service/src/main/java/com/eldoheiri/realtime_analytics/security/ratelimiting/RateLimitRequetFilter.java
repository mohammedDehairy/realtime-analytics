package com.eldoheiri.realtime_analytics.security.ratelimiting;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eldoheiri.realtime_analytics.dataobjects.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimitRequetFilter extends OncePerRequestFilter {
    private static final long CAPACITY = 60;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Bucket bucket = buckets.computeIfAbsent(resolveKey(request), ignored -> createBucket());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            long retryAfterSeconds = Math.max(1L, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(HttpStatus.TOO_MANY_REQUESTS.value());
            errorResponse.setMessage("You have exhausted your API Request Quota");
            response.addHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(retryAfterSeconds));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(convertyObjectToJson(errorResponse));
            return;
        }

        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        filterChain.doFilter(request, response);
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder().capacity(CAPACITY).refillGreedy(CAPACITY, REFILL_PERIOD).build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveKey(HttpServletRequest request) {
        String apiKey = request.getHeader("x-api-key");
        if (apiKey != null && !apiKey.isBlank()) {
            return "api-key:" + apiKey;
        }

        return "ip:" + request.getRemoteAddr();
    }

    private String convertyObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return OBJECT_MAPPER.writeValueAsString(object);
    }
    
}
