package com.example.EcommerceWeb.service;


import com.example.EcommerceWeb.DTO.ProductDTO;
import com.example.EcommerceWeb.DTO.RatingSummaryDTO;
import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.ProductImageRepository;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.Service.ProductService;
import com.example.EcommerceWeb.Service.ReviewService;
import com.example.EcommerceWeb.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.Validator;

import javax.annotation.meta.When;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductServiceTest {
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductRepository productRepository;
    @InjectMocks
    private ProductImageRepository productImageRepository;
    @InjectMocks
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ReviewService reviewService;
    @InjectMocks
    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProductSuccess(){
        Product product=new Product();
        MockMultipartFile img = new MockMultipartFile("file", "image.jpg", "image/jpeg", "abc".getBytes());
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product saved=productService.addProduct(product, List.of(img),null);
        assertThat(saved).isNotNull();
        verify(productRepository,times(1)).save(product);
 }

    @Test
    void updateProductSuccess(){
        int id = 1;
        Product existing = new Product();
        existing.setProductId(id);
        existing.setStock(10);

        Product input = new Product();
        input.setProductName("abc");
        input.setStock(5);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        MockMultipartFile img = new MockMultipartFile(
                "file", "a.jpg", "image/jpeg", "123".getBytes()
        );

        Product updated = productService.updateProduct(id, input, List.of(img), null);

        assertThat(updated.getProductName()).isEqualTo("abc");
        assertThat(updated.getStock()).isEqualTo(15);
        verify(productRepository).save(existing);
    }

    @Test
    void deleteProductSuccess() {
        int id = 1;
        when(productRepository.existsById(id)).thenReturn(true);

        productService.deleteProduct(id);

        verify(productRepository).deleteById(id);
    }

    @Test
    void getProductByIdSuccess() {
        Product p = new Product();
        p.setProductId(1);
        p.setProductName("xyz");
        p.setImages(List.of());
        when(productRepository.findById(1)).thenReturn(Optional.of(p));

        RatingSummaryDTO ratingSummary = new RatingSummaryDTO();
        when(reviewService.getRatingSummary(1)).thenReturn(ratingSummary);

        Page<ReviewDTO> mockPage = new PageImpl<>(List.of());
        when(reviewService.getReviewsForProduct(eq(1), anyString(), anyInt(), anyInt()))
                .thenReturn(mockPage);

        when(reviewService.getProductRatingWithDistribution(1))
                .thenReturn(Map.of());

        ProductDTO dto = productService.getProductById(1);

        assertThat(dto.getProductName()).isEqualTo("xyz");
    }

}
