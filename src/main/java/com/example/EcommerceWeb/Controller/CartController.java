package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.AddItemRequest;
import com.example.EcommerceWeb.DTO.CartDTO;
import com.example.EcommerceWeb.DTO.RemoveItemRequest;
import com.example.EcommerceWeb.DTO.UpdateCartRequest;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.CartService;
import com.example.EcommerceWeb.customException.UserNotFoundException;
import com.example.EcommerceWeb.model.Cart;
import com.example.EcommerceWeb.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/getCard")
    public ResponseEntity<CartDTO> getCart(Authentication authentication){
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(cartService.getCartDto(user.getId()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItem(
            @Valid @RequestBody AddItemRequest request,Authentication authentication) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(cartService.addItem(user.getId(), request.getProductid(), request.getQuantity()),HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateItem(
            Authentication authentication,
            @Valid @RequestBody UpdateCartRequest request) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(cartService.updateItem(user.getId(), request.getCartItemId(), request.getQuantity()),HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartDTO> removeItem(Authentication authentication,
            @Valid @RequestBody RemoveItemRequest request) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(cartService.removeItem(user.getId(),request.getCartItemId()),HttpStatus.OK);
    }

    @DeleteMapping("/clear")
        public ResponseEntity<Cart> clearCart(Authentication authentication){
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        Cart clearedcart=cartService.clearCart(user.getId());
            return new ResponseEntity<>(clearedcart,HttpStatus.OK);
        }
    }

