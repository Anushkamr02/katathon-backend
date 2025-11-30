package com.telegrambot.backend.runner;

import com.telegrambot.backend.service.TelegramBotListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Component that runs the Telegram Bot in Long Polling mode.
 * * It implements CommandLineRunner so that it runs automatically after the
 * Spring application context is initialized. It is conditionally enabled
 * via the 'telegram.polling.enabled' property.
 */
@Component
@ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true", matchIfMissing = true)
public class TelegramPollingRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TelegramPollingRunner.class);

    private final TelegramBotListener telegramBotListener;

    // Configuration property to determine if polling mode should be used
    @Value("${telegram.polling.enabled:false}")
    private boolean pollingEnabled;

    // Configuration property to determine if bot is enabled
    @Value("${telegram.bot.enabled:true}")
    private boolean botEnabled;

    // Injected property for the base webhook URL (required for listener registration)
    @Value("${telegram.webhook.url:}")
    private String webhookUrl;

    public TelegramPollingRunner(TelegramBotListener telegramBotListener) {
        this.telegramBotListener = telegramBotListener;
    }

    /**
     * This method runs once the Spring Boot application starts.
     * It conditionally registers the bot for polling if 'pollingEnabled' is true.
     * * @param args Command line arguments.
     */
    @Override
    public void run(String... args) {
        if (pollingEnabled) {
            logger.warn("Running Telegram Bot in LONG POLLING MODE. Webhook endpoint /telegram/webhook will be inactive.");
            try {
                // Register the bot for long polling
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(telegramBotListener);
                logger.info("Telegram Bot registered successfully for long polling.");
                
            }catch (TelegramApiException e) {
                logger.error("Failed to register Telegram Bot for long polling: {}", e.getMessage(), e);
            }
        } else {
            logger.info("Running Telegram Bot in WEBHOOK MODE. Polling runner is skipped.");
            // In Webhook mode, the bot is registered via TelegramBotConfig
            // and updates are received via TelegramWebhookController.
        }
    }
}