package com.safewalk.springboot.backend.service;

import com.safewalk.springboot.backend.entity.User;

import java.util.Optional;

/**
 * User service contract used by other services (SosServiceImpl, controllers, etc).
 * Provide methods to look up users by telegram id or by internal id and to create/update users.
 */
public interface UserService {

    /**
     * Find a user by their Telegram chat id.
     *
     * @param telegramChatId telegram chat id
     * @return Optional with User if found
     */
    Optional<User> findByTelegramChatId(Long telegramChatId);

    /**
     * Find user by internal DB id.
     *
     * @param id user primary key
     * @return Optional with User if found
     */
    Optional<User> findById(Long id);

    /**
     * Create or update a user record.
     *
     * @param user user entity
     * @return saved user
     */
    User createOrUpdate(User user);
}
