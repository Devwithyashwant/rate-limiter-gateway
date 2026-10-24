package com.yashwant.rate_limiter_gateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

}


