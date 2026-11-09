package com.loopin.trust.service;

import com.loopin.trust.dto.TrustVerificationRequest;
import com.loopin.trust.dto.TrustVerificationResponse;
import com.loopin.trust.model.TrustProfile;

import java.util.List;
import java.util.Map;

public interface TrustService {

    TrustVerificationResponse verifyBorrowingAttempt(TrustVerificationRequest request);

    TrustProfile getProfileByEmail(String email);

    List<TrustProfile> getAllProfiles();

    Map<String, Object> getTrustStatistics();
}
