package com.safewalk.springboot.backend.service;

import com.safewalk.springboot.backend.dto.RouteOptionDto;

import java.util.List;

/**
 * Service contract for scoring route options.
 */
public interface SafetyScoringService {

    /**
     * Score and sort route options by safety. Implementations should set safetyScore
     * on provided RouteOptionDto objects and return them (sorted).
     */
    List<RouteOptionDto> calculateScores(List<RouteOptionDto> routeOptions);

    /**
     * Utility: compute distance (meters) from point to polyline.
     * Implementations may expose precise logic used by deviation detection.
     */
    double calculateDistanceFromRoute(java.util.List<java.util.List<Double>> polyline, double lat, double lng);
}
