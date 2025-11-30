package com.telegrambot.backend.service;

import com.telegrambot.backend.entity.User;
import com.telegrambot.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.telegrambot.backend.repository.EmergencyContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate; // Required for external API calls
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.springframework.context.annotation.Lazy;

import java.util.Optional;

/**
 * Core service layer for the Telegram Bot backend.
 * Responsible for handling low-level Telegram API interactions (sending
 * messages),
 * checking user registration status, and orchestrating the high-level bot
 * commands
 * before delegation to the ConversationService.
 */
@SuppressWarnings("unused")
@Service
public class TelegramBotService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    // Placeholder for the external Telegram client (since we don't have the library
    // source)
    // In a real project, this would be a specific Telegram API client bean.
    @SuppressWarnings({ "unsaved", "unused" })
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final UserRepository userRepository;
    @SuppressWarnings("unused")
    private final EmergencyContactRepository emergencyContactRepository;

    // Inject TelegramBotListener to actually send messages
    private TelegramBotListener telegramBotListener;

    // We will inject the ConversationService here once it is created
    // private final ConversationService conversationService;

    /**
     * Constructor for dependency injection.
     * 
     * @param restTemplate               Used for making HTTP requests to the
     *                                   Telegram API.
     * @param userRepository             Repository for accessing user data.
     * @param emergencyContactRepository Repository for accessing emergency contact
     *                                   data.
     */
    public TelegramBotService(RestTemplate restTemplate,
            UserRepository userRepository,
            EmergencyContactRepository emergencyContactRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.emergencyContactRepository = emergencyContactRepository;
    }

    /**
     * Setter for TelegramBotListener (to avoid circular dependency).
     * Using @Lazy to break the circular dependency chain.
     */
    @Autowired(required = false)
    @Lazy
    public void setTelegramBotListener(TelegramBotListener telegramBotListener) {
        this.telegramBotListener = telegramBotListener;
    }

    /**
     * Sends a simple text message to a specified Telegram chat using the Telegram
     * API.
     * This is the primary outbound communication method.
     *
     * @param chatId The recipient's Telegram chat ID (Long).
     * @param text   The message content.
     * @return true if the message was successfully sent.
     */
    public boolean sendMessage(long chatId, String text) {
        try {
            if (telegramBotListener == null) {
                logger.error("TelegramBotListener not initialized! Cannot send message.");
                return false;
            }

            // Create SendMessage object
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);

            // Send using TelegramBotListener
            telegramBotListener.send(message);
            logger.debug("Message sent to chat {}: {}", chatId, text);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send message to chat {}: {}", chatId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if a user is already registered in the system based on their Telegram
     * ID.
     * 
     * @param telegramId The unique ID provided by Telegram.
     * @return true if the user is registered, false otherwise.
     */
    public boolean isUserRegistered(String telegramId) {
        return userRepository.existsByTelegramId(telegramId);
    }

    /**
     * Retrieves a User entity based on their Telegram ID.
     * 
     * @param telegramId The unique ID provided by Telegram.
     * @return An Optional containing the User.
     */
    public Optional<User> findUserByTelegramId(String telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    // --- High-Level Command Handling Stubs ---
    // These methods receive the update and decide whether to handle it directly
    // (like /help) or delegate to the stateful ConversationService.

    /**
     * Handles the initial /start command, which initiates registration or welcomes
     * a returning user.
     */
    @Transactional
    public void handleStartCommand(long chatId, String telegramId) {
        if (isUserRegistered(telegramId)) {
            logger.info("Returning user {} started the bot.", telegramId);
            sendMessage(chatId, "Welcome back to SafeWalk! What can I help you with today? Use /help for options.");
        } else {
            logger.info("New user {} started the bot. Initiating registration.", telegramId);

            // NOTE: The next line would normally call the ConversationService to start
            // state tracking.
            // Example: conversationService.startRegistration(chatId, telegramId);

            sendMessage(chatId,
                    "Welcome to SafeWalk! To get started, please register with us. We need your name and phone number. What is your full name?");
        }
    }

    /**
     * Handles the /new_trip command.
     */
    public void handleNewTripCommand(long chatId, String telegramId) {
        if (!isUserRegistered(telegramId)) {
            sendMessage(chatId, "Please register first using /start.");
            return;
        }

        // NOTE: This logic is complex and must be delegated to the ConversationService.
        // Example: conversationService.startTripSetup(chatId, telegramId);

        sendMessage(chatId,
                "Starting a new trip setup. Please provide your starting location (e.g., '123 Main St, City').");
    }

    /**
     * Handles the /help command.
     */
    public void handleHelpCommand(long chatId) {
        String helpText = """
                Welcome to SafeWalk Bot!

                *Commands:*
                /start - Register or greet a returning user.
                /new_trip - Begin setting up a monitored trip.
                /cancel - Cancel the current trip or ongoing setup process.
                /contacts - Manage your emergency contacts.
                /sos - Trigger an immediate emergency alert (only during an active trip).
                """;
        sendMessage(chatId, helpText);
    }
}