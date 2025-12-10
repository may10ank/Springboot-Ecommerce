package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.PasswordChangeDto;
import com.example.EcommerceWeb.DTO.UserDTO;
import com.example.EcommerceWeb.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return new ResponseEntity<>(userService.getUserProfile(email), HttpStatus.OK);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserDTO> updateProfile(Authentication authentication,
                                                        @RequestBody UserDTO updatedUser) {
        String email = authentication.getName();
        return new ResponseEntity<>(userService.updateProfile(email, updatedUser), HttpStatus.OK);
    }

    @DeleteMapping("/profile/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String email = authentication.getName();
        userService.deleteAccount(email);
        return new ResponseEntity<>("Your account has been deleted successfully.",HttpStatus.OK);
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication,
                                                 @RequestBody PasswordChangeDto passwordChangeDTO) {
        String email = authentication.getName();
        userService.changePassword(email, passwordChangeDTO);
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    }
}
