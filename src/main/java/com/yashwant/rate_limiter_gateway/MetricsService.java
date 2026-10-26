package com.yashwant.rate_limiter_gateway;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final AtomicLong allowedCount = new AtomicLong(0);
    private final AtomicLong blockedCount = new AtomicLong(0);

    public void recordAllowed() {
        allowedCount.incrementAndGet();
    }

    public void recordBlocked() {
        blockedCount.incrementAndGet();
    }

    public long getAllowedCount() {
        return allowedCount.get();
    }

    public long getBlockedCount() {
        return blockedCount.get();
    }
}