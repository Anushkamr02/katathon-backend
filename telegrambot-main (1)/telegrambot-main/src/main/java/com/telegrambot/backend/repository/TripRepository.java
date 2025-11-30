package com.telegrambot.backend.repository;

import com.telegrambot.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Trip entities.
 * Provides specialized methods for finding trips by user and status,
 * which is essential for monitoring and lifecycle management.
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * Finds the currently active trip for a given user.
     * This is crucial for managing trip lifecycle (ending, monitoring, SOS).
     *
     * @param userId The ID of the User entity.
     * @param status The status indicating an active trip (e.g., "ACTIVE").
     * @return An Optional containing the active Trip.
     */
    Optional<Trip> findByUserIdAndStatus(Long userId, String status);

    /**
     * Finds a trip by its ID and ensures it belongs to the specified user.
     *
     * @param id The trip ID.
     * @param userId The ID of the owner User entity.
     * @return An Optional containing the Trip if found and owned by the user.
     */
    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}