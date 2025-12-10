package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>, JpaSpecificationExecutor<Product> {
    Page<Product> findByBusinessBusinessId(int businessId, Pageable pageable);

}
