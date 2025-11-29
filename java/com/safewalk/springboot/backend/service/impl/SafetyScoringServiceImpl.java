package com.safewalk.springboot.backend.service.impl;

import com.safewalk.springboot.backend.dto.RouteOptionDto;
import com.safewalk.springboot.backend.service.SafetyScoringService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal scoring implementation so project compiles. Replace with real rules later.
 */
@Service
public class SafetyScoringServiceImpl implements SafetyScoringService {

    @Override
    public List<RouteOptionDto> calculateScores(List<RouteOptionDto> routeOptions) {
        if (routeOptions == null) return new ArrayList<>();
        for (RouteOptionDto r : routeOptions) {
            double km = (r.getDistance() == null) ? 0.0 : r.getDistance() / 1000.0;
            double score = Math.max(0.0, 100.0 - km); // simple placeholder
            r.setSafetyScore(score);
        }
        routeOptions.sort((a, b) -> Double.compare(b.getSafetyScore(), a.getSafetyScore()));
        return routeOptions;
    }

    @Override
    public double calculateDistanceFromRoute(List<List<Double>> polyline, double lat, double lng) {
        if (polyline == null || polyline.size() < 1) return Double.POSITIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < polyline.size() - 1; i++) {
            List<Double> a = polyline.get(i);
            List<Double> b = polyline.get(i + 1);
            double d = distancePointToSegmentMeters(lat, lng, a.get(0), a.get(1), b.get(0), b.get(1));
            if (d < min) min = d;
        }
        return min;
    }

    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        double phi1 = Math.toRadians(lat1), phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1), dlambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dphi/2)*Math.sin(dphi/2) + Math.cos(phi1)*Math.cos(phi2)*Math.sin(dlambda/2)*Math.sin(dlambda/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private double distancePointToSegmentMeters(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax, dy = by - ay;
        if (dx == 0 && dy == 0) return haversineMeters(px, py, ax, ay);
        double t = ((px - ax) * dx + (py - ay) * dy) / (dx*dx + dy*dy);
        t = Math.max(0, Math.min(1, t));
        double projLat = ax + t * dx, projLng = ay + t * dy;
        return haversineMeters(px, py, projLat, projLng);
    }
}
