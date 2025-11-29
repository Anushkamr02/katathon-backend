package com.safewalk.springboot.backend.dto;

import java.util.List;

/**
 * DTO representing a route option returned by the routing/scoring engine.
 */
public class RouteOptionDto {
    private Integer id;
    private String name;
    private Double distance;      // meters
    private Long duration;        // seconds
    private Double safetyScore;
    private List<LocationDto> polyline;

    public RouteOptionDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    // some code expected getRouteId()/getId confusion; provide both
    public Integer getRouteId() { return id; }
    public void setRouteId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public Double getSafetyScore() { return safetyScore; }
    public void setSafetyScore(Double safetyScore) { this.safetyScore = safetyScore; }

    public List<LocationDto> getPolyline() { return polyline; }
    public void setPolyline(List<LocationDto> polyline) { this.polyline = polyline; }
}
