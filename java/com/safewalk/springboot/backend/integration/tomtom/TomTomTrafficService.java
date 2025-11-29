package com.safewalk.springboot.backend.integration.tomtom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Minimal Traffic service. If API is available, extend to query TomTom Traffic API.
 */
@Service
public class TomTomTrafficService {

    private final boolean useStub;

    public TomTomTrafficService(@Value("${tomtom.stub.enabled:true}") boolean stub,
                                @Value("${tomtom.api.key:}") String apiKey) {
        this.useStub = apiKey == null || apiKey.isBlank() || stub;
    }

    public Map<String, Object> getTraffic(String latLng) {
        if (useStub) {
            return Map.of("latLng", latLng, "congestion", "LOW", "speedKmph", 25.0);
        }
        // For production: call TomTom traffic endpoints and parse response
        return Map.of("latLng", latLng, "congestion", "UNKNOWN", "speedKmph", 0.0);
    }
}
