package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.ProductQAData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductQADataRepository extends MongoRepository<ProductQAData, String> {
    Optional<ProductQAData> findByProductIdAndQuestion(int productId, String question);
}
