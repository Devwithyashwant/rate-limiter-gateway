package com.yashwant.rate_limiter_gateway;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitEventConsumer {

    private final Map<String, Integer> blockedCountPerUser = new ConcurrentHashMap<>();

    @KafkaListener(topics = "rate-limit-exceeded", groupId = "rate-limiter-group")
    public void consume(String message) {
        System.out.println("Received Kafka event: " + message);

        String userId = extractUserId(message);
        blockedCountPerUser.merge(userId, 1, Integer::sum);

        System.out.println("Total blocked attempts for " + userId + ": " + blockedCountPerUser.get(userId));
    }

    private String extractUserId(String message) {
        // message format: "User <userId> exceeded rate limit at <timestamp>"
        String[] parts = message.split(" ");
        return parts.length > 1 ? parts[1] : "unknown";
    }
}