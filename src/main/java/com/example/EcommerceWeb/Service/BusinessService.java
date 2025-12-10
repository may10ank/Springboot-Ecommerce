package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.BusinessDTO;
import com.example.EcommerceWeb.DTO.PasswordChangeDto;
import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.model.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {
    @Autowired
    BusinessRepository businessRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String CACHE_NAME="business";

    @Cacheable(value = CACHE_NAME,key = "#email")
    public BusinessDTO getProfile(String email) {
        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return new BusinessDTO(business);
    }

    @CachePut(value = CACHE_NAME,key = "#email")
    public BusinessDTO updateProfile(String email, BusinessDTO updateDTO) {
        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setBusinessName(updateDTO.getBusinessName());
        business.setOwnerName(updateDTO.getOwnerName());
        business.setPhoneNumber(updateDTO.getPhoneNumber());
        business.setAddress(updateDTO.getAddress());

        Business saved = businessRepository.save(business);
        return new BusinessDTO(saved);
    }

    @CacheEvict(value = CACHE_NAME,key = "#email")
    public void deleteAccount(String email) {
        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        businessRepository.delete(business);

    }

    @CacheEvict(value = CACHE_NAME,key = "#email")
    public void changePassword(String email, PasswordChangeDto passwordChangeDto){
        Business business=businessRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        if(!passwordEncoder.matches(passwordChangeDto.getOldPassword(),business.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }
        business.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        businessRepository.save(business);
    }

}
