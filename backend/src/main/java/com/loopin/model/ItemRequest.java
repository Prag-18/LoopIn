package com.loopin.model;

import java.time.LocalDate;

/**
 * Domain Entity representing a student's request to borrow an item
 * within the campus community trust circle.
 */
public class ItemRequest {

    private Long id;
    private String itemName;
    private String category;
    private String requesterName;
    private String requesterEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private int trustRadius; // 1 = 1st degree (friends), 2 = 2nd degree (friends of friends), 3 = Campus-wide
    private String status;    // OPEN, FULFILLED, CANCELLED
    private String note;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                '}';
    }
}
