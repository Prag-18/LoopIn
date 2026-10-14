package com.loopin.model;

/**
 * Domain Entity representing an item made available by a student lender
 * in the campus community.
 */
public class ItemListing {

    private Long id;
    private String title;
    private String category;
    private String ownerName;
    private String ownerEmail;
    private int minTrustDegree; // 1 = 1st degree only, 2 = up to 2nd degree, 3 = anyone on campus
    private boolean available;
    private String condition; // EXCELLENT, GOOD, FAIR

    public ItemListing() {
    }

    public ItemListing(Long id, String title, String category, String ownerName,
                       String ownerEmail, int minTrustDegree, boolean available, String condition) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.minTrustDegree = minTrustDegree;
        this.available = available;
        this.condition = condition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public int getMinTrustDegree() {
        return minTrustDegree;
    }

    public void setMinTrustDegree(int minTrustDegree) {
        this.minTrustDegree = minTrustDegree;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ItemListing{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", minTrustDegree=" + minTrustDegree +
                ", available=" + available +
                '}';
    }
}
