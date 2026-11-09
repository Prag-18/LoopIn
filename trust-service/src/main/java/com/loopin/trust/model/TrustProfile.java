package com.loopin.trust.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * JPA Entity managed by Microservice 2 (Trust Service).
 * Stores community standing, verified campus badge, trust score, and violation counts.
 */
@Entity
@Table(name = "trust_profiles")
public class TrustProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student email is mandatory")
    @Column(name = "student_email", nullable = false, unique = true)
    private String studentEmail;

    @Column(name = "trust_score", nullable = false)
    private int trustScore; // 0 to 100

    @Column(name = "campus_verified", nullable = false)
    private boolean campusVerified = true;

    @Column(name = "completed_loans", nullable = false)
    private int completedLoans = 0;

    @Column(name = "violation_count", nullable = false)
    private int violationCount = 0;

    @Column(name = "badge_level")
    private String badgeLevel; // GOLD, SILVER, BRONZE

    @Column(name = "last_evaluated_at")
    private LocalDateTime lastEvaluatedAt;

    public TrustProfile() {
    }

    public TrustProfile(Long id, String studentEmail, int trustScore, boolean campusVerified,
                        int completedLoans, int violationCount, String badgeLevel) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.trustScore = trustScore;
        this.campusVerified = campusVerified;
        this.completedLoans = completedLoans;
        this.violationCount = violationCount;
        this.badgeLevel = badgeLevel;
        this.lastEvaluatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastEvaluatedAt = LocalDateTime.now();
        if (this.badgeLevel == null) {
            updateBadge();
        }
    }

    public void updateBadge() {
        if (trustScore >= 85) {
            this.badgeLevel = "GOLD";
        } else if (trustScore >= 70) {
            this.badgeLevel = "SILVER";
        } else {
            this.badgeLevel = "BRONZE";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(int trustScore) {
        this.trustScore = trustScore;
        updateBadge();
    }

    public boolean isCampusVerified() {
        return campusVerified;
    }

    public void setCampusVerified(boolean campusVerified) {
        this.campusVerified = campusVerified;
    }

    public int getCompletedLoans() {
        return completedLoans;
    }

    public void setCompletedLoans(int completedLoans) {
        this.completedLoans = completedLoans;
    }

    public int getViolationCount() {
        return violationCount;
    }

    public void setViolationCount(int violationCount) {
        this.violationCount = violationCount;
    }

    public String getBadgeLevel() {
        return badgeLevel;
    }

    public void setBadgeLevel(String badgeLevel) {
        this.badgeLevel = badgeLevel;
    }

    public LocalDateTime getLastEvaluatedAt() {
        return lastEvaluatedAt;
    }

    public void setLastEvaluatedAt(LocalDateTime lastEvaluatedAt) {
        this.lastEvaluatedAt = lastEvaluatedAt;
    }
}
