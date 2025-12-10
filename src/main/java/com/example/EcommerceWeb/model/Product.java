package com.example.EcommerceWeb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;
    @NotBlank(message = "Product name is required")
    @Size(min = 2,max = 100)
    private String productName;
    @NotBlank(message = "Description is required")
    @Size(min = 10,max = 2000)
    @Column(length = 2000)
    private String productDescription;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Brand is required")
    private String brand;
    @NotNull
    @Min(value = 0,message = "Stock cannot be negative")
    private int stock;
    @NotNull
    @Min(value=1,message = "price must be positive")
    private int actualPrice;
    @NotNull
    @Min(value=1,message = "Discounted price must be positive")
    private int discountedPrice;
    @Min(value=0,message = "Discount Percent cannot be negative")
    @Max(value=100,message = "Discount percent cannot be more than 100")
    @NotNull
    private int discountPercent;
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnoreProperties("product")
    @JsonManagedReference
    private List<ProductImage> images;
    @Lob
    @JsonIgnore
    private byte[] video;
    @ManyToOne
    @JoinColumn(name = "business_id",nullable = false)
    private Business business;
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews=new ArrayList<>();
    private int totalSalesCount;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

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

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
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

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public int getTotalSalesCount() {
        return totalSalesCount;
    }

    public void setTotalSalesCount(int totalSalesCount) {
        this.totalSalesCount = totalSalesCount;
    }

    public Product() {
    }

    public Product(int id, String name, String description, String category, String brand, int stock, int actualPrice, int discountedPrice, int discountPercent, List<ProductImage> images, byte[] video) {
        this.productId = id;
        this.productName = name;
        this.productDescription= description;
        this.category = category;
        this.brand = brand;
        this.stock = stock;
        this.actualPrice = actualPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
        this.images = images;
        this.video = video;
    }

    public void reduceStock(int quantity,Product product){
        if(this.stock<quantity){
            throw new IllegalArgumentException("Stock for the product+"+product.getProductName()+"is zero");
        }
        this.stock-=quantity;
        totalSalesCount+=1;
    }
}
