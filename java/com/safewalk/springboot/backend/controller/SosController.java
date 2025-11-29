package com.safewalk.springboot.backend.controller;

import com.safewalk.springboot.backend.dto.SosDto;
import com.safewalk.springboot.backend.service.SosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sos")

public class SosController {

    private static final Logger log = LoggerFactory.getLogger(SosController.class);

    private final SosService sosService;

    @PostMapping
    public ResponseEntity<?> sendSos(@Valid @RequestBody SosDto sosDto) {
        // Minimal stub implementation: call service if available, return 200
        try {
            sosService.handleSos(sosDto); // assumes service has handleSos(SosDto)
        } catch (Exception ex) {
            log.error("Error handling SOS", ex);
            return ResponseEntity.internalServerError().body("Failed to process SOS");
        }
        return ResponseEntity.ok().build();
    }
}
