package com.telegrambot.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips", indexes = {
    @Index(name = "idx_trip_user_status", columnList = "user_id, status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who initiated the trip (Many-to-one relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    // Start and end points (address text)
    @Column(nullable = false, length = 255)
    @NotNull
    private String startPointAddress;

    @Column(nullable = false, length = 255)
    @NotNull
    private String endPointAddress;

    // Planned route as serialized data
    @Lob
    @Column(name = "planned_route_data")
    private String plannedRouteData;

    // Planned start and expected end time
    @Column(name = "start_time", nullable = false)
    @NotNull
    private LocalDateTime startTime;

    @Column(name = "expected_end_time", nullable = false)
    @NotNull
    private LocalDateTime expectedEndTime;

    // Actual time the trip was completed or cancelled
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    // Status of the trip
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private TripStatus status = TripStatus.PENDING;

    // Flag to indicate SOS
    @Column(name = "sos_activated", nullable = false)
    private boolean sosActivated = false;

    // Location updates (one-to-many)
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripLocation> locations = new ArrayList<>();

    // Enum for status
    public enum TripStatus {
        PENDING, ACTIVE, COMPLETED, SOS, DEVIATED, CANCELLED
    }

    // Convenience helper
    public void addLocation(TripLocation location) {
        locations.add(location);
        location.setTrip(this);
    }
}