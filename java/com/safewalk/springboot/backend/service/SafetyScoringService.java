package com.safewalk.springboot.backend.service;

import com.safewalk.springboot.backend.dto.RouteOptionDto;

import java.util.List;

public interface SafetyScoringService {

    /**
     * Compute final safety score (0–10) for a single route.
     */
    int calculateSafetyScore(RouteOptionDto route, boolean isFemale);

    /**
     * Compute minimum distance from current location to a given polyline.
     * Note: polyline is represented as List of [lat, lng] lists.
     */
    double calculateDistanceFromRoute(List<List<Double>> polyline, double currentLat, double currentLng);
}
