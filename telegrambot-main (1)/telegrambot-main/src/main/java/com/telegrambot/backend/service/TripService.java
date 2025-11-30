package com.telegrambot.backend.service;

import com.telegrambot.backend.dto.SosDto;
import com.telegrambot.backend.dto.TripCreationDto;
import com.telegrambot.backend.dto.RouteOptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Service responsible for all communication with the external SafeWalk Core
 * backend
 * related to trip creation, retrieval, and actions (SOS, End Trip).
 *
 * It uses RestTemplate for synchronous HTTP calls and HmacUtil for request
 * signing.
 */
@Service
public class TripService {

    private static final Logger logger = LoggerFactory.getLogger(TripService.class);

    private final RestTemplate restTemplate;
    private final HmacUtil hmacUtil;

    // Base URL for the external SafeWalk API, injected from application.properties
    @Value("${safewalk.api.base-url:http://localhost:8081}")
    private String apiBaseUrl;

    public TripService(RestTemplate restTemplate, HmacUtil hmacUtil) {
        this.restTemplate = restTemplate;
        this.hmacUtil = hmacUtil;
    }

    /**
     * Creates a new trip in the SafeWalk Core backend.
     *
     * @param tripDto The details for the trip to be created.
     * @return The ID of the newly created trip, or null if the creation fails.
     */
    public Long createTrip(TripCreationDto tripDto) {
        final String uri = apiBaseUrl + "/api/trips";
        try {
            // 1. Create JSON payload and sign it
            String jsonPayload = hmacUtil.convertObjectToJson(tripDto);

            String signature = hmacUtil.generateSignature(jsonPayload);

            // 2. Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Signature", signature);

            // 3. Send request
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            ResponseEntity<Long> response = restTemplate.postForEntity(uri, entity, Long.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Trip created successfully. Trip ID: {}", response.getBody());
                return response.getBody();
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                logger.error(
                        "Conflict (409) when creating trip - User may already have an active trip or duplicate request: {}",
                        e.getMessage());
            } else {
                logger.error("HTTP error {} when creating trip: {}", e.getStatusCode(), e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to create trip with SafeWalk Core backend: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Triggers an SOS alert for an active trip.
     *
     * @param sosDto The SOS payload.
     */
    public void triggerSos(SosDto sosDto) {
        final String uri = apiBaseUrl + "/api/sos";
        try {
            String jsonPayload = hmacUtil.convertObjectToJson(sosDto);
            String signature = hmacUtil.generateSignature(jsonPayload);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Signature", signature);

            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            restTemplate.postForEntity(uri, entity, Void.class);
            logger.info("SOS triggered successfully for Trip ID: {}", sosDto.getTripId());

        } catch (Exception e) {
            logger.error("Failed to trigger SOS for Trip {} with SafeWalk Core backend: {}", sosDto.getTripId(),
                    e.getMessage(), e);
        }
    }

    /**
     * Notifies the SafeWalk Core backend that a trip has ended.
     *
     * @param tripId The ID of the trip to end.
     */
    public void endTrip(Long tripId) {
        final String uri = apiBaseUrl + "/api/trips/{tripId}/end";
        try {
            // Note: DELETE request usually doesn't have a body, so we only sign the
            // URL/path if needed.
            // For simplicity, we assume no body and no signing of the URL path is required
            // here.
            restTemplate.delete(uri, tripId);
            logger.info("Trip ID: {} ended successfully with SafeWalk Core backend.", tripId);
        } catch (Exception e) {
            logger.error("Failed to end Trip {} with SafeWalk Core backend: {}", tripId, e.getMessage(), e);
        }
    }

    /**
     * Retrieves route options for a given trip creation request
     * (Source/Destination).
     *
     * @param tripDto The trip request details (used to calculate routes).
     * @return A list of potential routes, or an empty list on failure.
     */
    public List<RouteOptionDto> getRouteOptions(TripCreationDto tripDto) {
        final String uri = apiBaseUrl + "/api/trips/route-options";
        try {
            // 1. Create JSON payload and sign it
            String jsonPayload = hmacUtil.convertObjectToJson(tripDto);
            String signature = hmacUtil.generateSignature(jsonPayload);

            // 2. Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Signature", signature);

            // 3. Send request
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

            // Use exchange for a POST that returns a list of objects
            ResponseEntity<List<RouteOptionDto>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<RouteOptionDto>>() {
                    });

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody(); // ✔ No cast needed
            }
        } catch (Exception e) {
            logger.error("Failed to fetch route options from SafeWalk Core backend: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}