package com.example.EcommerceWeb.controller;


import com.example.EcommerceWeb.Service.LoginService;
import com.example.EcommerceWeb.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.doNothing;

public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoginService loginService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUserTestSuccess() throws Exception {
        User user=new User();
        user.setId(1);
        user.setUsername("a");
        user.setName("xyz");
        user.setEmail("abc@xyz.com");

        doNothing().when(loginService).registerUser(any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void registerUserTestFailure() throws Exception {
        User user=new User();

        doNothing().when(loginService).registerUser(any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

}
