package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
}
