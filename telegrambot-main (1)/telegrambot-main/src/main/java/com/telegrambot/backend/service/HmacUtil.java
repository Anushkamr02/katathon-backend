package com.telegrambot.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for handling HMAC-SHA256 signature generation and verification.
 * This is crucial for securing communication between the SafeWalk Telegram Bot
 * and the external SafeWalk Core backend system.
 *
 * It uses a shared secret key (configured in application.properties) to sign
 * outgoing request payloads and verify incoming webhook payloads.
 */
@Component
public class HmacUtil {

    private static final Logger logger = LoggerFactory.getLogger(HmacUtil.class);
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    // Shared secret key for HMAC, injected from application.properties
    @Value("${safewalk.core.hmac-secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;

    /**
     * Constructor for dependency injection. ObjectMapper is used for converting objects to JSON.
     */
    public HmacUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Converts a Java object (DTO) into a standardized JSON string.
     * This JSON string is used as the payload for HMAC signature generation.
     *
     * @param object The Java object to serialize.
     * @return The JSON string representation.
     * @throws JsonProcessingException if serialization fails.
     */
    public String convertObjectToJson(Object object) throws JsonProcessingException {
        // We use the ObjectMapper to convert the object to a JSON string.
        // It's crucial for security that this serialization is deterministic
        // (i.e., fields are always ordered the same way) across both systems.
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Generates an HMAC-SHA256 signature for a given JSON payload.
     * The output signature is Base64 encoded.
     *
     * @param payload The raw JSON payload string to sign.
     * @return The Base64 encoded HMAC signature.
     */
    public String generateSignature(String payload) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            // Base64 encode the resulting signature bytes
            return Base64.getEncoder().encodeToString(rawHmac);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Error generating HMAC signature: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize HMAC algorithm or key.", e);
        }
    }

    /**
     * Verifies an incoming signature against a raw payload.
     * Used for validating webhooks from the SafeWalk Core backend.
     *
     * @param payload The raw JSON payload received.
     * @param signature The Base64 encoded signature received in the 'X-Signature' header.
     * @return true if the signature matches the payload, false otherwise.
     */
    public boolean verifySignature(String payload, String signature) {
        if (signature == null || signature.isEmpty()) {
            return false;
        }

        String calculatedSignature = generateSignature(payload);
        
        // Use constant time comparison to prevent timing attacks
        return safeEquals(calculatedSignature, signature);
    }
    
    /**
     * Compares two strings in constant time to mitigate timing attacks.
     *
     * @param a The first string.
     * @param b The second string.
     * @return True if strings are equal, false otherwise.
     */
    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}