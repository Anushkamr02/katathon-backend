package com.telegrambot.backend.config;

import com.telegrambot.backend.service.ConversationService;
import com.telegrambot.backend.service.TelegramBotListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Configuration class for Telegram Bot setup.
 * This class configures the Telegram Bot beans conditionally based on
 * properties.
 */
@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.enabled:true}")
    private boolean botEnabled;

    /**
     * Creates the TelegramBotListener bean if the bot is enabled.
     */
    @Bean
    @ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true", matchIfMissing = true)
    public TelegramBotListener telegramBotListener(ConversationService conversationService) {
        return new TelegramBotListener(null, botUsername, botToken, conversationService);
    }

    /**
     * Creates the TelegramBotsApi bean if the bot is enabled and polling is
     * disabled (webhook mode).
     * This bean should NOT be created when polling is enabled, as
     * TelegramPollingRunner handles registration.
     */
    @Bean
    @ConditionalOnProperty(name = "telegram.polling.enabled", havingValue = "false", matchIfMissing = false)
    public TelegramBotsApi telegramBotsApi(TelegramBotListener telegramBotListener) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBotListener);
        return botsApi;
    }

}
