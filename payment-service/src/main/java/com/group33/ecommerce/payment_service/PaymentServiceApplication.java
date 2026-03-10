package com.group33.ecommerce.payment_service;

import com.group33.ecommerce.payment_service.config.RazorpayConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RazorpayConfigProperties.class)
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner printAppInfo(@Value("${info.app.name:NOT_LOADED}") String appName) {
		return args -> System.out.println("INFO app.name loaded: " + appName);
	}
}
