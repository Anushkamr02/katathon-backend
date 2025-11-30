package com.telegrambot.backend.service;

import com.telegrambot.backend.dto.LocationUpdateDto;
import com.telegrambot.backend.entity.Trip;
import com.telegrambot.backend.entity.TripLocation;
import com.telegrambot.backend.entity.User;
import com.telegrambot.backend.repository.TripLocationRepository;
import com.telegrambot.backend.repository.TripRepository;
import com.telegrambot.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service dedicated to handling real-time location updates from Telegram.
 * It performs two main functions:
 * 1. Persists the location data locally (for history and audit).
 * 2. Forwards the location data to the external SafeWalk backend for monitoring and deviation detection.
 */
@Service
public class LocationUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateService.class);

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final TripLocationRepository tripLocationRepository;
    private final RestTemplate restTemplate;
    private final HmacUtil hmacUtil;

    // Base URL for the external SafeWalk Core backend (configured in application.properties)
    @Value("${safewalk.core.api-url}")
    private String coreApiUrl;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository for user lookup.
     * @param tripRepository Repository for finding active trips.
     * @param tripLocationRepository Repository for persisting location history.
     * @param restTemplate Configured RestTemplate for external communication.
     * @param hmacUtil Utility for cryptographic signature generation.
     */
    public LocationUpdateService(UserRepository userRepository,
                                 TripRepository tripRepository,
                                 TripLocationRepository tripLocationRepository,
                                 RestTemplate restTemplate,
                                 HmacUtil hmacUtil) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.tripLocationRepository = tripLocationRepository;
        this.restTemplate = restTemplate;
        this.hmacUtil = hmacUtil;
    }

    /**
     * Handles an incoming live location update from a Telegram user.
     *
     * @param telegramId The Telegram chat/account ID of the user sending the location.
     * @param location The Telegram Location object containing lat and lng.
     */
    @Transactional
    public void handleLocationUpdate(String telegramId, Location location) {
        logger.info("Received location update from user {}: ({}, {})", telegramId, location.getLatitude(), location.getLongitude());

        // 1. Find the user and their active trip
        Optional<User> userOpt = userRepository.findByTelegramId(telegramId);
        if (userOpt.isEmpty()) {
            logger.warn("Unregistered user attempted to send location: {}", telegramId);
            return;
        }

        User user = userOpt.get();
        // Assuming "ACTIVE" is the status for a monitored trip
        Optional<Trip> tripOpt = tripRepository.findByUserIdAndStatus(user.getId(), "ACTIVE");

        if (tripOpt.isEmpty()) {
            logger.info("User {} sent location but has no active trip. Ignoring.", telegramId);
            return;
        }

        Trip trip = tripOpt.get();

        // 2. Persist location locally for audit/history
        TripLocation tripLocation = TripLocation.builder()
                .trip(trip)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .recordedAt(LocalDateTime.now())
                .build();
        tripLocationRepository.save(tripLocation);

        // 3. Prepare DTO for external API call
        LocationUpdateDto updateDto = LocationUpdateDto.builder()
                .tripId(trip.getId())
                .lat(location.getLatitude())
                .lng(location.getLongitude())
                .build();

        // 4. Forward location to SafeWalk Core backend
        forwardLocationToCore(updateDto);
    }

    /**
     * Forwards the location update DTO to the SafeWalk Core backend API.
     * This API endpoint is responsible for deviation detection and monitoring.
     *
     * @param updateDto The DTO containing trip ID and coordinates.
     */
    private void forwardLocationToCore(LocationUpdateDto updateDto) {
        String url = coreApiUrl + "/api/trips/" + updateDto.getTripId() + "/locations";
        
        try {
            // Convert DTO to JSON string
            String jsonPayload = hmacUtil.convertObjectToJson(updateDto);

            // Generate HMAC signature
            String signature = hmacUtil.generateSignature(jsonPayload);

            // Set up headers (Content-Type and HMAC signature)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Signature", signature);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            // Send the request
            logger.debug("Forwarding location update for trip {} to Core.", updateDto.getTripId());
            restTemplate.postForEntity(url, requestEntity, Void.class);
            logger.info("Successfully forwarded location update for trip {}.", updateDto.getTripId());

        } catch (Exception e) {
            logger.error("Failed to forward location update to SafeWalk Core at {}: {}", url, e.getMessage(), e);
            // Non-critical error: allow the trip to continue, but log the failure.
        }
    }
}