package com.example.EcommerceWeb.model;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> nameOrDescriptionContians(String keyword){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase()+ "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + keyword.toLowerCase()+ "%")
                );
    }

    public static Specification<Product> categoryContains(String category){
        return (root, query,cb)->
                cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%");
    }

    public static Specification<Product> brandContains(String brand){
        return (root, query,cb)->
                cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
    }

    public static Specification<Product> priceLessThanEqual(int price){
        return (root, query,cb)->
                cb.lessThanOrEqualTo(root.get("discountedPrice"),price);
    }

    public static Specification<Product> priceGreaterThanEqual(int price){
        return (root, query,cb)->
                cb.greaterThanOrEqualTo(root.get("discountedPrice"),price);
    }

    public static Specification<Product> priceBetween(int min,int max){
        return (root, query,cb)->
                cb.between(root.get("discountedPrice"),min,max);
    }
}
