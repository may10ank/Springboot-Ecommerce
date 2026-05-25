package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.ProductVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVideoRepository extends JpaRepository<ProductVideo, Integer> {
    Optional<ProductVideo> findByProduct_ProductId(int productId);
}
