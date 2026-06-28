package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.JwtService;
import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final JwtService jwtService;

    private static final String frontend_url="http://localhost:4200";

    public OAuthSuccessHandler(UserRepository userRepository, BusinessRepository businessRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        if (email == null) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("""
                        <html><body><script>
                            window.opener
                                ? (window.opener.postMessage({error:'no_email'},'http://localhost:4200'), window.close())
                                : window.location.href='http://localhost:4200/auth/login?error=no_email';
                        </script></body></html>
                    """);
            return;
        }
        String intendedRole = (String) request.getSession().getAttribute("oauth_intended_role");
        if (intendedRole == null) intendedRole = "USER";
        request.getSession().removeAttribute("oauth_intended_role");
        String role;

        if (userRepository.findByEmail(email).isPresent()) {
            role = "ROLE_" + "USER";
        } else if (businessRepository.findByEmail(email).isPresent()) {
            role = "ROLE_" + "BUSINESS";
        } else {
            if ("BUSINESS".equals(intendedRole)) {
                Business newBusiness = new Business();
                newBusiness.setEmail(email);
                newBusiness.setOwnerName(name);
                newBusiness.setBusinessName(email.split("@")[0]);
                newBusiness.setPassword("");
                newBusiness.setRole("BUSINESS");
                businessRepository.save(newBusiness);
                role = "ROLE_BUSINESS";
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setUsername(email.split("@")[0]);
                newUser.setPassword("");
                newUser.setRole("USER");
                userRepository.save(newUser);
                role = "ROLE_USER";
            }
        }
            String token = jwtService.generateToken(email, role);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("""
                        <html>
                        <body>
                        <script>
                            console.log('OAuth success handler running, opener:', window.opener);
                            if (window.opener) {
                                window.opener.postMessage(
                                    { token: '%s', role: '%s' },
                                    'http://localhost:4200'
                                );
                                setTimeout(function() { window.close(); }, 500);
                            } else {
                                window.location.href = 'http://localhost:4200/auth/oauth-callback?token=%s&role=%s';
                            }
                        </script>
                        </body>
                        </html>
                    """.formatted(token, role, token, role));
    }
}

