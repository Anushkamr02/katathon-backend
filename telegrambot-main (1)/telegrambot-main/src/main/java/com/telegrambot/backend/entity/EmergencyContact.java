package com.telegrambot.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing an emergency contact in the SafeWalk Telegram bot backend.
 * 
 * This entity maps to the 'emergency_contacts' table in the MySQL database and stores
 * contact information for users' emergency contacts. Each emergency contact is linked
 * to a specific User via a many-to-one relationship, allowing multiple contacts per user.
 * The relationship is bidirectional with the User entity, using 'user_id' as the foreign key.
 */
@Entity
@Table(name = "emergency_contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one relationship with User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String phone;
}