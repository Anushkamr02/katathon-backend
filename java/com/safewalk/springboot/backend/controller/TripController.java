package com.safewalk.springboot.backend.controller;

import com.safewalk.springboot.backend.dto.LocationUpdateDto;
import com.safewalk.springboot.backend.dto.RouteOptionDto;
import com.safewalk.springboot.backend.dto.TripCreationDto;
import com.safewalk.springboot.backend.dto.TripRouteOptionsDto;
import com.safewalk.springboot.backend.entity.CoreLocationUpdate;
import com.safewalk.springboot.backend.entity.Trip;
import com.safewalk.springboot.backend.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing the entire Trip lifecycle.
 * All communication from the Telegram Bot backend to the SafeWalk core system
 * regarding trips (creation, start, location updates, completion) flows through here.
 */
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    private final TripService tripService;

    /**
     * Endpoint 1: Initiates a new trip request.
     * Finds and scores multiple route options based on source, destination, and user profile.
     * The trip is created in a PENDING status.
     *
     * @param requestDto The DTO containing trip details (source, destination, user info).
     * @return 201 Created with the Trip ID and the list of scored Route Options.
     */
    @PostMapping
    public ResponseEntity<TripRouteOptionsDto> createTrip(@Valid @RequestBody TripCreationDto requestDto) {
        logger.info("Received request to create a trip from Telegram Chat ID: {}", requestDto.getTelegramChatId());
        TripRouteOptionsDto options = tripService.createTrip(requestDto);
        return new ResponseEntity<>(options, HttpStatus.CREATED);
    }

    /**
     * Endpoint 2: Starts the trip using a user-selected route option.
     * Updates the trip status to ACTIVE and begins real-time monitoring.
     *
     * @param tripId The ID of the pending trip.
     * @param selectedRoute The route selected by the user from the options provided.
     * @return 200 OK with the full updated Trip entity.
     */
    @PostMapping("/{tripId}/start")
    public ResponseEntity<Trip> startTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody RouteOptionDto selectedRoute) {
        logger.info("Starting trip ID {} with selected route ID {}", tripId, selectedRoute.getRouteId());
        Trip trip = tripService.startTrip(tripId, selectedRoute);
        return ResponseEntity.ok(trip);
    }

    /**
     * Endpoint 3: Records a new location update for an active trip.
     * This is called repeatedly by the Telegram bot's live location feature.
     * Triggers the Deviation Detection Service internally.
     *
     * @param tripId The ID of the active trip.
     * @param updateDto The location data (lat/lng).
     * @return 200 OK with the saved CoreLocationUpdate entity.
     */
    @PostMapping("/{tripId}/locations")
    public ResponseEntity<CoreLocationUpdate> recordLocationUpdate(
            @PathVariable Long tripId,
            @Valid @RequestBody LocationUpdateDto updateDto) {
        logger.debug("Received location update for trip ID {} at ({}, {})", tripId, updateDto.getLat(), updateDto.getLng());

        // Note: The LocationUpdateDto may contain the tripId, but using the @PathVariable is safer
        // since it is enforced by the URL structure.

        CoreLocationUpdate savedUpdate = tripService.recordLocationUpdate(tripId, updateDto);
        return ResponseEntity.ok(savedUpdate);
    }

    /**
     * Endpoint 4: Completes an active trip.
     * Marks the trip status as COMPLETED and stops monitoring.
     *
     * @param tripId The ID of the trip to complete.
     * @return 200 OK with the completed Trip entity.
     */
    @PostMapping("/{tripId}/complete")
    public ResponseEntity<Trip> completeTrip(@PathVariable Long tripId) {
        logger.info("Request received to complete trip ID: {}", tripId);
        Trip completedTrip = tripService.completeTrip(tripId);
        return ResponseEntity.ok(completedTrip);
    }
}