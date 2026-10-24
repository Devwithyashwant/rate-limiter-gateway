package com.yashwant.rate_limiter_gateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

class TokenBucketTest{

    @Test
    void shouldAllowRequestsUpToMaxTokens(){
        TokenBucket bucket = new TokenBucket(5,0.5);

        for(int i=0;i<5;i++) {
            assertTrue(bucket.allowRequest(), "Request" + (i + 1) + " should be allowed");
        }
    }

    @Test
    void shouldRejectRequestAfterTokensExhausted(){
        TokenBucket bucket = new TokenBucket(5,0.5);


        for(int i=0;i<5;i++) {
            bucket.allowRequest();

        }

        assertFalse(bucket.allowRequest() , "6th Request should be rejected");

    }

    @Test
    void shouldRefillTokenAfterWaiting() throws InterruptedException{
        TokenBucket bucket = new TokenBucket(5,0.5);

        for(int i=0;i<5;i++) {
            bucket.allowRequest();

        }
        assertFalse(bucket.allowRequest());
        Thread.sleep(2100);

        assertTrue(bucket.allowRequest() , "Should allow 1 request after refill period");
    }

    @Test
    void shouldHandleConcurrentRequestsCorrectly() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(5, 0.0); // 5 tokens, no refill during this test

        int numberOfThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger allowedCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                if (bucket.allowRequest()) {
                    allowedCount.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await(); // wait for all threads to finish
        executor.shutdown();

        assertEquals(5, allowedCount.get(), "Exactly 5 requests should be allowed out of 20 concurrent attempts");
    }

}


