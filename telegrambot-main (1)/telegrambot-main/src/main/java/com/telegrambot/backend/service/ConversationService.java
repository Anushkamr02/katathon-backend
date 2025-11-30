package com.telegrambot.backend.service;

import com.telegrambot.backend.entity.User;
import com.telegrambot.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for managing multi-step conversation states with users.
 * This handles registration, trip setup, contact management, and other
 * non-single-command flows.
 */
@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    // Map to store the current conversation step for each user (Telegram Chat ID)
    private final Map<Long, ConversationState> conversationStates = new HashMap<>();

    private final UserRepository userRepository;
    private final TelegramBotService telegramBotService;

    // --- Enumerations for State Management ---

    /**
     * Defines the possible stages in any conversation flow.
     */
    public enum ConversationState {
        IDLE, // No active conversation (default)
        REGISTER_NAME, // Waiting for user's name during registration
        REGISTER_PHONE, // Waiting for user's phone number
        REGISTER_GENDER, // Waiting for user's gender
        TRIP_WAIT_SOURCE, // Waiting for starting location
        TRIP_WAIT_DESTINATION, // Waiting for destination location
        TRIP_WAIT_CONFIRMATION, // Waiting for final trip confirmation
        CONTACT_WAIT_NAME, // Waiting for new contact name
        CONTACT_WAIT_PHONE // Waiting for new contact phone
    }

    // --- Constructor ---

    public ConversationService(UserRepository userRepository, TelegramBotService telegramBotService) {
        this.userRepository = userRepository;
        this.telegramBotService = telegramBotService;
    }

    // --- Main Update Handler ---

    /**
     * Entry point for processing all incoming updates from Telegram.
     * Determines if the update is a command or a continuation of an existing
     * conversation.
     *
     * @param update The Telegram Update object.
     */
    public void handleUpdate(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String telegramId = String.valueOf(chatId);

        if (text == null) {
            telegramBotService.sendMessage(chatId, "I only understand text messages. Please try again.");
            return;
        }

        if (text.startsWith("/")) {
            // Handle commands (delegated to TelegramBotService for initial checks)
            handleCommand(chatId, telegramId, text);
        } else {
            // Handle mid-conversation input
            handleConversationInput(chatId, telegramId, text);
        }
    }

    // --- Command Handling ---

    private void handleCommand(long chatId, String telegramId, String text) {
        // Clear any previous state when a new command is issued
        conversationStates.put(chatId, ConversationState.IDLE);

        if (text.equals("/start")) {
            telegramBotService.handleStartCommand(chatId, telegramId);
            if (!telegramBotService.isUserRegistered(telegramId)) {
                // If not registered, move state to start registration flow
                conversationStates.put(chatId, ConversationState.REGISTER_NAME);
            }
        } else if (text.equals("/new_trip")) {
            // Assuming the user is registered, start the trip flow
            if (telegramBotService.isUserRegistered(telegramId)) {
                telegramBotService.handleNewTripCommand(chatId, telegramId);
                conversationStates.put(chatId, ConversationState.TRIP_WAIT_SOURCE);
            } else {
                telegramBotService.sendMessage(chatId, "You need to register first. Use /start.");
            }
        } else if (text.equals("/help")) {
            telegramBotService.handleHelpCommand(chatId);
        } else {
            telegramBotService.sendMessage(chatId, "Unknown command. Use /help for available options.");
        }
    }

    // --- Conversation Flow Handling ---

    private void handleConversationInput(long chatId, String telegramId, String input) {
        ConversationState currentState = conversationStates.getOrDefault(chatId, ConversationState.IDLE);
        logger.info("Handling input '{}' for user {} in state {}", input, telegramId, currentState);

        switch (currentState) {
            case REGISTER_NAME:
                handleRegisterName(chatId, telegramId, input);
                break;
            case REGISTER_PHONE:
                handleRegisterPhone(chatId, telegramId, input);
                break;
            case REGISTER_GENDER:
                handleRegisterGender(chatId, telegramId, input);
                break;
            case TRIP_WAIT_SOURCE:
                handleTripSource(chatId, telegramId, input);
                break;
            case TRIP_WAIT_DESTINATION:
                handleTripDestination(chatId, telegramId, input);
                break;
            case TRIP_WAIT_CONFIRMATION:
                handleTripConfirmation(chatId, telegramId, input);
                break;
            case IDLE:
            default:
                telegramBotService.sendMessage(chatId,
                        "I'm not sure what to do with that. Please use a command like /new_trip or /help.");
                break;
        }
    }

    @Transactional
    private void handleRegisterName(long chatId, String telegramId, String name) {
        // Check if user already exists to prevent duplicate registration
        if (userRepository.existsByTelegramId(telegramId)) {
            telegramBotService.sendMessage(chatId, "You are already registered! Use /new_trip to start a trip.");
            conversationStates.put(chatId, ConversationState.IDLE);
            return;
        }

        // Create and persist a new user entity in a temporary state
        User user = new User();
        user.setTelegramId(telegramId);
        user.setName(name);
        user.setPhone("N/A");
        user.setGender("N/A");
        userRepository.save(user);

        conversationStates.put(chatId, ConversationState.REGISTER_PHONE);
        telegramBotService.sendMessage(chatId,
                "Thank you, " + name + ". What is your phone number (e.g., +1234567890)?");
    }

    @Transactional
    private void handleRegisterPhone(long chatId, String telegramId, String phone) {
        Optional<User> userOpt = userRepository.findByTelegramId(telegramId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Basic validation check
            if (phone.matches("^\\+?[0-9]{7,15}$")) {
                user.setPhone(phone);
                userRepository.save(user);

                conversationStates.put(chatId, ConversationState.REGISTER_GENDER);
                telegramBotService.sendMessage(chatId,
                        "Got the phone number. Finally, what is your gender (MALE/FEMALE/OTHER)?");
            } else {
                telegramBotService.sendMessage(chatId,
                        "That doesn't look like a valid phone number. Please provide a number starting with the country code (e.g., +1234567890).");
            }
        } else {
            // Should not happen if flow is correct
            telegramBotService.sendMessage(chatId, "Error: User data missing. Please start again with /start.");
            conversationStates.put(chatId, ConversationState.IDLE);
        }
    }

    @Transactional
    private void handleRegisterGender(long chatId, String telegramId, String gender) {
        String normalizedGender = gender.toUpperCase();
        if (normalizedGender.equals("MALE") || normalizedGender.equals("FEMALE") || normalizedGender.equals("OTHER")) {
            Optional<User> userOpt = userRepository.findByTelegramId(telegramId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setGender(normalizedGender);
                userRepository.save(user);

                // Registration complete
                conversationStates.put(chatId, ConversationState.IDLE);
                telegramBotService.sendMessage(chatId,
                        "Registration complete! You are all set up. You can now start a trip with /new_trip or manage contacts with /contacts.");
            } else {
                telegramBotService.sendMessage(chatId, "Error: User data missing. Please start again with /start.");
            }
        } else {
            telegramBotService.sendMessage(chatId, "Please enter one of the valid options: MALE, FEMALE, or OTHER.");
        }
    }

    // --- Trip Setup Handlers (Requires TripRepository, currently impossible) ---

    private void handleTripSource(long chatId, String telegramId, String sourceAddress) {
        // NOTE: This logic REQUIRES TripRepository and the ability to call the core
        // SafeWalk backend
        // via RestTemplate to resolve the address and get route options.

        // If TripRepository were available:
        // 1. Save source address to a temporary Trip entity.
        // 2. Call SafeWalk backend to get route options for source/destination (once
        // destination is known).

        // For now, we simulate the state transition:
        conversationStates.put(chatId, ConversationState.TRIP_WAIT_DESTINATION);
        telegramBotService.sendMessage(chatId,
                "Source location noted: " + sourceAddress + ". Now, what is your destination address?");
    }

    private void handleTripDestination(long chatId, String telegramId, String destinationAddress) {
        // NOTE: This logic REQUIRES TripRepository.

        // If TripRepository were available:
        // 1. Save destination address to the temporary Trip entity.
        // 2. Call SafeWalk backend to get route options and safety scores.
        // 3. Present the user with options (e.g., Route A (90% Safe), Route B (80%
        // Safe)).

        // For now, we simulate the state transition and skip complex routing:
        conversationStates.put(chatId, ConversationState.TRIP_WAIT_CONFIRMATION);
        telegramBotService.sendMessage(chatId,
                "Destination noted. I've calculated a safe route. Reply 'YES' to start the trip, or 'CANCEL' to abort.");
    }

    private void handleTripConfirmation(long chatId, String telegramId, String confirmation) {
        if (confirmation.equalsIgnoreCase("YES")) {
            // NOTE: This logic REQUIRES TripRepository.
            // If available, the final Trip entity would be created, and an initial
            // notification
            // would be sent to emergency contacts via the NotificationService.

            telegramBotService.sendMessage(chatId,
                    "Trip started! We will monitor your location. We will notify your contacts if you deviate or fail to check in. Use /cancel to end the trip early.");
            conversationStates.put(chatId, ConversationState.IDLE);
        } else if (confirmation.equalsIgnoreCase("CANCEL")) {
            telegramBotService.sendMessage(chatId, "Trip setup cancelled. You are now back in the main menu.");
            conversationStates.put(chatId, ConversationState.IDLE);
        } else {
            telegramBotService.sendMessage(chatId, "Please reply 'YES' to confirm or 'CANCEL' to abort the trip.");
        }
    }
}