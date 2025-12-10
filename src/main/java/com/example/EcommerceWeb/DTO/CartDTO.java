package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Cart;

import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private int id;
    private int userId;
    private int total;
    private List<CartItemDTO> items;

    public static CartDTO cartToCartDTO(Cart cart){
        CartDTO dto=new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        List<CartItemDTO> itemDTOS=cart.getItems().stream()
                .map(CartItemDTO::itemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOS);
        dto.setTotal(itemDTOS.stream().mapToInt(CartItemDTO::getTotalPrice).sum());
        return dto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}
