package com.telegrambot.backend.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a notification to be sent to a user.
 * Received from the safewalk backend via the notification webhook and forwarded to the Telegram user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    /**
     * The ID of the user to notify (e.g., Telegram chat ID).
     */
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    /**
     * The notification message to send.
     */
    @NotNull(message = "Message cannot be null")
    @Size(max = 1000, message = "Message must be at most 1000 characters")
    private String message;

    /**
     * Optional type of notification (e.g., "deviation", "sos").
     */
    private String type;

    /**
     * Optional trip ID associated with the notification.
     */
    private Long tripId;
}