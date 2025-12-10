package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.CartDTO;
import com.example.EcommerceWeb.Repository.CartItemRepository;
import com.example.EcommerceWeb.Repository.CartRepository;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.customException.ProductNotFoundException;
import com.example.EcommerceWeb.model.Cart;
import com.example.EcommerceWeb.model.CartItem;
import com.example.EcommerceWeb.model.Product;
import com.example.EcommerceWeb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    private final String CACHE_NAME="business";
    public Cart isCartPresent(int userId) {
        return cartRepository.findByUser_Id(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found: " + userId));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    @Cacheable(value = CACHE_NAME, key = "#userId")
    public CartDTO getCartDto(int userId){
        Cart cart=isCartPresent(userId);
        return CartDTO.cartToCartDTO(cart);
    }

    @CachePut(value = CACHE_NAME, key = "#userId")
    public CartDTO addItem(int userId,int productId,int quantity){
         Cart cart=isCartPresent(userId);
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException("Product not Found:" +productId));

        Optional<CartItem> existing=cart.getItems().stream()
                .filter(ci->ci.getProduct().getProductId()==productId)
                .findFirst();

        if(existing.isPresent()){
            CartItem item=existing.get();
            item.setQuantity(item.getQuantity()+quantity);
            cartItemRepository.save(item);
        }else{
            CartItem cartItem=new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }
        Cart saved=cartRepository.save(cart);
        return CartDTO.cartToCartDTO(saved);
    }

    @CachePut(value = CACHE_NAME, key = "#userId")
    public CartDTO updateItem(int userId, int cartItemId, int quantity) {
        Cart cart = isCartPresent(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        item.setQuantity(item.getQuantity()+quantity);
        cartItemRepository.save(item);
        Cart saved=cartRepository.save(cart);
        return CartDTO.cartToCartDTO(saved);
    }

    @CacheEvict(value = CACHE_NAME, key = "#userId")
    public CartDTO removeItem(int userId, int cartItemId) {
        Cart cart = isCartPresent(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        Cart saved=cartRepository.save(cart);
        return CartDTO.cartToCartDTO(saved);
    }
    @CacheEvict(value = CACHE_NAME, key = "#userId")
    public Cart clearCart(int userId){
        Cart cart=cartRepository.findByUser_Id(userId).orElseThrow(()->new RuntimeException("cart not found for user id" + userId));
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
