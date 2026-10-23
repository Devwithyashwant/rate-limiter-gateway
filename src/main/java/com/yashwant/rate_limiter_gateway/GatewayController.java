package com.yashwant.rate_limiter_gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GatewayController {

    @GetMapping("/api/resource")
    public String getResource(@RequestParam String userId){
        return "Hello" + userId + ", here is your resource!";

    }


}
