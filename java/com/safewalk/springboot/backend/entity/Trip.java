package com.safewalk.springboot.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TripStatus status = TripStatus.CREATED;

    @Column(name = "selected_route_polyline_json", columnDefinition = "TEXT")
    private String selectedRoutePolylineJson;

    @Column(name = "deviation_threshold_meters")
    private Double deviationThresholdMeters = 30.0;

    @Column(name = "distance_meters")
    private Double distance;

    @Column(name = "duration_seconds")
    private Long duration;

    @Column(name = "safety_score")
    private Double safetyScore;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    private CoreLocationUpdate latestLocation;

    public Trip() {}

    public Long getId() { return id; }

    public Long getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(Long id) { this.telegramChatId = id; }

    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }

    public String getSelectedRoutePolylineJson() { return selectedRoutePolylineJson; }
    public void setSelectedRoutePolylineJson(String s) { this.selectedRoutePolylineJson = s; }

    public Double getDeviationThresholdMeters() { return deviationThresholdMeters == null ? 30.0 : deviationThresholdMeters; }
    public void setDeviationThresholdMeters(Double d) { this.deviationThresholdMeters = d; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public Double getSafetyScore() { return safetyScore; }
    public void setSafetyScore(Double safetyScore) { this.safetyScore = safetyScore; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public CoreLocationUpdate getLatestLocation() { return latestLocation; }
    public void setLatestLocation(CoreLocationUpdate latestLocation) { this.latestLocation = latestLocation; }
}
