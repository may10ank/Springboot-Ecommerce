package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.RatingSummaryDTO;
import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.Review;
import com.example.EcommerceWeb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.StringConcatException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    public Review addReview(int userId, int productId, int rating, String comment){
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User"+userId+"not found"));
        Product product=productRepository.findById(productId).orElseThrow(()->new RuntimeException("Product"+productId+"not found"));
        Review review=new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        Review saved=reviewRepository.save(review);
        return saved;
    }

    public List<ReviewDTO> getReviewsByProduct(int productId){
        List<Review> review=reviewRepository.findByProductProductId(productId);
        return review.stream().map(ReviewDTO::reviewToDto).collect(Collectors.toList());
    }

    public Review updateReview(int reviewId, int rating, String comment){
        Review review=reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRating(rating);
        if(comment!=null) {
            review.setComment(comment);
        }
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public void deleteReview(int reviewId){
        Review review=reviewRepository.findById(reviewId).orElseThrow(()->new RuntimeException("review not found"));

        reviewRepository.delete(review);
    }

    public List<ReviewDTO> getReviewsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Review> reviews=reviewRepository.findByUser(user);
        return reviews.stream().map(ReviewDTO::reviewToDto).collect(Collectors.toList());
    }

    public Double getAverageRating(int productId){
        Product product=productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Double avgRating=reviewRepository.findAverageRatingByProduct(product);
        return avgRating!=null?avgRating:0.0;
    }

    public RatingSummaryDTO getRatingSummary(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Double avgRating = reviewRepository.findAverageRatingByProduct(product);
        int count = reviewRepository.countReviewsByProduct(product);
        return new RatingSummaryDTO(avgRating != null ? avgRating : 0.0, count);
    }

    public Page<ReviewDTO> getReviewsForProduct(int productId, String sortBy, int page, int size){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Sort sort=Sort.by("createdAt").descending();
        if("highest".equalsIgnoreCase(sortBy)) sort=Sort.by("rating").descending();
        else if("lowest".equalsIgnoreCase(sortBy)) sort=Sort.by("rating").ascending();
        Pageable pageable=PageRequest.of(page,size,sort);
        Page<Review> review= reviewRepository.findByProduct(product,pageable);
        return review.map(ReviewDTO::reviewToDto);
    }

    public Map<String, Object> getProductRatingWithDistribution(int productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Double avgRating = reviewRepository.findAverageRatingByProduct(product);
        int countReviews = reviewRepository.countReviewsByProduct(product);
        List<Object[]> distributionData=reviewRepository.getRatingDistribution(product);
        Map<Integer,Integer> distribution=new HashMap<>();
        for(Object[] row:distributionData){
            int rating=((Number)row[0]).intValue();
            int count= ((Number) row[1]).intValue();
            distribution.put(rating,count);
        }
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i,0);
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("productId", productId);
        summary.put("averageRating", avgRating != null ? avgRating : 0.0);
        summary.put("totalReviews", countReviews);
        summary.put("distribution", distribution);
        return summary;
    }
}
