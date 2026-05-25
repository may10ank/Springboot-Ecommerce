package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Order;
import com.example.EcommerceWeb.model.OrderStatus;
import com.example.EcommerceWeb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> findByUserAndStatusIn(User user,List<OrderStatus> statuses);
    List<Order> findByUserAndStatusNotIn(User user,List<OrderStatus> statuses);
    @Query("""
SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.product p 
WHERE p.business.businessId = :businessId ORDER BY o.orderDate DESC """)
    List<Order> findOrdersByBusinessId(@Param("businessId") int businessId);
}
