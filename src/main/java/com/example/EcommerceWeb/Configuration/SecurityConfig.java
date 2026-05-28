package com.example.EcommerceWeb.Configuration;

import com.example.EcommerceWeb.Controller.OAuthSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    OAuthSuccessHandler oAuthSuccessHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.csrf(csrf->csrf.disable())
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/home","/auth/register/user","/auth/register/business","/auth/login","/oauth2/**","/login/oauth2/**","/swagger-ui/**","/v3/api-docs/**","/ecommerce/actuator/**","/api/payments/success","/api/payments/cancel","/api/product/getProduct","/api/product/getProduct/{id}","/api/product/search","/api/product/voiceSearch","/api/product/qa/{productId}","/api/product/{productId}/similar","/api/home/stats").permitAll()
                        .requestMatchers("/business/profile","/business/profile/update","/business/delete","/api/orders/{orderId}/status","/api/orders/status/{status}","/api/product/addProduct","/api/product/updateProduct/{id}","/api/product/deleteProduct/{id}","/api/product/getProductBusiness","/api/product/uProduct/{id}","/business/profile/change-password").hasRole("BUSINESS")
                        .requestMatchers("/api/cart/**","/user/profile","/user/profile/update","/user/profile/delete","/user/profile/change-password","/api/review/user").hasRole("USER")
                        .requestMatchers("/api/orders/place","/api/orders/user","/api/orders/history","/api/orders/direct","/api/payments/initiate","/api/payments/history/{id}","/api/review/add","/api/review/update/{reviewId}","/api/review/delete/{reviewId}","/api/review/user/{userId}","/api/orders/cancel/{orderId}").hasRole("USER")
                        .requestMatchers("/api/orders/allOrders","/api/orders/{orderId}","/api/review/add","/api/review/product/{productId}/average-rating","/api/review/product/{productId}/rating-summary","/api/review/product/{productId}/reviews","/api/review/product/{productId}/rating-summary/distribution","/api/review/{productId}/summary","/auth/logout","/api/review/product/{productId}").authenticated())
//                .formLogin(Customizer.withDefaults())
                .oauth2Login(OAuth2Login ->OAuth2Login.successHandler(oAuthSuccessHandler).authorizationEndpoint(a->a.baseUri("/oauth2/authorization")).redirectionEndpoint(r->r.baseUri("/login/oauth2/code/*")))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200","http://localhost:8000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
