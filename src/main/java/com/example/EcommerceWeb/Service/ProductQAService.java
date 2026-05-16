package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ProductqarequestDTO;
import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductQAService {
    private final ProductRepository productRepository;
    private final ReviewService reviewService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String PYTHON_QA_URL="http://localhost:8000/product-qa";

    public ProductQAService(ProductRepository productRepository,
                            ReviewService reviewService,
                            RestTemplate restTemplate,
                            ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.reviewService = reviewService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String askQuestion(int productId, String question, List<Map<String, String>> chatHistory) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // 2. Fetch reviews for context
        List<ReviewDTO> reviewDTOs = reviewService.getReviewsByProduct(productId);

        // 3. Build request for Python
        ProductqarequestDTO request = new ProductqarequestDTO();
        request.setProductId(productId);
        request.setProductName(product.getProductName());
        request.setProductDescription(product.getProductDescription());
        request.setBrand(product.getBrand());
        request.setCategory(product.getCategory());
        request.setActualPrice(product.getActualPrice());
        request.setDiscountedPrice(product.getDiscountedPrice());
        request.setDiscountPercent(product.getDiscountPercent());
        request.setQuestion(question);

        // 4. Map reviews
        List<ProductqarequestDTO.ReviewItem> reviewItems = reviewDTOs.stream()
                .filter(r -> r.getComment() != null && !r.getComment().isBlank())
                .map(r -> new ProductqarequestDTO.ReviewItem(r.getRating(), r.getComment()))
                .collect(Collectors.toList());
        request.setReviews(reviewItems);

        // 5. Map chat history
        List<ProductqarequestDTO.ChatMessage> history = new ArrayList<>();
        if (chatHistory != null) {
            for (Map<String, String> msg : chatHistory) {
                history.add(new ProductqarequestDTO.ChatMessage(
                        msg.get("role"),
                        msg.get("content")
                ));
            }
        }
        request.setChatHistory(history);

        // 6. Call Python service
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductqarequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    PYTHON_QA_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            // Extract answer from response JSON
            Map<?, ?> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            return (String) responseMap.get("answer");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get answer from Q&A service: " + e.getMessage());
        }
    }
}
