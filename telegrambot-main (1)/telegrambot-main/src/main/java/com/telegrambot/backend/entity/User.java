package com.telegrambot.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a user in the SafeWalk Telegram bot backend.
 * 
 * This entity maps to the 'users' table in the ZenP MySQL database and stores
 * information about users interacting with the bot. The 'telegramId' field is
 * used as a unique identifier for Telegram chat/accounts, allowing the system
 * to recognize returning users and associate their emergency contacts and
 * trips.
 * 
 * Relationships:
 * - A user can have multiple emergency contacts (one-to-many).
 * - A user can have multiple trips (one-to-many).
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_telegram_id", columnList = "telegramId"),
        @Index(name = "idx_user_phone", columnList = "phone")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String telegramId; // chat/account identifier

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 10)
    private String gender;

    // One-to-many relationship with EmergencyContact
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

    // One-to-many relationship with Trip
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

    /**
     * Convenience method to add an emergency contact to the user.
     * Sets the bidirectional relationship and adds to the list.
     * 
     * @param ec The EmergencyContact to add.
     */
    public void addEmergencyContact(EmergencyContact ec) {
        emergencyContacts.add(ec);
        ec.setUser(this);
    }

    /**
     * Convenience method to add a trip to the user.
     * Sets the bidirectional relationship and adds to the list.
     * 
     * @param trip The Trip to add.
     */
    public void addTrip(Trip trip) {
        trips.add(trip);
        trip.setUser(this);
    }
}