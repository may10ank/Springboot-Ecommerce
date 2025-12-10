package com.example.EcommerceWeb.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddItemRequest {
    @NotNull(message = "Product ID is required")
    private Integer productid;

    @Min(value = 1,message = "Quantity must be at least 1")
    private int quantity;

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
