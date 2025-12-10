package com.example.EcommerceWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class EcommerceWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(EcommerceWebApplication.class, args);
	}

}
