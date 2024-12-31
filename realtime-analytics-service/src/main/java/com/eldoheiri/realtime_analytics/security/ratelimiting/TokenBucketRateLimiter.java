package com.eldoheiri.realtime_analytics.security.ratelimiting;

import java.util.HashMap;
import java.util.Map;

public class TokenBucketRateLimiter implements RateLimiter {
    private long lastCheckTimeInMillis;

    private int availableTokens = BUCKET_MAX_CAPCITY;

    private static final int UNIT_OF_TIME_IN_MILLIS = 333;

    private static final int REFILL_COUNT_PER_UNIT_OF_TIME_IN_MILLIS = 1;

    private static final int BUCKET_MAX_CAPCITY = 1;

    private static final Map<String, TokenBucketRateLimiter> limiters = new  HashMap<>();

    private static final Object lockObject = new Object();

    private TokenBucketRateLimiter() {
        this.lastCheckTimeInMillis = System.currentTimeMillis();
    }

    public static TokenBucketRateLimiter getInstance(String key) {
        synchronized(lockObject) {
            var newInstance = limiters.get(key);
            if (newInstance == null) {
                newInstance = new TokenBucketRateLimiter();
                limiters.put(key, newInstance);
            }
    
            return newInstance;
        }
    }

    public static boolean check(String key) {
        var instance = TokenBucketRateLimiter.getInstance(key);
        return instance.check();
    }

    public synchronized boolean check() {
        long currentTimeMillis = System.currentTimeMillis();
        int timeElapsedSinceLastCheck = (int)(currentTimeMillis - lastCheckTimeInMillis) / UNIT_OF_TIME_IN_MILLIS;
        if (timeElapsedSinceLastCheck > 0) {
            this.availableTokens = Math.min(BUCKET_MAX_CAPCITY,
             this.availableTokens + timeElapsedSinceLastCheck * REFILL_COUNT_PER_UNIT_OF_TIME_IN_MILLIS);
            this.lastCheckTimeInMillis = currentTimeMillis;
        }
        if (this.availableTokens == 0) {
            return false;
        }
        this.availableTokens -= 1;
        return true;
    }

    synchronized public static int getAvailableTokens(String key) {
        var instance = TokenBucketRateLimiter.getInstance(key);
        return instance.availableTokens;
    }
}
