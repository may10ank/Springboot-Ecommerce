package com.example.EcommerceWeb.DTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;

public class RemoveItemRequest {
    @NotNull(message = "Cart item Id is required")
    private Integer cartItemId;

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }
}
