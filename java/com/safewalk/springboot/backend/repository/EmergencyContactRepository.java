package com.safewalk.springboot.backend.repository;

import com.safewalk.springboot.backend.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for the EmergencyContact entity.
 * * Provides database access for managing a user's emergency contacts.
 */
@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
}