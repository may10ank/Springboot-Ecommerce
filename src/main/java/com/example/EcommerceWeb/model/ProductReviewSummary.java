package com.example.EcommerceWeb.model;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "product_reviews_summaries")
public class ProductReviewSummary {
    @Id
    private String id;
    private int productId;
    private String summary;
    private int reviewCountAtGeneration;
    private int newReviewsSinceLastSummary;
    private LocalDateTime generatedAt;
    private LocalDateTime lastUpdatedAt;
    public ProductReviewSummary() {}
    public ProductReviewSummary(int productId, String summary, int reviewCountAtGeneration) {
        this.productId = productId;
        this.summary = summary;
        this.reviewCountAtGeneration = reviewCountAtGeneration;
        this.newReviewsSinceLastSummary = 0;
        this.generatedAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public int getReviewCountAtGeneration() { return reviewCountAtGeneration; }
    public void setReviewCountAtGeneration(int reviewCountAtGeneration) { this.reviewCountAtGeneration = reviewCountAtGeneration; }

    public int getNewReviewsSinceLastSummary() { return newReviewsSinceLastSummary; }
    public void setNewReviewsSinceLastSummary(int newReviewsSinceLastSummary) { this.newReviewsSinceLastSummary = newReviewsSinceLastSummary; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
}

