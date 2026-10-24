package com.yashwant.rate_limiter_gateway;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class SlidingWindowCounterTest {
    @Test
    void shouldAllowRequestsUpToMax(){
        SlidingWindowCounter counter = new SlidingWindowCounter(5 , 10_000);

        for(int i = 0;i<5;i++){
            assertTrue(counter.allowRequest(), "Request" + (i + 1) + "should be allowed");
        }
    }
    @Test
    void shouldRejectRequesrAfterLimitReached() {
        SlidingWindowCounter counter = new SlidingWindowCounter(5, 10_000);
        for (int i = 0; i < 5; i++) {
            counter.allowRequest();

        }
        assertFalse(counter.allowRequest(), "6th Request should be rejected within the window");
    }

    @Test
    void shouldAllowRequestAfterWindowSlides() throws InterruptedException {
        SlidingWindowCounter counter = new SlidingWindowCounter(2, 2000);

        assertTrue(counter.allowRequest());
        assertTrue(counter.allowRequest());
        assertFalse(counter.allowRequest() , "3rd Request must be rejected within the window");

        Thread.sleep(2100);
        assertTrue(counter.allowRequest() , "Should allow request after window slides");

    }
}
