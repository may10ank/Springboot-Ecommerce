package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.OrderDTO;
import com.example.EcommerceWeb.DTO.OrderItemDTO;
import com.example.EcommerceWeb.Repository.*;
import com.example.EcommerceWeb.customException.ProductNotFoundException;
import com.example.EcommerceWeb.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    public Order createOrder(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user" + userId + "not found"));
        Cart cart = user.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("cart is empty");
        }
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(user.getAddress());
        order.setUser(user);
        int total = 0;
        for (CartItem cartItem : cart.getItems()) {
            Product product=cartItem.getProduct();
            product.reduceStock(cartItem.getQuantity(),product);
            productRepository.save(product);
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getDiscountedPrice());
            orderItem.setTotalPrice(cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
            total += orderItem.getTotalPrice();
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);
        return savedOrder;
    }

    @Transactional
    public OrderDTO placeDirectOrder(int userId, int productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(product.getDiscountedPrice() * quantity);
        order.setDeliveryAddress(user.getAddress());
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getDiscountedPrice());
        order.getItems().add(orderItem);

        product.reduceStock(quantity,product);
        productRepository.save(product);

        Order savedOrder = orderRepository.save(order);

        return OrderDTO.orderToOrderDto(savedOrder);
    }

    public List<OrderDTO> getOrderByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user" + userId + "not found"));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(OrderDTO::orderToOrderDto).collect(Collectors.toList());
    }

    public OrderDTO updateOrderStatus(int orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.orderToOrderDto(updatedOrder);
    }

    public Page<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortDir){
        Sort sort=sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(page,size,sort);
        Page<Order> orders=orderRepository.findAll(pageable);
        return orders.map(OrderDTO::orderToOrderDto);
    }

    public OrderDTO getOrderById(int orderId){
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found with id:"+orderId));
        return OrderDTO.orderToOrderDto(order);
    }

    public List<OrderDTO> getOrderByStatus(OrderStatus status){
        List<Order> order=orderRepository.findByStatus(status);
        return order.stream().map(OrderDTO::orderToOrderDto).collect(Collectors.toList());
    }

    public List<OrderDTO> getOrderHistory(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user" + userId + "not found"));
        List<Order> orders = orderRepository.findByUserAndStatus(user,OrderStatus.DELIVERED);
        return orders.stream().map(OrderDTO::orderToOrderDto).collect(Collectors.toList());
    }
}
