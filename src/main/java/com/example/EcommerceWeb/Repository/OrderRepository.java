package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Order;
import com.example.EcommerceWeb.model.OrderStatus;
import com.example.EcommerceWeb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> findByUserAndStatus(User user,OrderStatus orderStatus);
}
