package com.example.EcommerceWeb.DTO;

import com.example.EcommerceWeb.model.Payment;

import java.time.LocalDateTime;

public class PaymentDTO {
    private int id;
    private String transactionId;
    private String status;
    private String method;
    private int amount;
    private LocalDateTime paymentDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public static PaymentDTO paymentToDto(Payment payment){
        if(payment==null){return null;}
        PaymentDTO paymentDTO=new PaymentDTO();
        paymentDTO.setId(payment.getId());
        paymentDTO.setTransactionId(payment.getTransactionId());
        paymentDTO.setStatus(payment.getStatus().name());
        paymentDTO.setMethod(payment.getPaymentMethod().name());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        return paymentDTO;
    }
}
