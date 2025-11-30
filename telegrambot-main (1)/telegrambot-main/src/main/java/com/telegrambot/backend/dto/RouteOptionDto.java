package com.telegrambot.backend.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a route option for a trip.
 * Returned by the safewalk backend via GET /api/trips/{id}/route-options.
 * Includes route geometry, safety score, and other details for user selection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteOptionDto {

    /**
     * The ID of the route option.
     */
    @NotNull(message = "Route ID cannot be null")
    private Long routeId;

    /**
     * The safety score of the route (e.g., 0.0 to 1.0, higher is safer).
     */
    @NotNull(message = "Safety score cannot be null")
    private Double safetyScore;

    /**
     * The estimated distance of the route in meters.
     */
    @NotNull(message = "Distance cannot be null")
    private Double distance;

    /**
     * The estimated duration of the route in seconds.
     */
    @NotNull(message = "Duration cannot be null")
    private Double duration;

    /**
     * List of coordinates representing the route polyline (e.g., [[lat1, lng1], [lat2, lng2]]).
     */
    @NotNull(message = "Polyline cannot be null")
    @Size(min = 2, message = "Polyline must have at least 2 points")
    private List<List<Double>> polyline;

    /**
     * Optional description of the route (e.g., "Safest route via well-lit streets").
     */
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;
}