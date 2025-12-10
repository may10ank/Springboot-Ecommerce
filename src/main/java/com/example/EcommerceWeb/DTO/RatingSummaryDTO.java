package com.example.EcommerceWeb.DTO;

public class RatingSummaryDTO {
    private Double averageRating;
    private int totalReview;

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReview() {
        return totalReview;
    }

    public void setTotalReview(int totalReview) {
        this.totalReview = totalReview;
    }

    public RatingSummaryDTO(Double averageRating, int totalReview) {
        this.averageRating = averageRating;
        this.totalReview = totalReview;
    }

    public RatingSummaryDTO(){

    }
}
