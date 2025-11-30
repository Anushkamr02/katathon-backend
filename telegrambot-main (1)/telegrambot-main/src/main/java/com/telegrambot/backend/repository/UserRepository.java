package com.telegrambot.backend.repository;

import com.telegrambot.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing User entities.
 * Extends JpaRepository to get basic CRUD operations and provides custom
 * query methods for bot-specific lookups.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their unique Telegram ID.
     * This is the primary lookup mechanism for recognizing a user who sends a message
     * to the bot, as the Telegram ID is the foreign key for all user data.
     *
     * @param telegramId The unique ID (String) provided by Telegram (e.g., from Message.getChatId()).
     * @return An Optional containing the User if found.
     */
    Optional<User> findByTelegramId(String telegramId);

    /**
     * Checks if a User already exists with the given Telegram ID.
     * Used typically during initial registration (/start command) to check if the user is new.
     *
     * @param telegramId The unique ID (String) provided by Telegram.
     * @return true if a user exists, false otherwise.
     */
    boolean existsByTelegramId(String telegramId);
}