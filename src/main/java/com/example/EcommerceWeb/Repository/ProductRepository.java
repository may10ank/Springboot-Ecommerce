package com.example.EcommerceWeb.Repository;

import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>, JpaSpecificationExecutor<Product> {
    Page<Product> findByBusinessBusinessId(int businessId, Pageable pageable);

    @Query("""
            select p from Product p ORDER BY CASE when p.createdAt is NOT NULL AND p.createdAt>=:oneWeekAgo then 1 else 0 end desc,
            (p.totalSalesCount*0.7+p.productId*0.3) DESC
            """)
    Page<Product> findAllSortedProducts(@Param("oneWeekAgo")LocalDateTime oneWeekAgo,Pageable pageable);

    @Query("""
        SELECT COUNT(DISTINCT p.brand)
        FROM Product p
    """)
    long countDistinctBrands();


}
