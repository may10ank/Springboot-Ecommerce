package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.OrderItem;

public class OrderItemDTO {
    private String productName;
    private int quantity;
    private int price;
    private int totalAmount;
    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public static OrderItemDTO OrderItemToDto(OrderItem item){
        OrderItemDTO orderItemDTO=new OrderItemDTO();
        orderItemDTO.setProductName(item.getProduct().getProductName());
        orderItemDTO.setQuantity(item.getQuantity());
        orderItemDTO.setPrice(item.getPrice());
        orderItemDTO.setTotalAmount(item.getTotalPrice());
        return orderItemDTO;
    }
}
