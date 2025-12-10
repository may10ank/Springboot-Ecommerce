package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
