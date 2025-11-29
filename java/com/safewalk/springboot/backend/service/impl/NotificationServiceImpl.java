package com.safewalk.springboot.backend.service.impl;

import com.safewalk.springboot.backend.middleware.HmacUtil;
import com.safewalk.springboot.backend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Simple implementation that posts signed JSON payloads to the telegram backend.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String sharedSecret;
    private final String telegramBackendUrl;

    public NotificationServiceImpl(RestTemplate restTemplate,
                                   ObjectMapper objectMapper,
                                   @Value("${webhook.shared.secret:supersecret_local}") String sharedSecret,
                                   @Value("${telegram.backend.url:http://localhost:8081}") String telegramBackendUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.sharedSecret = sharedSecret;
        this.telegramBackendUrl = telegramBackendUrl;
    }

    @Override
    public void sendNotification(Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            String sig = HmacUtil.signHex(sharedSecret, json);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Signature", sig);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            String url = telegramBackendUrl + "/webhook/notifications";
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
            // safe check: ResponseEntity#getStatusCodeValue exists; if your environment flagged it,
            // use resp.getStatusCode().value()
            log.info("Notification posted, status={}", resp.getStatusCode().value());
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }

    @Override
    public void sendNotificationToUser(Long tripId, String message) {
        if (tripId == null) {
            log.warn("sendNotificationToUser called with null tripId; ignore");
            return;
        }
        try {
            Map<String,Object> payload = Map.of(
                    "type", "message_to_user",
                    "tripId", tripId,
                    "message", message
            );
            sendNotification(payload);
        } catch (Exception e) {
            log.error("Failed to sendNotificationToUser for tripId={}", tripId, e);
        }
    }

    @Override
    public void sendSosAlertToContacts(Long tripId, String message) {
        try {
            Map<String,Object> payload = Map.of(
                    "type", "sos_contacts",
                    "tripId", tripId,
                    "message", message
            );
            sendNotification(payload);
        } catch (Exception e) {
            log.error("Failed to sendSosAlertToContacts for tripId={}", tripId, e);
        }
    }
}
