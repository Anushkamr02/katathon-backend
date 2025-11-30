package com.telegrambot.backend.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an SOS alert for a trip.
 * Received from the Telegram bot via POST /api/sos.
 * Triggers emergency logging and notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SosDto {

    /**
     * The ID of the trip where the SOS was triggered.
     */
    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    /**
     * Optional message describing the emergency (e.g., "I feel unsafe").
     */
    @Size(max = 500, message = "SOS message must be at most 500 characters")
    private String message;
}