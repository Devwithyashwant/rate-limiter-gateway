package com.yashwant.rate_limiter_gateway;

import java.util.concurrent.ConcurrentLinkedDeque;


public class SlidingWindowCounter implements RateLimiterStrategy {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final ConcurrentLinkedDeque<Long> requestTimeStamps =  new ConcurrentLinkedDeque<>();


    public SlidingWindowCounter(int maxRequests , long windowSizeMillis){
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;

    }


    public synchronized boolean allowRequest(){
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMillis;


        while(!requestTimeStamps.isEmpty() && requestTimeStamps.peekFirst() < windowStart){
            requestTimeStamps.pollFirst();

        }
        if(requestTimeStamps.size()<maxRequests){
            requestTimeStamps.addLast(now);
            return true;
        }
        return false;
    }
}
