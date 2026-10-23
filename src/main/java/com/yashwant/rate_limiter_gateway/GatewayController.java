package com.yashwant.rate_limiter_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GatewayController {

    @Autowired
    private RateLimiterService rateLimiterService;

    @GetMapping("/api/resource")
    public ResponseEntity<String> getResource(@RequestParam String userId){
        if (!rateLimiterService.isAllowed(userId)){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate Limit Exceeded for user: " + userId);

        }
        return ResponseEntity.ok("Hello " + userId + ", Here is your resource!");


    }


}
