package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.model.Product;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewSummaryService {
    private final ChatClient chatClient;
     private final ReviewService reviewService;
     private final ProductRepository productRepository;

    public ReviewSummaryService(ChatClient.Builder chatClient, ReviewService reviewService, ProductRepository productRepository) {
        this.chatClient =chatClient.build();
        this.reviewService = reviewService;
        this.productRepository = productRepository;
    }

    public String summarizeReviews(List<String> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "No reviews available for this Product";
        }

        String allReviews = String.join("\n", reviews);

        String promptText = """
                 You are a helpful assistant. Summarize the following customer product reviews
                 into 2-3 sentences highlighting overall sentiment, common praise, and common complaints.
                \s
                 Reviews:
                 {reviews}
                     """;

        PromptTemplate template = new PromptTemplate(promptText);
        Prompt prompt = template.create(Map.of("reviews", allReviews));

        return chatClient.prompt(prompt).call().content();
    }

    public String reviewSummary(int productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<String> reviewTexts = reviewService.getReviewsByProduct(product.getProductId())
                .stream()
                .map(ReviewDTO::getComment)
                .filter(comment->comment!=null && !comment.isBlank())
                .collect(Collectors.toList());
        return summarizeReviews(reviewTexts);
    }

}
