package com.safewalk.springboot.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing an SOS alert for a specific trip.
 * * This DTO is received via POST /api/sos.
 * It triggers the emergency notification process via the SosService.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SosDto {

    /**
     * The ID of the trip where the SOS was triggered (Core Backend's ID).
     */
    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    /**
     * The unique Telegram Chat ID of the user who triggered the SOS.
     */
    @NotNull(message = "Telegram Chat ID cannot be null")
    private Long telegramChatId;

    /**
     * Optional message describing the emergency (e.g., "I feel unsafe").
     */
    @Size(max = 500, message = "SOS message must be at most 500 characters")
    private String message;

    /**
     * The current latitude of the user when the SOS was triggered.
     */
    @NotNull(message = "Latitude cannot be null")
    private Double lat;

    /**
     * The current longitude of the user when the SOS was triggered.
     */
    @NotNull(message = "Longitude cannot be null")
    private Double lng;

    /**
     * The time the SOS was triggered, sent as an ISO-8601 string from the bot.
     */
    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;
}