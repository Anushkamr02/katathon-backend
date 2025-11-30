package com.telegrambot.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main entry point for the SafeWalk Telegram Bot Backend application.
 *
 * This class uses the @SpringBootApplication annotation, which is a convenience
 * annotation that adds:
 * 1. @Configuration: Tags the class as a source of bean definitions for the application context.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath
 * settings, other beans, and various property settings (e.g., configuring HikariCP,
 * Spring Data JPA, and WebMVC).
 * 3. @ComponentScan: Tells Spring to look for other components, configurations, and services
 * in the 'com.telegrambot.backend' package, allowing us to find all controllers,
 * services, and repositories we've created.
 */
@SpringBootApplication
public class TelegramBotBackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotBackendApplication.class);

    /**
     * The main method that runs the Spring Boot application.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        logger.info("Starting SafeWalk Telegram Bot Backend application...");
        
        // Run the Spring application
        ConfigurableApplicationContext context = SpringApplication.run(TelegramBotBackendApplication.class, args);
        
        logger.info("SafeWalk Telegram Bot Backend application started successfully.");
        
        // Optional: Log the active profiles for verification
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 0) {
            logger.info("No active Spring profiles detected. Running with default settings.");
        } else {
            logger.info("Active Spring profiles: {}", String.join(", ", activeProfiles));
        }
    }
}