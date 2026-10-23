package com.yashwant.rate_limiter_gateway;

public interface RateLimiterStrategy {
    boolean allowRequest();
}