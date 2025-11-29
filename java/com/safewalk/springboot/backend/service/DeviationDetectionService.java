package com.safewalk.springboot.backend.service;

import com.safewalk.springboot.backend.entity.CoreLocationUpdate;
import com.safewalk.springboot.backend.entity.Trip;

/**
 * Service contract for detecting deviations from a planned route.
 * Implementations should inspect a Trip and the latest location update and
 * take appropriate actions (update trip status, notify, etc).
 */
public interface DeviationDetectionService {

    /**
     * Check whether the provided latestLocation deviates from the trip's selected route.
     * Implementations may update the trip state and send notifications as needed.
     *
     * @param trip the Trip entity to check (must not be null)
     * @param latestLocation the most recent CoreLocationUpdate for the trip (must not be null)
     */
    void checkDeviation(Trip trip, CoreLocationUpdate latestLocation);
}
