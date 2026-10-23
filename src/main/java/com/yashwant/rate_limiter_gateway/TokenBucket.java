package com.yashwant.rate_limiter_gateway;

    public class TokenBucket implements RateLimiterStrategy{
    private final int maxTokens;
    private final double refillRatePerSecond;
    private double currentTokens;
    private long lastRefillTimestamp;


    public TokenBucket(int maxTokens , double refillRatePerSecond) {
        this.maxTokens = maxTokens;
        this.refillRatePerSecond = refillRatePerSecond;
        this.currentTokens = maxTokens;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public synchronized boolean allowRequest(){
        refill();
        if(currentTokens>=1) {
            currentTokens = currentTokens - 1;
            return true;
        }
        return false;
    }

    private void refill(){
        long now = System.nanoTime();
        double secondsElapsed = (now - lastRefillTimestamp)/ 1_000_000_000.0;
        double tokensToAdd = secondsElapsed * refillRatePerSecond;
        currentTokens  = Math.min(maxTokens , currentTokens + tokensToAdd);
        lastRefillTimestamp = now;



    }


}
