package com.telegrambot.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
 * DTO representing the payload for creating a trip in the SafeWalk backend.
 * This is sent as JSON from the Telegram bot to the safewalk backend via POST /api/trips.
 * 
 * Example JSON payload:
 * {
 *   "userName": "John Doe",
 *   "phone": "+1234567890",
 *   "emergencyContacts": [{"name": "Jane Doe", "phone": "+0987654321"}],
 *   "gender": "MALE",
 *   "source": {"lat": 40.7128, "lng": -74.0060, "rawAddress": "New York, NY"},
 *   "destination": {"lat": 34.0522, "lng": -118.2437, "rawAddress": "Los Angeles, CA"},
 *   "requestedAt": "2023-10-01T12:00:00Z",
 *   "telegramChatId": 123456789
 * }
 * 
 * Note: Use toString() cautiously as it may expose sensitive fields like phone numbers in logs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripCreationDto {

    /**
     * The full name of the user creating the trip.
     */
    @NotBlank(message = "User name cannot be blank")
    @Size(max = 200, message = "User name must be at most 200 characters")
    private String userName;

    /**
     * The user's phone number, supporting international formats.
     */
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be a valid international format (7-15 digits, optional +)")
    private String phone;

    /**
     * List of emergency contacts for the trip. Must contain at least one.
     */
    @NotNull(message = "Emergency contacts cannot be null")
    @Size(min = 1, message = "At least one emergency contact is required")
    @Valid
    private List<EmergencyContactDto> emergencyContacts;

    /**
     * The gender of the user.
     */
    @NotNull(message = "Gender cannot be null")
    private Gender gender;

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

    /**
     * Optional ISO-8601 timestamp when the trip was requested (e.g., "2023-10-01T12:00:00Z").
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z?$", message = "Requested at must be a valid ISO-8601 timestamp", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String requestedAt;

    /**
     * Optional Telegram chat ID for routing notifications back to the user.
     */
    private Long telegramChatId;
}