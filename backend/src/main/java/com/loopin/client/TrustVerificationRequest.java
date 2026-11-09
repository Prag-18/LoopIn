package com.loopin.client;

/**
 * Request DTO sent from Service 1 (Core) to Service 2 (Trust Service)
 * across network over REST.
 */
public class TrustVerificationRequest {

    private String studentEmail;
    private int requestedRadius;
    private String itemName;

    public TrustVerificationRequest() {
    }

    public TrustVerificationRequest(String studentEmail, int requestedRadius, String itemName) {
        this.studentEmail = studentEmail;
        this.requestedRadius = requestedRadius;
        this.itemName = itemName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getRequestedRadius() {
        return requestedRadius;
    }

    public void setRequestedRadius(int requestedRadius) {
        this.requestedRadius = requestedRadius;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
