package com.telegrambot.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a live location update for a trip.
 * Sent from the Telegram bot to the safewalk backend via POST /api/trips/{tripId}/locations.
 * Used to track the user's position during the trip for deviation detection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationUpdateDto {

    /**
     * The ID of the trip being updated.
     */
    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    /**
     * The latitude of the current location.
     */
    @NotNull(message = "Latitude cannot be null")
    private Double lat;

    /**
     * The longitude of the current location.
     */
    @NotNull(message = "Longitude cannot be null")
    private Double lng;
}