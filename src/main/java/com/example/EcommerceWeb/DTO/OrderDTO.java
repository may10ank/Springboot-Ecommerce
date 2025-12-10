package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Order;
import com.example.EcommerceWeb.model.OrderItem;
import com.example.EcommerceWeb.model.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderDTO {
    private int id;
    @JsonFormat(pattern = "dd-MMM-yyyy hh:mm a")
    private LocalDateTime orderDate;
    private int totalAmount;
    private String status;
    private String deliveryAddress;
    private PaymentDTO paymentDTO;
    private List<OrderItemDTO> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public PaymentDTO getPaymentDTO() {
        return paymentDTO;
    }

    public void setPaymentDTO(PaymentDTO paymentDTO) {
        this.paymentDTO = paymentDTO;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public static OrderDTO orderToOrderDto(Order order){
        OrderDTO dto=new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setItems(order.getItems().stream().map(OrderItemDTO::OrderItemToDto).toList());
        dto.setPaymentDTO(PaymentDTO.paymentToDto(order.getPayment()!=null? order.getPayment():null));
        return dto;
    }
}
