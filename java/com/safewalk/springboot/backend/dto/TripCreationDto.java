package com.safewalk.springboot.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the initial payload for creating a trip request in the SafeWalk Core Backend.
 * * This DTO is received via POST /api/trips.
 * It contains all user, contact, and location data needed to calculate safe routes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripCreationDto {

    /**
     * Unique identifier for the Telegram chat/user requesting the trip.
     * Required for sending notifications back.
     */
    @NotNull(message = "Telegram Chat ID cannot be null")
    private Long telegramChatId;

    /**
     * User's full name.
     */
    @NotBlank(message = "User name cannot be blank")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String userName;

    /**
     * User's phone number, supporting international formats.
     */
    @NotBlank(message = "Phone number cannot be blank")
    // Basic pattern validation for a phone number (e.g., optional +, 7-15 digits)
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be a valid international format (7-15 digits, optional +)")
    private String phone;

    /**
     * User's gender (for safety score personalization, e.g., 'MALE', 'FEMALE', 'OTHER').
     */
    @NotBlank(message = "Gender cannot be blank")
    @Size(max = 10, message = "Gender must be at most 10 characters")
    private String gender;

    /**
     * List of emergency contacts for the trip. Must contain at least one.
     */
    @NotNull(message = "Emergency contacts cannot be null")
    @Size(min = 1, message = "At least one emergency contact is required")
    @Valid // Ensures nested DTOs are also validated
    private List<EmergencyContactDto> emergencyContacts;

    /**
     * The starting location of the trip.
     */
    @NotNull(message = "Source location cannot be null")
    @Valid
    private LocationDto source;

    /**
     * The destination location of the trip.
     */
    @NotNull(message = "Destination location cannot be null")
    @Valid
    private LocationDto destination;
}