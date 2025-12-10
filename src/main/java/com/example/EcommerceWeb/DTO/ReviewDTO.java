package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Review;

import java.time.LocalDateTime;


public class ReviewDTO {
    private int rating;
    private String comment;
    private String username;
    private LocalDateTime createdAt;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ReviewDTO() {
    }

    public ReviewDTO(int id, int userId, int productId, int rating, String comment, String username, LocalDateTime createdAt) {
        this.rating = rating;
        this.comment = comment;
        this.username = username;
        this.createdAt = createdAt;
    }

    public static ReviewDTO reviewToDto(Review review){
        ReviewDTO reviewDTO=new ReviewDTO();
        reviewDTO.setComment(review.getComment());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setUsername(review.getUser().getUsername());
        reviewDTO.setCreatedAt(review.getCreatedAt());
        return reviewDTO;
    }
}
