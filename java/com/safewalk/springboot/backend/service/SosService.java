package com.safewalk.springboot.backend.service;

import com.safewalk.springboot.backend.dto.SosDto;
import com.safewalk.springboot.backend.entity.Trip;

/**
 * Service API for handling SOS events and trip cancellation.
 */
public interface SosService {

    /**
     * New: Generic SOS handler used by controller
     */
    void handleSos(SosDto sosDto);

    Trip triggerSos(SosDto sosDto);

    Trip cancelTrip(Long tripId, String reason);
}
