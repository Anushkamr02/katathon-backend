package com.safewalk.springboot.backend.repository;

import com.safewalk.springboot.backend.entity.CoreLocationUpdate;
import com.safewalk.springboot.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing CoreLocationUpdate entities (location history)
 * in the SafeWalk Core Backend.
 * * This data is crucial for historical analysis and debugging deviation events.
 */
@Repository
public interface TripLocationRepository extends JpaRepository<CoreLocationUpdate, Long> {

    /**
     * Finds the latest location update for a specific trip, ordered by timestamp.
     * * Useful for determining the user's most recent position.
     *
     * @param trip The Trip entity.
     * @return An Optional containing the latest location update.
     */
    Optional<CoreLocationUpdate> findTopByTripOrderByRecordedAtDesc(Trip trip);

    /**
     * Finds all location updates for a specific trip, ordered chronologically.
     *
     * @param trip The Trip entity.
     * @return A list of all CoreLocationUpdate entities for the trip.
     */
    List<CoreLocationUpdate> findAllByTripOrderByRecordedAtAsc(Trip trip);
}