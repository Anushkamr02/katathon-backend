package com.telegrambot.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // <-- THIS IMPORT IS VITAL
import org.springframework.web.client.RestTemplate; // <-- THIS IMPORT IS VITAL

@SpringBootTest
class TelegramBotBackendApplicationTests {

    // THIS MOCK IS WHAT STOPS SPRING FROM TRYING TO CREATE THE REAL BEAN
    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        // ...
    }
}