package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.ProductReviewSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewSummaryRepository extends MongoRepository<ProductReviewSummary,String> {
    Optional<ProductReviewSummary> findByProductId(int productId);
}
