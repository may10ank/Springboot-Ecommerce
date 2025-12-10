package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.Review;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.text.CharacterIterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewSummaryService {
    private final ChatClient chatClient;
    @Autowired
     ReviewService reviewService;
    @Autowired
     ProductRepository productRepository;

    public ReviewSummaryService(ChatClient.Builder chatClient) {

        this.chatClient =chatClient.build();
    }

    public String summarizeReviews(List<String> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "No reviews available";
        }

        String allReviews = String.join("\n", reviews);

        String promptText = """
                Summarize the following product reviews in a few words (max 15 words).
                Reviews:
                {reviews}
                """;

        PromptTemplate template = new PromptTemplate(promptText);
        Prompt prompt = template.create(Map.of("reviews", allReviews));

        ChatResponse response= (ChatResponse) chatClient.prompt(prompt).call();
        return response.getResult().getOutput().getText();
    }

    public String reviewSummary(int productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<String> reviewTexts = reviewService.getReviewsByProduct(productId)
                .stream()
                .map(ReviewDTO::getComment) // assuming your Review entity has getComment()
                .collect(Collectors.toList());
        return summarizeReviews(reviewTexts);
    }

}
