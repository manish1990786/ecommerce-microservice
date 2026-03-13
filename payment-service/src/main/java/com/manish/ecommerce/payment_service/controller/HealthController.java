package com.manish.ecommerce.payment_service.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "Payment Service Running";
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Payment Service");
        response.put("timestamp", Instant.now().toString());

        return response;
    }
}
