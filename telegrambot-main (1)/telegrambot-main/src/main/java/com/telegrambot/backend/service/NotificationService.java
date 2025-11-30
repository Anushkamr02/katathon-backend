package com.telegrambot.backend.service;

import com.telegrambot.backend.dto.NotificationDto;
import com.telegrambot.backend.entity.User;
import com.telegrambot.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for processing and delivering notifications received
 * from the external SafeWalk backend (e.g., deviation alerts, SOS confirmations)
 * to the corresponding Telegram user.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final UserRepository userRepository;
    private final TelegramBotService telegramBotService;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository for accessing user data.
     * @param telegramBotService Core service for sending messages via Telegram API.
     */
    public NotificationService(UserRepository userRepository, TelegramBotService telegramBotService) {
        this.userRepository = userRepository;
        this.telegramBotService = telegramBotService;
    }

    /**
     * Sends a notification message to the target user identified in the DTO.
     * This is the entry point for all incoming webhooks from the SafeWalk Core system.
     *
     * @param notificationDto The DTO containing the user ID, message, and type.
     */
    public void sendToUser(NotificationDto notificationDto) {
        Long telegramChatId = notificationDto.getUserId();
        String message = notificationDto.getMessage();
        String type = notificationDto.getType();

        // 1. Validate the user exists in our local database
        Optional<User> userOpt = userRepository.findByTelegramId(String.valueOf(telegramChatId));

        if (userOpt.isEmpty()) {
            logger.warn("Received notification for unknown user ID: {}", telegramChatId);
            return;
        }

        User user = userOpt.get();
        String formattedMessage;

        // 2. Format the message based on the notification type
        switch (type.toUpperCase()) {
            case "DEVIATION":
                formattedMessage = String.format("\uD83D\uDEA8 *ALERT: Route Deviation Detected!*\n\n%s", message);
                break;
            case "SOS_CONFIRMATION":
                formattedMessage = String.format("\uD83C\uDF0D *SOS Triggered and Contacts Notified.*\n\n%s", message);
                break;
            case "CHECK_IN_REMINDER":
                formattedMessage = String.format("\u23F0 *Check-in Reminder*\n\n%s", message);
                break;
            case "TRIP_ENDED":
                formattedMessage = String.format("\u2705 *Trip Ended Successfully*\n\n%s", message);
                break;
            default:
                formattedMessage = message; // Default to plain message
                break;
        }

        logger.info("Sending {} notification to user {}.", type, user.getName());

        // 3. Use the TelegramBotService to send the message
        boolean success = telegramBotService.sendMessage(telegramChatId, formattedMessage);

        if (success) {
            logger.info("Successfully delivered notification of type {} to chat ID {}", type, telegramChatId);
        } else {
            logger.error("Failed to deliver notification of type {} to chat ID {}", type, telegramChatId);
        }
    }
}