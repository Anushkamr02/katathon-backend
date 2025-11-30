package com.telegrambot.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * JPA entity representing a single geographic location update received during a Trip.
 * Used for tracking and deviation detection.
 */
@Entity
@Table(name = "trip_locations", indexes = {
    @Index(name = "idx_location_trip_time", columnList = "trip_id, recorded_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The trip this location belongs to (Many-to-one relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @NotNull
    private Trip trip;

    @Column(nullable = false)
    @NotNull
    private Double latitude;

    @Column(nullable = false)
    @NotNull
    private Double longitude;

    // Timestamp when the location was recorded/received
    @Column(name = "recorded_at", nullable = false)
    @NotNull
    private LocalDateTime recordedAt;

    // Accuracy of the GPS reading (optional)
    @Column(name = "accuracy_meters")
    private Double accuracyMeters;
}