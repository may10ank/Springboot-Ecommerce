package com.example.EcommerceWeb.DTO;

public class HomeStatsDto {
    private long totalProducts;
    private long totalCustomers;
    private long totalBrands;

    public HomeStatsDto(
            long totalProducts,
            long totalCustomers,
            long totalBrands
    ) {
        this.totalProducts = totalProducts;
        this.totalCustomers = totalCustomers;
        this.totalBrands = totalBrands;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getTotalBrands() {
        return totalBrands;
    }

}
