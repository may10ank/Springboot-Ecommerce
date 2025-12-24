package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.JwtService;
import com.example.EcommerceWeb.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authenticationToken= (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User=authenticationToken.getPrincipal();
        String email=oAuth2User.getAttribute("email");
        String name=oAuth2User.getAttribute("name");
        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Email not provided by OAuth provider");
            return;
        }
        String role;
        if (userRepository.findByEmail(email).isPresent()) {
            role ="ROLE_" + "USER";
        } else if (businessRepository.findByEmail(email).isPresent()) {
            role = "ROLE_" +"BUSINESS";
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
        response.getWriter().write("""
        {
          "message": "OAuth login successful",
          "token": "%s"
        }
        """.formatted(token));
    }

    }

