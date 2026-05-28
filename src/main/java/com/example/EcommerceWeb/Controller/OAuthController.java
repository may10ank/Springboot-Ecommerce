package com.example.EcommerceWeb.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OAuthController {
    @GetMapping("/auth/oauth2/start")
    public void startOAuth(@RequestParam(defaultValue = "USER") String role, HttpServletRequest request,HttpServletResponse response) throws IOException {
        request.getSession().setAttribute("oauth_intended_role", role);
        response.sendRedirect("/oauth2/authorization/google");
    }
}
