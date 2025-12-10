package com.example.EcommerceWeb.service;

import com.example.EcommerceWeb.DTO.OrderDTO;
import com.example.EcommerceWeb.Repository.*;
import com.example.EcommerceWeb.Service.OrderService;
import com.example.EcommerceWeb.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

public class OrderServiceTest {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CartRepository cartRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    PaymentRepository paymentRepository;

    void createOrderTestSuccess(){
        User user=new User();
        user.setName("abc");
        Cart cart = new Cart();
        CartItem item = new CartItem();
        Product product = new Product();
        product.setStock(10);
        product.setDiscountedPrice(100);

        item.setProduct(product);
        item.setQuantity(2);

        cart.setItems(List.of(item));
        user.setCart(cart);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Order order=orderService.createOrder(1);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThat(order.getTotalAmount()).isEqualTo(200);
        assertThat(order.getItems().size()).isEqualTo(1);

        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void getOrderByUserTest(){
        User user=new User();
        Order order1=new Order();
        Order order2=new Order();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order1,order2));
        List<OrderDTO> orders=orderService.getOrderByUser(1);
        assertThat(orders).hasSize(2);
    }

    @Test
    void placeDirectOrderWithNoStock(){
        User user = new User();
        Product product = new Product();
        product.setStock(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(5)).thenReturn(Optional.of(product));
        assertThatThrownBy(()->orderService.placeDirectOrder(1,1,2)).isInstanceOf(RuntimeException.class).hasMessageContaining("Insufficient stock!");
    }

    @Test
    void getOrderById() {
        Order order = new Order();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getOrderById(1);

        assertThat(dto).isNotNull();
    }

    @Test
    void getOrderHistoryTest(){
        User user=new User();
        Order order1=new Order();
        order1.setStatus(OrderStatus.DELIVERED);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserAndStatus(user, OrderStatus.DELIVERED)).thenReturn(List.of(order1));
        List<OrderDTO> orders=orderService.getOrderHistory(1);
        assertThat(orders).hasSize(1);
    }
}
