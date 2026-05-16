package com.example.EcommerceWeb.DTO;

import java.util.List;

public class ProductqarequestDTO {
    private int productId;
    private String productName;
    private String productDescription;
    private String brand;
    private String category;
    private int actualPrice;
    private int discountedPrice;
    private int discountPercent;
    private List<ReviewItem> reviews;
    private String question;
    private List<ChatMessage> chatHistory;

    public static class ReviewItem {
        private int rating;
        private String comment;

        public ReviewItem() {}
        public ReviewItem(int rating, String comment) {
            this.rating = rating;
            this.comment = comment;
        }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage() {}
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getActualPrice() { return actualPrice; }
    public void setActualPrice(int actualPrice) { this.actualPrice = actualPrice; }

    public int getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(int discountedPrice) { this.discountedPrice = discountedPrice; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public List<ReviewItem> getReviews() { return reviews; }
    public void setReviews(List<ReviewItem> reviews) { this.reviews = reviews; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<ChatMessage> getChatHistory() { return chatHistory; }
    public void setChatHistory(List<ChatMessage> chatHistory) { this.chatHistory = chatHistory; }

}
