package com.group33.ecommerce.payment_service.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RazorpayConfigLogger {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayConfigLogger.class);

    private final RazorpayConfigProperties razorpayConfig;

    public RazorpayConfigLogger(RazorpayConfigProperties razorpayConfig) {
        this.razorpayConfig = razorpayConfig;
    }

    @PostConstruct
    public void logRazorpayConfig() {
        logger.info("Razorpay Key ID: {}", razorpayConfig.getKeyId());
        logger.info("Razorpay Key Secret: {}", razorpayConfig.getKeySecret());
        logger.info("Razorpay Webhook Secret: {}", razorpayConfig.getWebhookSecret());
    }
}
