package com.example.EcommerceWeb.Configuration;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoConfig implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> ecommerce = new HashMap<String, String>();
        ecommerce.put("Project Name", "Ecommerce API");
        ecommerce.put("Project Description", "Ecommerce API marketplace for buyers and seller");
        ecommerce.put("Project Version", "1.0.0");
        ecommerce.put("Contact Email", "abc@xyz.com");
        ecommerce.put("Contact Mobile", "+91 7464384343");
        builder.withDetail("ecommerce-info", ecommerce);
    }
}