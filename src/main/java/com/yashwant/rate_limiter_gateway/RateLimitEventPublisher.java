package com.yashwant.rate_limiter_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class RateLimitEventPublisher {

    private static final String TOPIC = "rate-limit-exceeded";

    @Autowired
    private KafkaTemplate<String , String> kafkaTemplate;

    public void publishRateLimitExceeded(String userId){
        String message = "User " + userId + " exceeded rate limit at " + System.currentTimeMillis() + ".";
        kafkaTemplate.send(TOPIC , userId, message);
    }
}
