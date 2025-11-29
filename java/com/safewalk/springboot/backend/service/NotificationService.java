package com.safewalk.springboot.backend.service;

import java.util.Map;

/**
 * Notification contract used by services to send signed notifications
 * to downstream systems (Telegram backend).
 */
public interface NotificationService {

    /**
     * Send a generic payload (will be signed with HMAC).
     */
    void sendNotification(Map<String,Object> payload);

    /**
     * Convenience: send a short text message to the user associated with tripId.
     */
    void sendNotificationToUser(Long tripId, String message);

    /**
     * Convenience: send SOS message to contacts (used by SosService/impl)
     */
    void sendSosAlertToContacts(Long tripId, String message);
}
