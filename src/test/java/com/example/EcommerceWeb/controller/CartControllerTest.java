package com.example.EcommerceWeb.controller;

import com.example.EcommerceWeb.DTO.AddItemRequest;
import com.example.EcommerceWeb.DTO.CartDTO;
import com.example.EcommerceWeb.DTO.RemoveItemRequest;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.CartService;
import com.example.EcommerceWeb.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private CartService cartService;
    @Autowired
    private ObjectMapper objectMapper;
    private User savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setId(1);
        user.setName("abc");
        user.setEmail("xyz@abc.com");
        user.setPassword("pass123");
        user.setRole("USER");

        savedUser = userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "xyz@abc.com", roles = {"USER"})
    void addItemSuccess() throws Exception {

        AddItemRequest request = new AddItemRequest();
        request.setProductid(1);
        request.setQuantity(2);

        CartDTO cartDTO = new CartDTO();

        when(cartService.addItem(savedUser.getId(), 1, 2))
                .thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(cartService).addItem(savedUser.getId(), 1, 2);
    }

    @Test
    @WithMockUser(username = "xyz@abc.com", roles = {"USER"})
    void testRemoveItemSuccess() throws Exception {

        RemoveItemRequest request = new RemoveItemRequest();
        request.setCartItemId(1);

        CartDTO cartDTO = new CartDTO();

        when(cartService.removeItem(savedUser.getId(), 1))
                .thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cartService).removeItem(savedUser.getId(),1);
    }


}
