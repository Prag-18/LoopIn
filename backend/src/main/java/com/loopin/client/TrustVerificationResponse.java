package com.loopin.client;

/**
 * Response DTO returned by Service 2 (Trust Service) to Service 1 (Core).
 */
public class TrustVerificationResponse {

    private String studentEmail;
    private boolean verified;
    private int trustScore;
    private String statusMessage;
    private int recommendedMaxRadius;

    public TrustVerificationResponse() {
    }

    public TrustVerificationResponse(String studentEmail, boolean verified, int trustScore,
                                     String statusMessage, int recommendedMaxRadius) {
        this.studentEmail = studentEmail;
        this.verified = verified;
        this.trustScore = trustScore;
        this.statusMessage = statusMessage;
        this.recommendedMaxRadius = recommendedMaxRadius;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(int trustScore) {
        this.trustScore = trustScore;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getRecommendedMaxRadius() {
        return recommendedMaxRadius;
    }

    public void setRecommendedMaxRadius(int recommendedMaxRadius) {
        this.recommendedMaxRadius = recommendedMaxRadius;
    }

    @Override
    public String toString() {
        return "TrustVerificationResponse{" +
                "studentEmail='" + studentEmail + '\'' +
                ", verified=" + verified +
                ", trustScore=" + trustScore +
                ", statusMessage='" + statusMessage + '\'' +
                ", recommendedMaxRadius=" + recommendedMaxRadius +
                '}';
    }
}
