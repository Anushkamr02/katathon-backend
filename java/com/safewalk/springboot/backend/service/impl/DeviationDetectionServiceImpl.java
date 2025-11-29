package com.safewalk.springboot.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safewalk.springboot.backend.entity.CoreLocationUpdate;
import com.safewalk.springboot.backend.entity.Trip;
import com.safewalk.springboot.backend.entity.TripStatus;
import com.safewalk.springboot.backend.repository.TripRepository;
import com.safewalk.springboot.backend.service.DeviationDetectionService;
import com.safewalk.springboot.backend.service.NotificationService;
import com.safewalk.springboot.backend.service.SafetyScoringService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of the Deviation Detection Service.
 * * Calculates the distance from the selected route and triggers alerts if the
 * deviation exceeds the trip's defined threshold.
 */
@Service
@RequiredArgsConstructor
public class DeviationDetectionServiceImpl implements DeviationDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(DeviationDetectionServiceImpl.class);

    private final SafetyScoringService safetyScoringService;
    private final NotificationService notificationService;
    private final TripRepository tripRepository;
    private final ObjectMapper objectMapper;

    // Type reference for deserializing the polyline JSON: List of Lists of Doubles
    private static final TypeReference<List<List<Double>>> POLYLINE_TYPE = new TypeReference<>() {};

    /**
     * Checks for deviation upon every location update.
     *
     * @param trip The active Trip entity.
     * @param latestLocation The latest recorded location update.
     */
    @Override
    @Transactional
    public void checkDeviation(Trip trip, CoreLocationUpdate latestLocation) {
        // 1. Deserialize the selected route polyline
        List<List<Double>> polyline;
        try {
            polyline = objectMapper.readValue(trip.getSelectedRoutePolylineJson(), POLYLINE_TYPE);
        } catch (IOException e) {
            logger.error("Failed to deserialize polyline for Trip ID: {}. Cannot perform deviation check.", trip.getId(), e);
            return;
        }

        // 2. Calculate the shortest distance from the current location to the polyline
        double distance = safetyScoringService.calculateDistanceFromRoute(
            polyline,
            latestLocation.getLatitude(),
            latestLocation.getLongitude()
        );

        // 3. Compare distance to the threshold
        double threshold = trip.getDeviationThresholdMeters();
        logger.debug("Trip ID {} distance from route: {} meters (Threshold: {}m)", trip.getId(), Math.round(distance), threshold);

        if (distance > threshold) {
            handleDeviationDetected(trip, distance);
        } else if (trip.getStatus() == TripStatus.DEVIATION_ALERT) {
            // If the user was deviating but is now back on track, clear the alert
            handleDeviationCleared(trip);
        }
    }

    /**
     * Handles the state change when a deviation is detected.
     *
     * @param trip The deviating trip.
     * @param distance The deviation distance in meters.
     */
    private void handleDeviationDetected(Trip trip, double distance) {
        if (trip.getStatus() == TripStatus.ACTIVE) {
            // First time deviation detected: change status and notify user
            trip.setStatus(TripStatus.DEVIATION_ALERT);
            tripRepository.save(trip);
            logger.warn("DEVIATION DETECTED for Trip ID {}. Distance: {}m.", trip.getId(), Math.round(distance));

            String message = String.format(
                "⚠️ **ROUTE DEVIATION ALERT** ⚠️\n" +
                "You are **%d meters** off your selected safe route. Please confirm you are safe by replying with /safe.\n" +
                "If you need help, reply with /sos immediately.",
                (int) Math.round(distance)
            );
            notificationService.sendNotificationToUser(trip.getId(), message);

            // NOTE: In a complete system, a timer would start here. If no /safe or /sos is received
            // after N minutes, the status would automatically escalate to SOS_PENDING.
        }
    }

    /**
     * Handles the state change when a previously deviating user is back on track.
     *
     * @param trip The trip that is now back on route.
     */
    private void handleDeviationCleared(Trip trip) {
        trip.setStatus(TripStatus.ACTIVE);
        tripRepository.save(trip);
        logger.info("Deviation cleared for Trip ID {}. User is back on route.", trip.getId());

        notificationService.sendNotificationToUser(trip.getId(), "✅ **Deviation Cleared**\nYou are back on your safe route. Monitoring continues.");
    }
}