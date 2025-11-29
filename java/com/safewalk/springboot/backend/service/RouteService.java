package com.safewalk.springboot.backend.service;

import com.safewalk.springboot.backend.dto.RouteOptionDto;
import com.safewalk.springboot.backend.dto.TripCreationDto;
import com.safewalk.springboot.backend.dto.TripRouteOptionsDto;
import com.safewalk.springboot.backend.tomtomintegration.TomTomRoutingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Minimal RouteService that uses TomTomRoutingService stub and SafetyScoringService.
 */
@Service
public class RouteService {

    private final TomTomRoutingService tomTomRoutingService;
    private final SafetyScoringService safetyScoringService;

    public RouteService() {
        this.tomTomRoutingService = new TomTomRoutingService("", true);
        this.safetyScoringService = routes -> {
            for (RouteOptionDto r : routes) {
                double d = r.getDistanceMeters() == null ? 1000.0 : r.getDistanceMeters();
                r.setSafetyScore(Math.max(0.0, 1000.0 / d));
            }
            return routes;
        };
    }

    public TripRouteOptionsDto computeRoutes(TripCreationDto requestDto) {
        String source = requestDto.getSource();
        String destination = requestDto.getDestination();

        List<Map<String,Object>> raw = tomTomRoutingService.getRoutes(source, destination, 3);
        List<RouteOptionDto> options = new ArrayList<>();
        for (Map<String,Object> r : raw) {
            RouteOptionDto ro = new RouteOptionDto();
            ro.setId(((Number) r.getOrDefault("id", 1)).intValue());
            ro.setDistanceMeters(((Number) r.getOrDefault("distanceMeters", 1000)).doubleValue());
            ro.setPolyline((String) r.getOrDefault("polyline", ""));
            options.add(ro);
        }

        options = safetyScoringService.calculateScores(options);
        options.sort((a,b) -> Double.compare(b.getSafetyScore(), a.getSafetyScore()));

        return new TripRouteOptionsDto(null, options);
    }
}
