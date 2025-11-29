package com.safewalk.springboot.backend.service.impl;

import com.safewalk.springboot.backend.dto.SosDto;
import com.safewalk.springboot.backend.entity.EmergencyContact;
import com.safewalk.springboot.backend.entity.Trip;
import com.safewalk.springboot.backend.entity.TripStatus;
import com.safewalk.springboot.backend.entity.User;
import com.safewalk.springboot.backend.exception.ResourceNotFoundException;
import com.safewalk.springboot.backend.repository.TripRepository;
import com.safewalk.springboot.backend.service.NotificationService;
import com.safewalk.springboot.backend.service.SosService;
import com.safewalk.springboot.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SosServiceImpl implements SosService {

    private static final Logger logger = LoggerFactory.getLogger(SosServiceImpl.class);

    private final TripRepository tripRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    public SosServiceImpl(TripRepository tripRepository,
                          NotificationService notificationService,
                          UserService userService) {
        this.tripRepository = tripRepository;
        this.notificationService = notificationService;
        this.userService = userService;
    }
    @Override
public void handleSos(SosDto sosDto) {
    if (sosDto == null) {
        logger.error("handleSos called with null request");
        throw new IllegalArgumentException("SosDto cannot be null");
    }

    logger.info("Handling generic SOS: tripId={}, telegramChatId={}",
            sosDto.getTripId(), sosDto.getTelegramChatId());

    // Simply reuse the main logic from triggerSos() so behavior stays consistent
    triggerSos(sosDto);
}


    /**
     * Triggers an SOS alert.
     */
    @Override
    @Transactional
    public Trip triggerSos(SosDto sosDto) {
        if (sosDto == null) {
            logger.error("triggerSos called with null SosDto");
            throw new IllegalArgumentException("sosDto is required");
        }

        logger.debug("triggerSos called with sosDto: {}", sosDto);

        try {
            // 1. Find and validate the trip
            Trip trip = tripRepository.findById(sosDto.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + sosDto.getTripId()));

            logger.debug("Trip found: id={}, status={}, telegramChatId={}",
                    trip.getId(),
                    trip.getStatus(),
                    trip.getTelegramChatId());

            // 2. Validate status: ensure TripStatus enum (defensive)
            if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
                logger.warn("SOS triggered on a non-active trip ID: {} with status: {}. Ignoring.",
                        trip.getId(), trip.getStatus());
                return trip;
            }

            // 3. Update trip status
            trip.setStatus(TripStatus.SOS_TRIGGERED);
            Trip savedTrip = tripRepository.save(trip);
            logger.info("Trip {} status updated to SOS_TRIGGERED", savedTrip.getId());

            // 4. Fetch user (by telegramChatId) and emergency contacts
            Optional<User> userOpt = Optional.empty();
            if (trip.getTelegramChatId() != null) {
                try {
                    userOpt = userService.findByTelegramChatId(trip.getTelegramChatId());
                } catch (Exception e) {
                    logger.warn("Error while fetching user by telegramChatId {}: {}", trip.getTelegramChatId(), e.toString());
                }
            }

            User user = userOpt.orElse(null);

            if (user == null) {
                logger.warn("No user found for trip {}. Will still record SOS but cannot notify contacts or user.", trip.getId());
            }

            List<EmergencyContact> contacts = (user != null && user.getEmergencyContacts() != null)
                    ? user.getEmergencyContacts()
                    : Collections.emptyList();

            logger.debug("Contacts to notify for trip {}: count={}", trip.getId(), contacts.size());

            // 5. Construct location string
            String locationLink = "No recent location available.";
            try {
                if (trip.getLatestLocation() != null) {
                    locationLink = formatLocationLink(
                            trip.getLatestLocation().getLatitude(),
                            trip.getLatestLocation().getLongitude()
                    );
                } else {
                    logger.debug("No latestLocation on Trip {}, getLatestLocation() returned null", trip.getId());
                }
            } catch (NoSuchMethodError nsme) {
                logger.warn("Trip.getLatestLocation() is unavailable: {}", nsme.toString());
            } catch (Exception ex) {
                logger.warn("Error while constructing location link for trip {}: {}", trip.getId(), ex.toString());
            }

            // 6. Build user message
            String userMessage = (sosDto.getMessage() != null && !sosDto.getMessage().isBlank())
                    ? "\nUser's message: " + sosDto.getMessage()
                    : "";

            String userName = (user != null && user.getName() != null) ? user.getName() : "Unknown user";
            String userPhone = (user != null && user.getPhone() != null) ? user.getPhone() : "unknown";

            String alertMessage = String.format(
                    "🚨 EMERGENCY SOS ALERT 🚨\n\n" +
                            "Your contact, %s, has triggered an SOS alert!\n\n" +
                            "Last Known Location: %s\n\n" +
                            "Contact their phone immediately: %s%s\n\n" +
                            "Please call local emergency services if you cannot reach them.",
                    userName,
                    locationLink,
                    userPhone,
                    userMessage
            );

            // 7. Dispatch notifications to emergency contacts
            for (EmergencyContact contact : contacts) {
                try {
                    logger.info("Queuing SOS alert to contact: {} ({}) for user {}", contact.getName(), contact.getPhone(), userName);
                    Map<String, Object> payload = Map.of(
                            "type", "sos_contact",
                            "tripId", savedTrip.getId(),
                            "contactName", contact.getName(),
                            "contactPhone", contact.getPhone(),
                            "message", alertMessage
                    );
                    notificationService.sendNotification(payload);
                } catch (Exception e) {
                    logger.error("Failed to notify contact {} for trip {}: {}", contact.getId(), trip.getId(), e.toString());
                }
            }

            // 8. Send confirmation to user (via notification service)
            try {
                Map<String, Object> userPayload = Map.of(
                        "type", "sos_user",
                        "tripId", savedTrip.getId(),
                        "telegramChatId", trip.getTelegramChatId(),
                        "message", "Your emergency contacts have been notified. Stay safe."
                );
                notificationService.sendNotification(userPayload);
            } catch (Exception e) {
                logger.warn("Failed to send confirmation to user for trip {}: {}", trip.getId(), e.toString());
            }

            logger.info("SOS flow completed for trip {}", trip.getId());
            return savedTrip;

        } catch (ResourceNotFoundException rnfe) {
            logger.error("triggerSos failed: {}", rnfe.getMessage());
            throw rnfe; // rethrow so caller knows
        } catch (Exception ex) {
            logger.error("Unexpected error in triggerSos: ", ex);
            throw new RuntimeException("Failed to trigger SOS", ex);
        }
    }

    /**
     * Cancels a trip.
     */
    @Override
    @Transactional
    public Trip cancelTrip(Long tripId, String reason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        if (trip.getStatus() != TripStatus.COMPLETED && trip.getStatus() != TripStatus.CANCELLED) {
            trip.setStatus(TripStatus.CANCELLED);
            try {
                // optional setter; if not present, ignore
                trip.setCancellationReason(reason);
            } catch (NoSuchMethodError nsme) {
                logger.warn("Trip.setCancellationReason() not present; skipping setting reason");
            } catch (Exception e) {
                logger.warn("Failed to set cancellation reason for trip {}: {}", tripId, e.toString());
            }

            Trip savedTrip = tripRepository.save(trip);
            logger.info("Trip ID {} cancelled. Reason: {}", tripId, reason);

            try {
                Map<String, Object> payload = Map.of(
                        "type", "trip_cancelled",
                        "tripId", tripId,
                        "reason", reason
                );
                notificationService.sendNotification(payload);
            } catch (Exception e) {
                logger.warn("Failed to notify user about cancellation for trip {}: {}", tripId, e.toString());
            }

            return savedTrip;
        }

        logger.warn("Attempted to cancel trip ID {} which was already in status: {}", tripId, trip.getStatus());
        return trip;
    }

    /* Helper to create Google Maps link from coordinates */
    private String formatLocationLink(Double lat, Double lng) {
        if (lat == null || lng == null) return "No recent location available.";
        return String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", lat, lng);
    }
}
