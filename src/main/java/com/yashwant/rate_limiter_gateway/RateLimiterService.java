package com.yashwant.rate_limiter_gateway;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class RateLimiterService {

    private final Map<String , TokenBucket> userBuckets = new ConcurrentHashMap<>();
    private static final int MAX_TOKENS = 5;
    private static final double REFILL_RATE = 0.5;


    public boolean isAllowed(String userId){
       TokenBucket bucket = userBuckets.computeIfAbsent(userId,
               id -> new TokenBucket(MAX_TOKENS , REFILL_RATE));
        return bucket.allowRequest();


    }


}
