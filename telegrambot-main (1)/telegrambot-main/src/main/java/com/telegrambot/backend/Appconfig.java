package com.telegrambot.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to define Spring beans.
 * This class ensures that a RestTemplate instance is available for dependency injection
 * throughout the application, specifically for the TelegramBotService.
 */
@Configuration
public class Appconfig {

    /**
     * Defines a RestTemplate bean.
     * RestTemplate is used to make synchronous REST calls (HTTP requests).
     * By annotating this method with @Bean, Spring registers the returned object
     * in its Application Context, making it available for injection.
     *
     * @return A new instance of RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}