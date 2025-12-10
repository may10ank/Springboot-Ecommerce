package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.LoginRequest;
import com.example.EcommerceWeb.Service.JwtService;
import com.example.EcommerceWeb.Service.LoginService;
import com.example.EcommerceWeb.Service.TokenService;
import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.User;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/auth/")
public class LoginController {
@Autowired
    LoginService loginService;
@Autowired
JwtService jwtService;

@Autowired
    TokenService tokenService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@RequestBody@Valid User user){
        loginService.registerUser(user);
        return new ResponseEntity<>("User registered successfully,Login and start Shopping", HttpStatus.OK);
    }

    @PostMapping("/register/business")
    public ResponseEntity<String> registerBusiness(@RequestBody @Valid Business business){
        loginService.registerBusiness(business);
        return new ResponseEntity<>("Business registered successfully,Login and start selling", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        String token= loginService.login(loginRequest);
        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Extract expiration from token using JwtService
            Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);
            LocalDateTime expiry = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Save token in blacklist
            tokenService.blacklistToken(token, expiry);
        }

        return ResponseEntity.ok("Logged out successfully");
    }


}
