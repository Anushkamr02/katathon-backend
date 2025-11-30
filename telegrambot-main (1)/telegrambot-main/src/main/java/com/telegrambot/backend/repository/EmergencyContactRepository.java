package com.telegrambot.backend.repository;

import com.telegrambot.backend.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing EmergencyContact entities.
 * Provides methods to find contacts linked to a specific user.
 */
@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    /**
     * Finds all EmergencyContacts associated with a specific user ID.
     * This is the main way the bot backend retrieves contact lists for a user.
     *
     * @param userId The ID of the User entity.
     * @return A list of EmergencyContact entities.
     */
    List<EmergencyContact> findByUserId(Long userId);

    /**
     * Finds a specific EmergencyContact by its ID and the ID of the parent user.
     * Useful for safely retrieving or deleting a contact when both IDs are known.
     *
     * @param id The ID of the EmergencyContact.
     * @param userId The ID of the parent User entity.
     * @return An Optional containing the EmergencyContact if found.
     */
    Optional<EmergencyContact> findByIdAndUserId(Long id, Long userId);
}