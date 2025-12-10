package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.LoginRequest;
import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.User;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtService jwtService;
    @Autowired
    NotificationService notificationService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved=userRepository.save(user);

        String subject = "Welcome to E-commerce, " + saved.getName() + "!";
        String body = "Hi " + saved.getName() + ",\n\n" +
                "Your account has been successfully created.\n" +
                "Username: " + saved.getUsername() + "\n" +
                "Email: " + saved.getEmail() + "\n\n" +
                "You can now log in and start shopping.\n\n" +
                "Thank you for joining us!\n";
        try {
            notificationService.sendEmail(saved.getEmail(), subject, body);
            System.out.println("Welcome email sent to: " + saved.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public void registerBusiness(Business business) {
        business.setPassword(passwordEncoder.encode(business.getPassword()));
        Business saved=businessRepository.save(business);
        String subject = "Welcome to E-commerce " + saved.getBusinessName() + "!";
        String body = "Hi " + saved.getBusinessName() + ",\n\n" +
                "Your account has been successfully created.\n" +
                "Username: " + saved.getBusinessName() + "\n" +
                "Email: " + saved.getEmail() + "\n\n" +
                "You can now log in and start selling.\n\n" +
                "Thank you for joining us!\n";
        try {
            notificationService.sendEmail(saved.getEmail(), subject, body);
            System.out.println("Welcome email sent to: " + saved.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public String login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String role = userRepository.findByEmail(loginRequest.getEmail()).map(User::getRole)
                .orElse(businessRepository.findByEmail(loginRequest.getEmail()).map(Business::getRole).orElse("UNKNOWN"));
        String springRole = "ROLE_" + role;
        return jwtService.generateToken(loginRequest.getEmail(), springRole);
    }

}