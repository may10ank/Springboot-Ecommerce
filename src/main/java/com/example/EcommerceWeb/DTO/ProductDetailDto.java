package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.ProductImage;

import java.util.List;
import java.util.Base64;


public class ProductDetailDto {
    private int id;
    private String name;
    private String description;
    private String category;
    private String brand;
    private int stock;
    private int actualPrice;
    private int discountedPrice;
    private int discountPercent;
    private List<ProductImageDto> images;
    private String videoBase64;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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

    public List<ProductImageDto> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDto> images) {
        this.images = images;
    }

    public String getVideoBase64() {
        return videoBase64;
    }

    public void setVideoBase64(String videoBase64) {
        this.videoBase64 = videoBase64;
    }

    public static ProductDetailDto fromEntity(Product product) {
        ProductDetailDto dto = new ProductDetailDto();
        dto.setId(product.getProductId());
        dto.setName(product.getProductName());
        dto.setDescription(product.getProductDescription());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setActualPrice(product.getActualPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
        List<ProductImageDto> imagesDto = product.getImages().stream()
                .map(img -> {
                    ProductImageDto imgDto = new ProductImageDto();
                    imgDto.setId(img.getId());
                    if (img.getData() != null) {
                        imgDto.setImageBase64(Base64.getEncoder().encodeToString(img.getData()));
                    }
                    return imgDto;
                }).toList();
        dto.setImages(imagesDto);

        if (product.getVideo() != null) {
            dto.setVideoBase64(Base64.getEncoder().encodeToString(product.getVideo()));
        }
        return dto;
    }

}
