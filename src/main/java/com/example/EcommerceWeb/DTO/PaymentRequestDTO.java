package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    private int orderId;
    @NotNull(message = "payment method is required")
    private String paymentMethod;
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
