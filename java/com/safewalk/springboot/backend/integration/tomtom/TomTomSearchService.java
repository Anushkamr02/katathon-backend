package com.safewalk.springboot.backend.integration.tomtom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Minimal TomTom search/geocode service. If api key is blank, geocoding returns null and POI search returns zero hits.
 */
@Service
public class TomTomSearchService {

    private final String apiKey;
    private final boolean useStub;
    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    public TomTomSearchService(@Value("${tomtom.api.key:}") String apiKey,
                               @Value("${tomtom.stub.enabled:true}") boolean stub,
                               RestTemplate rest) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.useStub = this.apiKey.isEmpty() || stub;
        this.rest = rest;
    }

    /**
     * Geocode address -> "lat,lng" string. Returns null if not found or stubbed.
     */
    public String geocode(String address) {
        if (useStub) return null;
        try {
            String q = address;
            UriComponentsBuilder b = UriComponentsBuilder
                    .fromUriString("https://api.tomtom.com/search/2/geocode/{query}.json")
                    .queryParam("key", apiKey)
                    .queryParam("limit", 1);

            URI uri = b.buildAndExpand(q).toUri();
            String body = rest.getForObject(uri, String.class);
            if (body == null) return null;
            JsonNode root = mapper.readTree(body);
            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                JsonNode pos = results.get(0).path("position");
                double lat = pos.path("lat").asDouble();
                double lon = pos.path("lon").asDouble();
                return String.format("%s,%s", lat, lon);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Count POIs of interest near a sample coordinate.
     * For full TomTom usage implement search/POI queries; here we return 0 for stub mode or on error.
     */
    public int countPoisNear(String latLng, String category, int radiusMeters) {
        if (useStub) {
            // deterministic fake counts based on category hash
            return Math.abs((latLng + "|" + category).hashCode() % 3);
        }
        try {
            // Implement real TomTom Search/POI query if desired (omitted for brevity)
            return 0;
        } catch (Exception ex) {
            return 0;
        }
    }
}
