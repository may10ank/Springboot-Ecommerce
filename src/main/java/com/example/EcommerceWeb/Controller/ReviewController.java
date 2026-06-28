package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.RatingSummaryDTO;
import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.DTO.ReviewUpdateDTO;
import com.example.EcommerceWeb.Repository.OrderRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.ReviewService;
import com.example.EcommerceWeb.Service.ReviewSummaryService;
import com.example.EcommerceWeb.customException.UserNotFoundException;
import com.example.EcommerceWeb.model.Review;
import com.example.EcommerceWeb.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewSummaryService reviewSummaryService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewController(ReviewService reviewService, ReviewSummaryService reviewSummaryService, UserRepository userRepository, ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewService = reviewService;
        this.reviewSummaryService = reviewSummaryService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Review> addReview(Authentication authentication,
                                            @RequestParam int productId,
                                            @RequestParam int rating,
                                            @RequestParam(required = false) String comment){
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(reviewService.addReview(user.getId(), productId, rating, comment), HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProduct(@PathVariable int productId){
        return new ResponseEntity<>(reviewService.getReviewsByProduct(productId),HttpStatus.OK);
    }


    @PutMapping("/update/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable int reviewId, @RequestBody ReviewUpdateDTO request) {
        return new ResponseEntity<>(reviewService.updateReview(reviewId, request.getRating(),request.getComment()),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable int reviewId){
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>("Review Deleted successfully",HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(Authentication authentication) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(reviewService.getReviewsByUser(user.getId()),HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable int productId) {
        return new ResponseEntity<>(reviewService.getAverageRating(productId),HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/rating-summary")
    public ResponseEntity<RatingSummaryDTO> getRatingSummary(@PathVariable int productId) {
        return new ResponseEntity<>(reviewService.getRatingSummary(productId),HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(
            @PathVariable Integer productId,
            @RequestParam(required = false, defaultValue = "all") String sortBy,
            @RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "5")int size) {
        return new ResponseEntity<>(reviewService.getReviewsForProduct(productId,sortBy,page,size).getContent(),HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/rating-summary/distribution")
    public ResponseEntity<Map<String, Object>> getProductRatingSummary(@PathVariable int productId) {
        return new ResponseEntity<>(reviewService.getProductRatingWithDistribution(productId),HttpStatus.OK);
    }

    @GetMapping("/{productId}/summary")
    public ResponseEntity<String> summarizeReviews(@PathVariable int productId) {
        String summary = reviewSummaryService.reviewSummary(productId);
        return new ResponseEntity<>(summary,HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/my-review")
    public ResponseEntity<?> getMyReview(@PathVariable int productId,Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ReviewDTO review = reviewService.getUserReviewForProduct(user.getId(), productId);
        return new ResponseEntity<>(review,HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/can-review")
    public ResponseEntity<Boolean> canReview(@PathVariable int productId,Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean hasPurchased = orderRepository.existsByUserAndProductPurchased(user.getId(), productId);
        boolean hasReviewed = reviewService.hasUserReviewedProduct(user.getId(), productId);
        return ResponseEntity.ok(hasPurchased && !hasReviewed);
    }

    @GetMapping("/product/{productId}/debug-review")
    public ResponseEntity<?> debugReview(@PathVariable int productId,Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean hasPurchased = orderRepository.existsByUserAndProductPurchased(user.getId(), productId);
        boolean hasReviewed  = reviewService.hasUserReviewedProduct(user.getId(), productId);
        Map<String, Object> debug = new HashMap<>();
        debug.put("userId", user.getId());
        debug.put("productId", productId);
        debug.put("hasPurchased", hasPurchased);
        debug.put("hasReviewed", hasReviewed);
        debug.put("canReview", hasPurchased && !hasReviewed);
        return ResponseEntity.ok(debug);
    }
}
