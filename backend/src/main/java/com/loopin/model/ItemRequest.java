package com.loopin.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Child JPA Entity representing a student's borrow request / attempt.
 * Follows the Unit 5 User/Attempt domain model pattern with a Many-to-One
 * relationship to the User parent entity.
 */
@Entity
@Table(name = "item_requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference
    private User user;

    @NotBlank(message = "Item name cannot be blank")
    @Column(name = "item_name", nullable = false)
    private String itemName;

    private String category;

    @Column(name = "requester_name")
    private String requesterName;

    @Column(name = "requester_email", nullable = false)
    private String requesterEmail;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "trust_radius", nullable = false)
    private int trustRadius; // 1 = 1st degree (friends), 2 = 2nd degree (friends of friends), 3 = Campus-wide

    @Column(nullable = false)
    private String status;    // OPEN, FULFILLED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "trust_verification_status")
    private String trustVerificationStatus; // Result returned from Microservice 2 (Trust Service)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ItemRequest() {
    }

    public ItemRequest(Long id, String itemName, String category, String requesterName,
                       String requesterEmail, LocalDate startDate, LocalDate endDate,
                       int trustRadius, String status, String note) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.requesterName = requesterName;
        this.requesterEmail = requesterEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.trustRadius = trustRadius;
        this.status = status;
        this.note = note;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "OPEN";
        }
        if (this.trustVerificationStatus == null) {
            this.trustVerificationStatus = "PENDING";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getTrustRadius() {
        return trustRadius;
    }

    public void setTrustRadius(int trustRadius) {
        this.trustRadius = trustRadius;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTrustVerificationStatus() {
        return trustVerificationStatus;
    }

    public void setTrustVerificationStatus(String trustVerificationStatus) {
        this.trustVerificationStatus = trustVerificationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ItemRequest{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", requesterName='" + requesterName + '\'' +
                ", requesterEmail='" + requesterEmail + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", trustRadius=" + trustRadius +
                ", status='" + status + '\'' +
                ", trustVerificationStatus='" + trustVerificationStatus + '\'' +
                '}';
    }
}
