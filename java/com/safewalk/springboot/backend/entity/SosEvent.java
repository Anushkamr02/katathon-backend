package com.safewalk.springboot.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA entity representing a user-initiated SOS (Emergency) event.
 * * This entity logs all necessary details about the emergency, including location
 * and the time of the alert, primarily for auditing and post-event analysis.
 */
@Entity
@Table(name = "sos_events", indexes = {
    @Index(name = "idx_sos_trip_time", columnList = "trip_id, created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SosEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The trip during which the SOS was triggered (Many-to-One relationship)
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

    // Optional message from the user
    @Size(max = 500)
    private String message;

    // Timestamp when the SOS event was recorded
    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;
    
    // Status of the emergency handling (e.g., PENDING, CONTACTED, RESOLVED)
    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";
}