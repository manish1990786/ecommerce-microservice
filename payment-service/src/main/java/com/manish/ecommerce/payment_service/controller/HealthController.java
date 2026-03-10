package com.manish.ecommerce.payment_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health-check")
    public String healthCheck() {
        return "Payment Service is Running!";
    }
}
