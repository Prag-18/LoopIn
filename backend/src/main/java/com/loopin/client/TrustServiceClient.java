package com.loopin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * REST Client in Service 1 for communicating with Service 2 (Trust Service).
 * Uses Spring Boot RestClient with base URL loaded from application.properties.
 * Implements graceful fallback with try/catch when Service 2 is unreachable.
 */
@Component
public class TrustServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(TrustServiceClient.class);

    private final RestClient restClient;
    private final String trustServiceUrl;

    public TrustServiceClient(@Value("${trust.service.url:http://localhost:8081}") String trustServiceUrl) {
        this.trustServiceUrl = trustServiceUrl;
        this.restClient = RestClient.builder()
                .baseUrl(trustServiceUrl)
                .build();
    }

    /**
     * Calls Service 2 to verify student trust standing and requested trust radius.
     * If Service 2 is offline/down, handles it gracefully by returning a safe fallback.
     */
    public TrustVerificationResponse verifyTrust(String studentEmail, int requestedRadius, String itemName) {
        TrustVerificationRequest request = new TrustVerificationRequest(studentEmail, requestedRadius, itemName);

        try {
            logger.info("Calling Microservice 2 at {}/api/trust/verify for student: {}", trustServiceUrl, studentEmail);

            TrustVerificationResponse response = restClient.post()
                    .uri("/api/trust/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TrustVerificationResponse.class);

            if (response != null) {
                logger.info("Received verification from Microservice 2: Score={}, Status={}", 
                        response.getTrustScore(), response.getStatusMessage());
                return response;
            }
        } catch (Exception ex) {
            logger.warn("Microservice 2 (Trust Service) is unreachable or returned error: {}. Executing graceful fallback.", 
                    ex.getMessage());
        }

        // Graceful Fallback Response when Service 2 is stopped / unavailable
        return new TrustVerificationResponse(
                studentEmail,
                false,
                50,
                "FALLBACK_SERVICE2_OFFLINE",
                1 // Safe default: 1st-degree only allowed during offline mode
        );
    }
}
