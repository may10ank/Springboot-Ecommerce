package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.CartItem;
import com.example.EcommerceWeb.model.Product;

public class CartItemDTO {
    private int id;
    private int quantity;
    private int totalPrice;
    private ProductSummaryDTO productSummaryDTO;

    public static CartItemDTO itemDTO(CartItem item){
        Product product=item.getProduct();
        ProductSummaryDTO ps=new ProductSummaryDTO();
        ps.setId(product.getProductId());
        ps.setName(product.getProductName());
        ps.setBrand(product.getBrand());
        ps.setDiscountedPrice(product.getDiscountedPrice());

        CartItemDTO dto=new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setProductSummaryDTO(ps);
        dto.setTotalPrice(ps.getDiscountedPrice()*item.getQuantity());
        return dto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ProductSummaryDTO getProductSummaryDTO() {
        return productSummaryDTO;
    }

    public void setProductSummaryDTO(ProductSummaryDTO productSummaryDTO) {
        this.productSummaryDTO = productSummaryDTO;
    }
}
