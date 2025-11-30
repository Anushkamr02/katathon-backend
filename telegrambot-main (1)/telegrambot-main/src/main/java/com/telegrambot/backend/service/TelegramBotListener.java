package com.telegrambot.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Custom implementation of TelegramLongPollingBot.
 * This class serves as the official bot instance registered with the TelegramBotsApi.
 * It acts as the low-level communication handler, receiving updates and forwarding
 * them to the ConversationService for business logic processing.
 *
 * NOTE: It inherits TelegramLongPollingBot for convenience, but the base
 * TelegramBots library handles switching to Webhook mode if configured in TelegramBotConfig.
 */
public class TelegramBotListener extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);

    private final String botUsername;
    private final String botToken;
    private final ConversationService conversationService;

    /**
     * Constructor for initialization via the TelegramBotConfig class.
     *
     * @param options The default bot options (e.g., proxy settings).
     * @param botUsername The bot's username (e.g., SafeWalkBot).
     * @param botToken The bot's API token.
     * @param conversationService The injected service for handling user input.
     */
    @SuppressWarnings("deprecation")
    public TelegramBotListener(DefaultBotOptions options,
                               String botUsername,
                               String botToken,
                               ConversationService conversationService) {
       super(new DefaultBotOptions());

        this.botUsername = botUsername;
        this.botToken = botToken;
        this.conversationService = conversationService;
    }

    /**
     * Returns the unique username of this bot.
     * Required by TelegramLongPollingBot interface.
     * @return The bot's username.
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Returns the token of the bot.
     * Required by TelegramLongPollingBot interface.
     * @return The bot's API token.
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * The main entry point for processing incoming updates from Telegram.
     * This method is called by the TelegramBots library whenever a new message,
     * command, or interaction (like a button click or location update) is received.
     *
     * @param update The Update object from Telegram.
     */
    @Override
    public void onUpdateReceived(Update update) {
        // Forward the update object to the core business logic service.
        // The ConversationService will determine the type of update and handle it.
        logger.debug("Update received. Forwarding to ConversationService.");
        conversationService.handleUpdate(update);
    }

    /**
     * Convenience method to send a message using the Execute method.
     * This method will be used by other services (like NotificationService)
     * to send reactive or proactive messages to users.
     *
     * @param message The SendMessage object to be executed.
     * @return The sent message object or null on error.
     */
    public Message send(SendMessage message) {
        try {
            return execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to chat {}: {}", message.getChatId(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convenience method to execute any Bot API method (e.g., EditMessageText, SendPhoto, etc.).
     *
     * @param method The BotApiMethod to execute.
     * @return The result of the execution.
     */
    public Message executeMethod(BotApiMethod<Message> method) {

        try {
            return execute(method);
        } catch (TelegramApiException e) {
            logger.error("Failed to execute API method {}: {}", method.getMethod(), e.getMessage(), e);
            return null;
        }
    }

    // --- Webhook Configuration Methods (Used by TelegramBotConfig) ---

    // Fields to hold webhook configuration if needed
    private String botPath;
    private boolean isWebhook;

    public void setWebhook(boolean isWebhook) {
        this.isWebhook = isWebhook;
    }

    public void setBotPath(String botPath) {
        this.botPath = botPath;
    }

    /**
     * Overridden method to provide the bot's path for webhook mode.
     * The path should correspond to the endpoint in TelegramWebhookController.
     */
    
    public String getBotPath() {
        // This is only relevant if isWebhook is true
        return isWebhook ? botPath : "";
    }
}