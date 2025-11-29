package com.safewalk.springboot.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a live location update for a specific active trip.
 * * This DTO is received via POST /api/trips/{tripId}/locations.
 * It is consumed by the DeviationDetectionService for safety monitoring.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateDto {

    /**
     * The unique ID of the trip being updated (Core Backend's ID).
     */
    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    /**
     * The latitude of the current location.
     */
    @NotNull(message = "Latitude cannot be null")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double lat;

    /**
     * The longitude of the current location.
     */
    @NotNull(message = "Longitude cannot be null")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double lng;

    /**
     * The time the location was recorded, sent as an ISO-8601 string from the bot.
     * This is crucial for accurate tracking and deviation analysis.
     */
    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;
}