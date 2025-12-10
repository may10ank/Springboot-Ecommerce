package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business,Integer> {
    Optional<Business> findByEmail(String email);

  

}
