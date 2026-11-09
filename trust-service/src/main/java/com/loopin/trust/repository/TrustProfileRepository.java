package com.loopin.trust.repository;

import com.loopin.trust.model.TrustProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for TrustProfile in Microservice 2.
 */
@Repository
public interface TrustProfileRepository extends JpaRepository<TrustProfile, Long> {

    // Derived Query Method 1: Find profile by student email
    Optional<TrustProfile> findByStudentEmail(String studentEmail);

    // Derived Query Method 2: Find all campus verified profiles
    List<TrustProfile> findByCampusVerifiedTrue();

    // Derived Query Method 3: Find by badge level
    List<TrustProfile> findByBadgeLevel(String badgeLevel);

    // Custom JPQL Query: Calculate average campus trust score
    @Query("SELECT AVG(t.trustScore) FROM TrustProfile t")
    Double getAverageCampusTrustScore();
}
