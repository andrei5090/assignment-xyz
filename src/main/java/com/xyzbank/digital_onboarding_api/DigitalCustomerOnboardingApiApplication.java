package com.xyzbank.digital_onboarding_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
// TODO: consider TTL
public class DigitalCustomerOnboardingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalCustomerOnboardingApiApplication.class, args);
    }

}
