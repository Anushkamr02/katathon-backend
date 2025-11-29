package com.safewalk.springboot.backend.repository;

import com.safewalk.springboot.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for the User entity.
 * * Provides methods for performing CRUD operations and complex queries on User data.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User entity by their unique Telegram Chat ID.
     * * Required for the SosService and TripService to identify the user initiating the trip.
     *
     * @param telegramChatId The unique Telegram chat identifier.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByTelegramChatId(Long telegramChatId);
}