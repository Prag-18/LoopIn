package com.loopin.trust.service;

import com.loopin.trust.dto.TrustVerificationRequest;
import com.loopin.trust.dto.TrustVerificationResponse;
import com.loopin.trust.model.TrustProfile;
import com.loopin.trust.repository.TrustProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TrustServiceTest {

    private TrustProfileRepository trustProfileRepository;
    private TrustService trustService;

    @BeforeEach
    void setUp() {
        trustProfileRepository = Mockito.mock(TrustProfileRepository.class);
        trustService = new TrustServiceImpl(trustProfileRepository);
    }

    @Test
    @DisplayName("Service 2 Rule 1: High trust score (>= 60) student qualifies for campus-wide (radius 3)")
    void shouldApproveCampusWideRadiusForGoodStanding() {
        TrustProfile goodStudent = new TrustProfile(
                1L, "alex.m@campus.edu", 85, true, 5, 0, "GOLD"
        );
        when(trustProfileRepository.findByStudentEmail("alex.m@campus.edu"))
                .thenReturn(Optional.of(goodStudent));

        TrustVerificationRequest request = new TrustVerificationRequest("alex.m@campus.edu", 3, "Camera");
        TrustVerificationResponse response = trustService.verifyBorrowingAttempt(request);

        assertThat(response.isVerified()).isTrue();
        assertThat(response.getRecommendedMaxRadius()).isEqualTo(3);
        assertThat(response.getStatusMessage()).isEqualTo("VERIFIED_CAMPUS_MEMBER");
    }

    @Test
    @DisplayName("Service 2 Rule 1: Low trust score (< 60) or violations restrict campus-wide radius to 2")
    void shouldRestrictRadiusWhenTrustScoreIsLow() {
        TrustProfile riskyStudent = new TrustProfile(
                2L, "risk.user@campus.edu", 45, true, 1, 2, "BRONZE"
        );
        when(trustProfileRepository.findByStudentEmail("risk.user@campus.edu"))
                .thenReturn(Optional.of(riskyStudent));

        TrustVerificationRequest request = new TrustVerificationRequest("risk.user@campus.edu", 3, "Calculator");
        TrustVerificationResponse response = trustService.verifyBorrowingAttempt(request);

        assertThat(response.isVerified()).isFalse();
        assertThat(response.getRecommendedMaxRadius()).isEqualTo(2);
        assertThat(response.getStatusMessage()).isEqualTo("RADIUS_RESTRICTED_LOW_TRUST_SCORE");
    }

    @Test
    @DisplayName("Service 2 Rule 2: New academic email auto-provisions profile with baseline score 75")
    void shouldAutoProvisionNewAcademicStudent() {
        when(trustProfileRepository.findByStudentEmail("new.student@campus.edu"))
                .thenReturn(Optional.empty());

        TrustProfile savedMock = new TrustProfile(
                10L, "new.student@campus.edu", 75, true, 0, 0, "SILVER"
        );
        when(trustProfileRepository.save(any(TrustProfile.class))).thenReturn(savedMock);

        TrustVerificationRequest request = new TrustVerificationRequest("new.student@campus.edu", 1, "Book");
        TrustVerificationResponse response = trustService.verifyBorrowingAttempt(request);

        assertThat(response.isVerified()).isTrue();
        assertThat(response.getTrustScore()).isEqualTo(75);
    }
}
