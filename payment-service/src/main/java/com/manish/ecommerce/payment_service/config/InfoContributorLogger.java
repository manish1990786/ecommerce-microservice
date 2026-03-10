package com.manish.ecommerce.payment_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InfoContributorLogger {

    @Autowired(required = false)
    private List<InfoContributor> contributors;

    @PostConstruct
    public void logInfoContributors() {
        if (contributors != null && !contributors.isEmpty()) {
            System.out.println("---- Active InfoContributors ----");
            contributors.forEach(c -> System.out.println("InfoContributor: " + c.getClass().getName()));
        } else {
            System.out.println("No InfoContributors found.");
        }
    }
}
