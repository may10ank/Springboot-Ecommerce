package com.example.EcommerceWeb.controller;

import com.example.EcommerceWeb.DTO.RatingSummaryDTO;
import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.CartService;
import com.example.EcommerceWeb.Service.ReviewService;
import com.example.EcommerceWeb.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ProductRepository productRepository;
    @MockBean
    private ReviewService reviewService;
    @Autowired
    private ObjectMapper objectMapper;
    private User savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setId(1);
        user.setName("abc");
        user.setEmail("xyz@abc.com");
        user.setPassword("pass123");
        user.setRole("USER");
        userRepository.save(savedUser);
    }

    @Test
    @WithMockUser(username = "xyz@abc.com", roles = {"USER"})
    void getReviewByProductSuccess() throws Exception {
        ReviewDTO reviewDTO=new ReviewDTO();
        reviewDTO.setRating(4);
        reviewDTO.setUsername("abc");
        reviewDTO.setComment("nice product");
        when(reviewService.getReviewsByProduct(2)).thenReturn(List.of(reviewDTO));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/product/2"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].rating").value(4));
    }

    @Test
    @WithMockUser(username = "xyz@abc.com", roles = {"USER"})
    void getAverageRatingSuccess() throws Exception {
        when(reviewService.getAverageRating(5))
                .thenReturn(4.5);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/product/5/average-rating"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("4.5"));
    }

    @Test
    @WithMockUser(username = "xyz@abc.com", roles = {"USER"})
    void getRatingSummarySuccess() throws Exception {
        RatingSummaryDTO ratingSummaryDTO=new RatingSummaryDTO();
        ratingSummaryDTO.setAverageRating(3.45);
        when(reviewService.getRatingSummary(5))
                .thenReturn(ratingSummaryDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/product/5/average-rating"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.AverageRating").value(3.45));
    }





}
