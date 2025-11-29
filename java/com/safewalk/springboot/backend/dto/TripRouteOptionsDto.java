package com.safewalk.springboot.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper DTO for holding all generated route options for a new trip request.
 * * This is the final response payload for POST /api/trips.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRouteOptionsDto {

    /**
     * The unique ID of the Trip that was created in the Core Backend database.
     * The Telegram bot uses this ID for subsequent actions (e.g., /start, /locations).
     */
    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    /**
     * The list of calculated route options, sorted by safety score (highest first).
     */
    @NotNull(message = "Route options list cannot be null")
    @Size(min = 1, message = "At least one route option must be provided")
    @Valid
    private List<RouteOptionDto> routeOptions;
}