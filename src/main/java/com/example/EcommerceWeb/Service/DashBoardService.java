package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.HomeStatsDto;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashBoardService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;
    public HomeStatsDto getHomeStats() {

        return new HomeStatsDto(
                productRepository.count(),
                userRepository.count(),
                productRepository.countDistinctBrands()
        );
    }


}
