package com.loopin.trust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservice 2: Trust & Safety Verification Service.
 * Runs independently on port 8081 with its own H2 database and JPA layer.
 */
@SpringBootApplication
public class TrustServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrustServiceApplication.class, args);
    }
}
