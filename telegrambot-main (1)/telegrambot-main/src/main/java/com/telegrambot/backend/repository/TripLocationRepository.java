package com.telegrambot.backend.repository;

import com.telegrambot.backend.entity.TripLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing TripLocation entities.
 * Used to store the historical path data for an active trip.
 */
@Repository
public interface TripLocationRepository extends JpaRepository<TripLocation, Long> {

    // Standard JpaRepository methods are sufficient for basic save/read operations.
    // Future expansion might include methods for finding the last recorded location
    // or locations within a time range, but we'll stick to the basics for now.
}