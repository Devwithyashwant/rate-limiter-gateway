package com.yashwant.rate_limiter_gateway;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

public class RedisRateLimiter implements RateLimiterStrategy {

    private final StringRedisTemplate redisTemplate;
    private final String userId;
    private final int maxRequests;
    private final long windowSeconds;

    public RedisRateLimiter(StringRedisTemplate redisTemplate , String userId , int maxRequests , long windowSeconds ){
        this.redisTemplate = redisTemplate;
        this.userId = userId;
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;

    }

    @Override
    public boolean allowRequest(){
        String key = "rate_limit:" + userId;

        Long currentCount = redisTemplate.opsForValue().increment(key);

        if(currentCount != null && currentCount == 1L){
            redisTemplate.expire(key,windowSeconds , TimeUnit.SECONDS);

        }
        return currentCount != null && currentCount <= maxRequests;
    }
}
