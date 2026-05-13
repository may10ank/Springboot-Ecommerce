package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.DTO.ReviewSummaryRequestDto;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ProductReviewSummaryRepository;
import com.example.EcommerceWeb.model.ProductReviewSummary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewSummaryService {
     private final ReviewService reviewService;
     private final ProductRepository productRepository;
     private final ProductReviewSummaryRepository summaryRepository;
     private final RestTemplate restTemplate;
     private static final int NEW_REVIEWS_THRESHOLD=100;
     private static final String PYTHON_SUMMARY_URL="http://localhost:8000/summarize";

    public ReviewSummaryService(ReviewService reviewService, ProductRepository productRepository,RestTemplate restTemplate,ProductReviewSummaryRepository summaryRepository) {
        this.restTemplate =restTemplate;
        this.reviewService = reviewService;
        this.productRepository = productRepository;
        this.summaryRepository=summaryRepository;
    }

    public String reviewSummary(int productId) {
        Optional<ProductReviewSummary> existing = summaryRepository.findByProductId(productId);
        if (existing.isPresent()) {
            return existing.get().getSummary();
        }
        return generateAndSave(productId,null);
    }

    public void onNewReviewAdded(int productId){
        Optional<ProductReviewSummary> existing = summaryRepository.findByProductId(productId);
        if (existing.isEmpty()) {
            return;
        }
        ProductReviewSummary doc = existing.get();
        int updatedCount = doc.getNewReviewsSinceLastSummary() + 1;
        if (updatedCount >= NEW_REVIEWS_THRESHOLD) {
            generateAndSave(productId, doc);
        } else {
            doc.setNewReviewsSinceLastSummary(updatedCount);
            doc.setLastUpdatedAt(LocalDateTime.now());
            summaryRepository.save(doc);
        }
    }

    private String generateAndSave(int productId,ProductReviewSummary existing) {
        List<ReviewDTO> reviewDTOs =reviewService.getReviewsByProduct(productId);
        if (reviewDTOs  == null || reviewDTOs .isEmpty()) {
            return "No reviews available for this product.";
        }
        List<ReviewSummaryRequestDto.ReviewItem> items=reviewDTOs.stream()
                .filter(r->r.getComment()!=null && !r.getComment().isBlank())
                .map(r->new ReviewSummaryRequestDto.ReviewItem(r.getRating(),r.getComment()))
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            return "No text reviews available to summarize.";
        }

        String summary=callPythonService(productId,items);
        if(existing==null) {
            ProductReviewSummary newSummary = new ProductReviewSummary(productId, summary, reviewDTOs.size());
            summaryRepository.save(newSummary);
        }else {
            existing.setSummary(summary);
            existing.setReviewCountAtGeneration(reviewDTOs.size());
            existing.setNewReviewsSinceLastSummary(0);
            existing.setLastUpdatedAt(LocalDateTime.now());
            summaryRepository.save(existing);
        }
        return summary;
    }

private String callPythonService(int productId,List<ReviewSummaryRequestDto.ReviewItem> items) {
    ReviewSummaryRequestDto requestBody=new ReviewSummaryRequestDto(productId,items);
    HttpHeaders headers=new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ReviewSummaryRequestDto> entity = new HttpEntity<>(requestBody, headers);
    try{
        ResponseEntity<String> response=restTemplate.exchange(PYTHON_SUMMARY_URL,HttpMethod.POST,entity,String.class);
        return response.getBody();
    }catch (Exception e){
        throw new RuntimeException("Failed to get summary from python service: "+e.getMessage());
    }
}
}
