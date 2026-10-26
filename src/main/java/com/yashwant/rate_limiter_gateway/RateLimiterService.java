package com.yashwant.rate_limiter_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class RateLimiterService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RateLimitEventPublisher eventPublisher;

    @Autowired
    private MetricsService metricsService;

    private final Map<String, RateLimiterStrategy> userLimiters = new ConcurrentHashMap<>();

    private static final int MAX_TOKENS = 5;
    private static final double REFILL_RATE = 0.5;

    private static final int MAX_REQUESTS_SLIDING = 5;
    private static final long WINDOW_SIZE_MILLIS = 10_000;

    private static final int MAX_REQUESTS_REDIS = 5;
    private static final long WINDOW_SECONDS_REDIS = 10;

    private static final String ALGORITHM = "REDIS";

    public boolean isAllowed(String userId) {
        RateLimiterStrategy limiter = userLimiters.computeIfAbsent(userId, id -> createLimiter(userId));
        boolean allowed = limiter.allowRequest();

        if (!allowed) {
            eventPublisher.publishRateLimitExceeded(userId);
            metricsService.recordBlocked();
        } else {
            metricsService.recordAllowed();
        }

        return allowed;
    }

    private RateLimiterStrategy createLimiter(String userId) {
        if (ALGORITHM.equals("SLIDING_WINDOW")) {
            return new SlidingWindowCounter(MAX_REQUESTS_SLIDING, WINDOW_SIZE_MILLIS);
        }
        if (ALGORITHM.equals("REDIS")) {
            return new RedisRateLimiter(redisTemplate, userId, MAX_REQUESTS_REDIS, WINDOW_SECONDS_REDIS);
        }
        return new TokenBucket(MAX_TOKENS, REFILL_RATE);
    }
}