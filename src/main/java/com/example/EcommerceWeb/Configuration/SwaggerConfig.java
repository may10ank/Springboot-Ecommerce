package com.example.EcommerceWeb.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI ecommerceOpenAPI(){
       return new OpenAPI().info(new Info().
                          title("Ecommerce API")
                          .description("API documentation for ecommerce project")
                          .version("1.0")
                          .contact(new Contact().
                                           name("abc").
                                           email("abc@gmail.com")));
    }
}
