package com.example.EcommerceWeb.Configuration;

import com.example.EcommerceWeb.Controller.OAuthSuccessHandler;
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
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/home","/auth/register/user","/auth/register/business","/auth/login","/oauth2/success","/swagger-ui/**","/v3/api-docs/**","/ecommerce/actuator/**","/api/payments/success","/api/payments/cancel").permitAll()
                        .requestMatchers("/business/profile","/business/profile/update","/business/delete","/api/orders/{orderId}/status","/api/orders/status/{status}","/api/product/addProduct","/api/product/updateProduct/{id}","/api/product/deleteProduct/{id}","/api/product/getProductBusiness","/api/product/uProduct/{id}","/business/profile/change-password").hasRole("BUSINESS")
                        .requestMatchers("/api/cart/**","/user/profile","/user/profile/update","/user/profile/delete","/user/profile/change-password","/api/review/user").hasRole("USER")
                        .requestMatchers("/api/orders/place","/api/orders/user","/api/orders/history","/api/orders/direct","/api/payments/initiate","/api/payments/history/{id}","/api/product/getProduct","/api/product/getProduct/{id}","/api/product/search","/api/product/voiceSearch","/api/review/add","/api/review/update/{reviewId}","/api/review/delete/{reviewId}","/api/review/user/{userId}").hasRole("USER")
                        .requestMatchers("/api/orders/allOrders","/api/orders/{orderId}","/api/review/add","/api/review/product/{productId}/average-rating","/api/review/product/{productId}/rating-summary","/api/review/product/{productId}/reviews","/api/review/product/{productId}/rating-summary/distribution","/api/review/{productId}/summary","/auth/logout","/api/review/product/{productId}").authenticated())
                .formLogin(Customizer.withDefaults())
                .oauth2Login(OAuth2Login ->OAuth2Login.successHandler(oAuthSuccessHandler))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
}
