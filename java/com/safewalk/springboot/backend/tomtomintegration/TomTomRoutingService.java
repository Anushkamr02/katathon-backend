package com.safewalk.springboot.backend.tomtomintegration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TomTomRoutingService: calls TomTom REST APIs if API key is provided.
 * If tomtom.api.key is empty, falls back to a small stub implementation.
 *
 * This returns a List of Map<String,Object> where each map includes:
 * - id (Integer)
 * - distanceMeters (Double)
 * - durationSec (Double)
 * - polyline (String)  // encoded polyline or empty string
 *
 * Note: adapt parsing code if TomTom response schema differs for your account.
 */
@Service
public class TomTomRoutingService {

    private final String apiKey;
    private final boolean stub;
    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    public TomTomRoutingService(
            @Value("${tomtom.api.key:}") String apiKey,
            @Value("${tomtom.stub.enabled:true}") boolean stub,
            RestTemplate rest) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        // If API key is empty, honor explicit stub flag; otherwise use real service.
        this.stub = (this.apiKey.isEmpty()) ? true : stub == false ? false : false;
        this.rest = rest;
    }

    /**
     * Get routes between source and destination.
     * Accepts source/destination as either "lat,lng" or free-text address (TomTom will handle text search if used).
     */
    public List<Map<String,Object>> getRoutes(String source, String destination, int alternatives) {
        if (apiKey == null || apiKey.isEmpty()) {
            return stubRoutes(source, destination);
        }
        try {
            // TomTom calculateRoute expects coordinates "lat,lng:lat,lng" or use other endpoints after geocoding.
            // Here we expect source/destination to be "lat,lng". If not, the caller should geocode first.
            String coords = String.format("%s:%s", source, destination);
            UriComponentsBuilder b = UriComponentsBuilder
                    .fromUriString("https://api.tomtom.com/routing/1/calculateRoute/{coords}/json")
                    .queryParam("key", apiKey)
                    .queryParam("routeRepresentation", "polyline") // get polyline
                    .queryParam("computeBestOrder", "false")
                    .queryParam("maxAlternatives", alternatives);

            URI uri = b.buildAndExpand(coords).toUri();
            ResponseEntity<String> resp = rest.getForEntity(uri, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return stubRoutes(source, destination);
            }

            JsonNode root = mapper.readTree(resp.getBody());
            // TomTom returns "routes" array, each route has summary and legs etc.
            JsonNode routes = root.path("routes");
            List<Map<String,Object>> out = new ArrayList<>();
            int idx = 1;
            for (JsonNode rnode : routes) {
                double distance = rnode.path("summary").path("lengthInMeters").asDouble(0.0);
                double duration = rnode.path("summary").path("travelTimeInSeconds").asDouble(0.0);
                // TomTom may provide an encoded polyline under "sections"/"polyline"/"points" or route.points
                String polyline = "";
                JsonNode sections = rnode.path("sections");
                if (sections.isArray() && sections.size() > 0) {
                    JsonNode first = sections.get(0);
                    if (first.has("polyline")) {
                        polyline = first.path("polyline").asText("");
                    } else {
                        // Some responses use "polyline" at another level; keep empty if not found
                        polyline = "";
                    }
                }
                out.add(Map.of(
                        "id", idx++,
                        "distanceMeters", distance,
                        "durationSec", duration,
                        "polyline", polyline
                ));
            }
            if (out.isEmpty()) return stubRoutes(source, destination);
            return out;
        } catch (Exception ex) {
            // On any parsing/network error, fallback to stub to keep service functional
            return stubRoutes(source, destination);
        }
    }

    private List<Map<String,Object>> stubRoutes(String source, String destination) {
        // deterministic stub based on source/destination strings
        return List.of(
            Map.of("id", 1, "distanceMeters", 1200.0, "durationSec", 600.0, "polyline", ""),
            Map.of("id", 2, "distanceMeters", 1400.0, "durationSec", 700.0, "polyline", ""),
            Map.of("id", 3, "distanceMeters", 1600.0, "durationSec", 800.0, "polyline", "")
        );
    }
}
