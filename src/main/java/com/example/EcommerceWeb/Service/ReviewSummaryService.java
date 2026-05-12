package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.DTO.ReviewSummaryRequestDto;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.model.Product;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewSummaryService {
     private final ReviewService reviewService;
     private final ProductRepository productRepository;
     private final RestTemplate restTemplate;
     private static final String PYTHON_SUMMARY_URL="http://localhost:8000/summarize";

    public ReviewSummaryService(ReviewService reviewService, ProductRepository productRepository,RestTemplate restTemplate) {
        this.restTemplate =restTemplate;
        this.reviewService = reviewService;
        this.productRepository = productRepository;
    }

    public String reviewSummary(int productId){
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));

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
