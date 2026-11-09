package com.loopin.trust.controller;

import com.loopin.trust.dto.TrustVerificationRequest;
import com.loopin.trust.dto.TrustVerificationResponse;
import com.loopin.trust.model.TrustProfile;
import com.loopin.trust.service.TrustService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Presentation Layer REST Controller for Microservice 2 (Trust Service).
 * Exposes endpoints on port 8081 for inter-service communication and diagnostics.
 */
@RestController
@RequestMapping("/api/trust")
@CrossOrigin(origins = "*")
public class TrustController {

    private final TrustService trustService;

    public TrustController(TrustService trustService) {
        this.trustService = trustService;
    }

    /**
     * Inter-Service Verification Endpoint:
     * POST /api/trust/verify
     * Called by Service 1 (Core) to evaluate a borrow request.
     */
    @PostMapping("/verify")
    public ResponseEntity<TrustVerificationResponse> verifyTrust(@RequestBody TrustVerificationRequest request) {
        TrustVerificationResponse response = trustService.verifyBorrowingAttempt(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve trust profile for a student email
     * GET /api/trust/profile?email=alex.m@campus.edu
     */
    @GetMapping("/profile")
    public ResponseEntity<TrustProfile> getProfileByEmail(@RequestParam String email) {
        return ResponseEntity.ok(trustService.getProfileByEmail(email));
    }

    /**
     * Retrieve all community trust profiles
     * GET /api/trust/profiles
     */
    @GetMapping("/profiles")
    public ResponseEntity<List<TrustProfile>> getAllProfiles() {
        return ResponseEntity.ok(trustService.getAllProfiles());
    }

    /**
     * Retrieve trust service aggregate statistics
     * GET /api/trust/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(trustService.getTrustStatistics());
    }
}
