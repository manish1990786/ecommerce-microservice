package com.manish.ecommerce.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentRequestsTopic() {
        return TopicBuilder.name("orders").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paymentStatusTopic() {
        return TopicBuilder.name("payment-status").partitions(1).replicas(1).build();
    }
}
