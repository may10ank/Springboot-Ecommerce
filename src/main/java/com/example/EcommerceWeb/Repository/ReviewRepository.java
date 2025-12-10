package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.Review;
import com.example.EcommerceWeb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findByProductProductId(int productId);
    List<Review> findByUser(User user);
    Page<Review> findByProduct(Product product, Pageable pageable);
    @Query("SELECT r.rating,count(r) FROM Review r WHERE r.product= :product GROUP BY r.rating")
    List<Object[]> getRatingDistribution(@Param("product") Product product);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product= :product")
    Double findAverageRatingByProduct(@Param("product")Product product);
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product")
    int countReviewsByProduct(@Param("product")Product product);

}
