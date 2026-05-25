package com.example.EcommerceWeb.model;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "product_qa_cache")
public class ProductQAData {
    @Id
    private String id;
    private int productId;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    public ProductQAData(){};

    public ProductQAData(int productId, String question, String answer) {
        this.productId = productId;
        this.question = question;
        this.answer = answer;
        this.createdAt=LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
