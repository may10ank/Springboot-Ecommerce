package com.example.EcommerceWeb.DTO;

import io.lettuce.core.protocol.CommandHandler;

import java.util.List;

public class ReviewSummaryRequestDto {

    private int productId;
    private List<ReviewItem> reviews;

    public ReviewSummaryRequestDto() {
    }

    public ReviewSummaryRequestDto(int productId, List<ReviewItem> reviews) {
        this.productId = productId;
        this.reviews = reviews;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public List<ReviewItem> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewItem> reviews) {
        this.reviews = reviews;
    }

    public static class ReviewItem{
        private int rating;
        private String comment;

        public ReviewItem() {
        }

        public ReviewItem(int rating, String comment) {
            this.rating = rating;
            this.comment = comment;
        }

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
    }
}
