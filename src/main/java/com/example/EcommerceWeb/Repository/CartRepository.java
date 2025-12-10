package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    Optional<Cart> findByUser_Id(int userId);
}
