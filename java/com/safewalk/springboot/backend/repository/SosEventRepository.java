package com.safewalk.springboot.backend.repository;

import com.safewalk.springboot.backend.entity.SosEvent;
import com.safewalk.springboot.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing SosEvent entities (Emergency Alerts)
 * in the SafeWalk Core Backend.
 * * Provides access for logging new SOS events and retrieving historical data for a trip.
 */
@Repository
public interface SosEventRepository extends JpaRepository<SosEvent, Long> {

    /**
     * Finds all SOS events associated with a specific trip.
     *
     * @param trip The Trip entity.
     * @return A list of SosEvent entities for the given trip.
     */
    List<SosEvent> findAllByTrip(Trip trip);
}