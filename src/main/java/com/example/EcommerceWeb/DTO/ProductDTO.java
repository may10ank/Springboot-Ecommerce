package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.ProductImage;
import com.example.EcommerceWeb.model.Review;
import org.springframework.data.domain.Page;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Map;

public class ProductDTO {
    private String productName;
    private String productDescription;
    private int actualPrice;
    private int discountedPrice;
    private int discountPercent;
    private String Brand;
    private RatingSummaryDTO ratingSummaryDTO;
    private Page<ReviewDTO> reviewList;
    private Map<String,Object> ratingDistributionSummary;

    private List<String> productImages;
    private String videos;

    private int totalSalesCount;
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public Page<ReviewDTO> getReviewList() {
        return reviewList;
    }

    public void setReviewList(Page<ReviewDTO> reviewList) {
        this.reviewList = reviewList;
    }

    public Map<String, Object> getRatingDistributionSummary() {
        return ratingDistributionSummary;
    }

    public void setRatingDistributionSummary(Map<String, Object> ratingDistributionSummary) {
        this.ratingDistributionSummary = ratingDistributionSummary;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
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
}
