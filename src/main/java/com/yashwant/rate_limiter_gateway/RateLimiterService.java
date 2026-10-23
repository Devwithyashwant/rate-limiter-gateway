package com.yashwant.rate_limiter_gateway;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class RateLimiterService {

    private final Map<String, RateLimiterStrategy> userLimiters = new ConcurrentHashMap<>();

    private static final int MAX_TOKENS = 5;
    private static final double REFILL_RATE = 0.5;

    private static final int MAX_REQUESTS_SLIDING = 5;
    private static final long WINDOW_SIZE_MILLIS = 10_000;

    private static final String ALGORITHM = "SLIDING_WINDOW";

    public boolean isAllowed(String userId) {
        RateLimiterStrategy limiter = userLimiters.computeIfAbsent(userId, id -> createLimiter());
        return limiter.allowRequest();
    }

    private RateLimiterStrategy createLimiter() {
        if (ALGORITHM.equals("SLIDING_WINDOW")) {
            return new SlidingWindowCounter(MAX_REQUESTS_SLIDING, WINDOW_SIZE_MILLIS);
        }
        return new TokenBucket(MAX_TOKENS, REFILL_RATE);
    }
}