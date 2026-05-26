package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.Service.ReviewService;
import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ProductListDTO {
    private int id;
    private String productName;
    private String Brand;
    private int actualPrice;
    private int discountedPrice;
    private int discountPercent;
    private RatingSummaryDTO ratingSummaryDTO;
    private String productImage;
    private int totalSalesCount;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public RatingSummaryDTO getRatingSummaryDTO() {
        return ratingSummaryDTO;
    }

    public void setRatingSummaryDTO(RatingSummaryDTO ratingSummaryDTO) {
        this.ratingSummaryDTO = ratingSummaryDTO;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getTotalSalesCount() {
        return totalSalesCount;
    }

    public void setTotalSalesCount(int totalSalesCount) {
        this.totalSalesCount = totalSalesCount;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ProductListDTO productToListDto(Product product, RatingSummaryDTO ratingSummaryDTO){
        ProductListDTO dto=new ProductListDTO();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setProductImage(product.getImages().get(0).getImageUrl());
        }
        dto.setId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setBrand(product.getBrand());
        dto.setActualPrice(product.getActualPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setRatingSummaryDTO(ratingSummaryDTO);
        dto.setTotalSalesCount(product.getTotalSalesCount());
        dto.setCategory(product.getCategory());
        return dto;
    }
}
