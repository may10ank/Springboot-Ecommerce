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
        String ImageBase64 = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            byte[] imageData = product.getImages().get(0).getData();
            if (imageData != null) {
                ImageBase64 = Base64.getEncoder().encodeToString(imageData);
            }
        }
        dto.setProductName(product.getProductName());
        dto.setBrand(product.getBrand());
        dto.setActualPrice(product.getActualPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
//        dto.setRatingSummaryDTO(reviewService.getRatingSummary(product.getProductId());
        dto.setRatingSummaryDTO(ratingSummaryDTO);
        dto.setTotalSalesCount(product.getTotalSalesCount());
        dto.setProductImage(ImageBase64);
        return dto;
    }
}
