package com.safewalk.springboot.backend.integration.tomtom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TomTom routing client. Uses real TomTom API when tomtom.api.key is set, otherwise returns deterministic stubs.
 * Expects source/destination as "lat,lng".
 */
@Service
public class TomTomRoutingService {

    private final String apiKey;
    private final boolean useStub;
    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    public TomTomRoutingService(@Value("${tomtom.api.key:}") String apiKey,
                                @Value("${tomtom.stub.enabled:true}") boolean stub,
                                RestTemplate rest) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.useStub = this.apiKey.isEmpty() || stub;
        this.rest = rest;
    }

    /**
     * Returns a list of route maps:
     * id (Integer), distanceMeters (Double), durationSec (Double), polyline (String)
     */
    public List<Map<String, Object>> getRoutes(String source, String destination, int alternatives) {
        if (useStub) return stubRoutes(source, destination);

        try {
            String coords = String.format("%s:%s", source, destination);
            UriComponentsBuilder b = UriComponentsBuilder
                    .fromUriString("https://api.tomtom.com/routing/1/calculateRoute/{coords}/json")
                    .queryParam("key", apiKey)
                    .queryParam("routeRepresentation", "polyline")
                    .queryParam("maxAlternatives", alternatives);

            URI uri = b.buildAndExpand(coords).toUri();
            String body = rest.getForObject(uri, String.class);
            if (body == null) return stubRoutes(source, destination);

            JsonNode root = mapper.readTree(body);
            JsonNode routes = root.path("routes");
            List<Map<String, Object>> out = new ArrayList<>();
            int idx = 1;
            if (routes.isArray()) {
                for (JsonNode r : routes) {
                    double distance = r.path("summary").path("lengthInMeters").asDouble(0.0);
                    double duration = r.path("summary").path("travelTimeInSeconds").asDouble(0.0);
                    String polyline = "";
                    JsonNode sections = r.path("sections");
                    if (sections.isArray() && sections.size() > 0) {
                        polyline = sections.get(0).path("polyline").asText("");
                    }
                    out.add(Map.of("id", idx++, "distanceMeters", distance, "durationSec", duration, "polyline", polyline));
                }
            }
            if (out.isEmpty()) return stubRoutes(source, destination);
            return out;
        } catch (Exception ex) {
            return stubRoutes(source, destination);
        }
    }

    private List<Map<String, Object>> stubRoutes(String source, String destination) {
        // Deterministic stubs depending on input hash to vary distances
        int base = Math.abs((source + "|" + destination).hashCode() % 500) + 1000;
        return List.of(
                Map.of("id", 1, "distanceMeters", (double) base, "durationSec", (double) (base / 2), "polyline", ""),
                Map.of("id", 2, "distanceMeters", (double) (base + 300), "durationSec", (double) ((base + 300) / 2), "polyline", ""),
                Map.of("id", 3, "distanceMeters", (double) (base + 600), "durationSec", (double) ((base + 600) / 2), "polyline", "")
        );
    }
}
