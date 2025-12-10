package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.BusinessDTO;
import com.example.EcommerceWeb.DTO.PasswordChangeDto;
import com.example.EcommerceWeb.Service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business")
public class BusinessController {
    @Autowired
    BusinessService businessService;

     @GetMapping("/profile")
    public ResponseEntity<BusinessDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return new ResponseEntity<>(businessService.getProfile(email), HttpStatus.OK);
    }
    @PutMapping("/profile/update")
    public ResponseEntity<BusinessDTO> updateProfile(Authentication authentication,@RequestBody BusinessDTO updateDTO) {
        String email = authentication.getName();
        return new ResponseEntity<>(businessService.updateProfile(email, updateDTO),HttpStatus.OK);
    }

    @DeleteMapping("/profile/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String email = authentication.getName();
        businessService.deleteAccount(email);
        return new ResponseEntity<>("Business account has been deleted successfully.",HttpStatus.OK);
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication,
                                                 @RequestBody PasswordChangeDto passwordChangeDTO) {
        String email = authentication.getName();
        businessService.changePassword(email, passwordChangeDTO);
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    }

}
