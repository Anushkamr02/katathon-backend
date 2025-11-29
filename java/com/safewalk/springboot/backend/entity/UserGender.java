package com.safewalk.springboot.backend.entity;

/**
 * Enumeration for user gender.
 * * Used in the User entity and can be utilized in the SafetyScoringService
 * for personalized risk assessment.
 */
public enum UserGender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}