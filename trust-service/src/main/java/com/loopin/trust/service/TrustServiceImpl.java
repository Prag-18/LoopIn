package com.loopin.trust.service;

import com.loopin.trust.dto.TrustVerificationRequest;
import com.loopin.trust.dto.TrustVerificationResponse;
import com.loopin.trust.model.TrustProfile;
import com.loopin.trust.repository.TrustProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class TrustServiceImpl implements TrustService {

    private static final Logger logger = LoggerFactory.getLogger(TrustServiceImpl.class);

    private final TrustProfileRepository trustProfileRepository;

    public TrustServiceImpl(TrustProfileRepository trustProfileRepository) {
        this.trustProfileRepository = trustProfileRepository;
    }

    /**
     * Executes Trust Verification for a borrow request attempt.
     * Enforces Business Rule 1 (Radius eligibility) and Business Rule 2 (Profile provisioning).
     */
    @Override
    @Transactional
    public TrustVerificationResponse verifyBorrowingAttempt(TrustVerificationRequest request) {
        if (request == null || request.getStudentEmail() == null || request.getStudentEmail().isBlank()) {
            throw new IllegalArgumentException("Student email is mandatory for trust verification.");
        }

        String email = request.getStudentEmail().trim().toLowerCase();
        int requestedRadius = request.getRequestedRadius();

        // Business Rule 2: Auto-provision profile with domain-based baseline score if new
        TrustProfile profile = trustProfileRepository.findByStudentEmail(email).orElseGet(() -> {
            boolean isAcademic = email.endsWith(".edu") || email.endsWith(".ac.in");
            int initialScore = isAcademic ? 75 : 50;
            TrustProfile newProfile = new TrustProfile(
                    null, email, initialScore, isAcademic, 0, 0, isAcademic ? "SILVER" : "BRONZE"
            );
            return trustProfileRepository.save(newProfile);
        });

        // Business Rule 1: Trust Radius Eligibility Rule
        // Campus-wide search (radius = 3) requires a minimum trust score of 60 and 0 violations
        int maxAllowedRadius;
        String statusMessage;
        boolean verified;

        if (requestedRadius == 3 && (profile.getTrustScore() < 60 || profile.getViolationCount() > 0)) {
            maxAllowedRadius = 2; // Downgrade to 2nd degree
            statusMessage = "RADIUS_RESTRICTED_LOW_TRUST_SCORE";
            verified = false;
            logger.warn("Student {} trust score ({}) insufficient for campus-wide radius. Capped at degree {}.",
                    email, profile.getTrustScore(), maxAllowedRadius);
        } else {
            maxAllowedRadius = 3;
            statusMessage = "VERIFIED_CAMPUS_MEMBER";
            verified = true;
        }

        return new TrustVerificationResponse(
                profile.getStudentEmail(),
                verified,
                profile.getTrustScore(),
                statusMessage,
                maxAllowedRadius
        );
    }

    @Override
    public TrustProfile getProfileByEmail(String email) {
        return trustProfileRepository.findByStudentEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("Trust profile not found for email: " + email));
    }

    @Override
    public List<TrustProfile> getAllProfiles() {
        return trustProfileRepository.findAll();
    }

    @Override
    public Map<String, Object> getTrustStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        long totalProfiles = trustProfileRepository.count();
        long verifiedProfiles = trustProfileRepository.findByCampusVerifiedTrue().size();
        Double avgScore = trustProfileRepository.getAverageCampusTrustScore();

        stats.put("totalProfiles", totalProfiles);
        stats.put("campusVerifiedMembers", verifiedProfiles);
        stats.put("averageTrustScore", avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);

        return stats;
    }
}
