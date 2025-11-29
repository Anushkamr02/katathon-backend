package com.safewalk.springboot.backend.repository;

import com.safewalk.springboot.backend.entity.Trip;
import com.safewalk.springboot.backend.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Trip entities in the SafeWalk Core Backend.
 * * Provides standard CRUD operations and custom queries necessary for trip management and safety monitoring.
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * Finds a Trip entity by its ID and ensures it is in the ACTIVE status.
     * * This is used frequently by the DeviationDetectionService and LocationUpdate endpoints.
     *
     * @param tripId The unique ID of the trip.
     * @return An Optional containing the active Trip, or empty if not found or not active.
     */
    Optional<Trip> findByIdAndStatus(Long tripId, TripStatus status);

    /**
     * Finds the most recent ACTIVE trip associated with a specific Telegram chat ID.
     * * This is useful if the bot needs to check the status of a user's current trip.
     *
     * @param telegramChatId The Telegram chat identifier.
     * @param status The status to filter by (should be TripStatus.ACTIVE).
     * @return An Optional containing the most recently active Trip.
     */
    Optional<Trip> findTopByTelegramChatIdAndStatusOrderByStartedAtDesc(Long telegramChatId, TripStatus status);

    /**
     * Finds all ACTIVE trips.
     * * Used by scheduled background tasks to monitor all currently active journeys.
     *
     * @param status The status to filter by (should be TripStatus.ACTIVE).
     * @return A list of all active Trip entities.
     */
    List<Trip> findAllByStatus(TripStatus status);
}