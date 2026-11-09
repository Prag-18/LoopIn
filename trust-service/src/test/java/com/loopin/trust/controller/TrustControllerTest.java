package com.loopin.trust.controller;

import com.loopin.trust.dto.TrustVerificationRequest;
import com.loopin.trust.dto.TrustVerificationResponse;
import com.loopin.trust.service.TrustService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TrustControllerTest {

    private TrustService trustService;
    private TrustController trustController;

    @BeforeEach
    void setUp() {
        trustService = Mockito.mock(TrustService.class);
        trustController = new TrustController(trustService);
    }

    @Test
    @DisplayName("POST /api/trust/verify returns 200 OK and verification response")
    void shouldReturnVerificationResult() {
        TrustVerificationResponse mockResponse = new TrustVerificationResponse(
                "alex.m@campus.edu", true, 85, "VERIFIED_CAMPUS_MEMBER", 3
        );
        when(trustService.verifyBorrowingAttempt(any())).thenReturn(mockResponse);

        ResponseEntity<TrustVerificationResponse> response = trustController.verifyTrust(
                new TrustVerificationRequest("alex.m@campus.edu", 3, "Calculator")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isVerified()).isTrue();
        assertThat(response.getBody().getTrustScore()).isEqualTo(85);
    }
}
