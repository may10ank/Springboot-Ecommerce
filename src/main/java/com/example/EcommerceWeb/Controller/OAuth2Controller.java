package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.JwtService;
import com.example.EcommerceWeb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
   JwtService jwtService;
        @GetMapping("/success")
        public ResponseEntity<String> oauthSuccess(OAuth2AuthenticationToken authentication) {
            String email = authentication.getPrincipal().getAttribute("email");
            String name = authentication.getPrincipal().getAttribute("name");

            String role;
            if (userRepository.findByEmail(email).isPresent()) {
                role = "USER";
            } else if (businessRepository.findByEmail(email).isPresent()) {
                role = "BUSINESS";
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setUsername(email.split("@")[0]);
                newUser.setPassword("");
                newUser.setRole("USER");
                userRepository.save(newUser);
                role = "USER";
            }
            String token = jwtService.generateToken(email, role);
            return new ResponseEntity<>("Login successful! JWT Token: " + token, HttpStatus.OK);
        }
}
