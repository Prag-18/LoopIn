package com.loopin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the LoopIn Spring Boot Application.
 *
 * Demonstrates Spring Boot Auto-Configuration:
 * - @SpringBootApplication bundles:
 *    1. @SpringBootConfiguration (indicates this is a configuration class)
 *    2. @EnableAutoConfiguration (tells Spring Boot to start adding beans based on classpath settings)
 *    3. @ComponentScan (tells Spring to scan and register @Controller, @Service, @Repository beans)
 */
@SpringBootApplication
public class LoopInApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoopInApplication.class, args);
    }
}
