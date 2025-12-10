package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.OrderDTO;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.Service.OrderService;
import com.example.EcommerceWeb.customException.UserNotFoundException;
import com.example.EcommerceWeb.model.Order;
import com.example.EcommerceWeb.model.OrderStatus;
import com.example.EcommerceWeb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(Authentication authentication){
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        Order order=orderService.createOrder(user.getId());
        return new ResponseEntity<>(OrderDTO.orderToOrderDto(order),HttpStatus.OK);
    }
    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(Authentication authentication){
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        List<OrderDTO> orderDTO=orderService.getOrderByUser(user.getId());
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable int orderId, @RequestParam OrderStatus orderStatus){
        return new ResponseEntity<>(orderService.updateOrderStatus(orderId,orderStatus),HttpStatus.OK);
    }

    @GetMapping("/allOrders")
    public ResponseEntity<List<OrderDTO>> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10")int size,
                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                       @RequestParam(defaultValue = "desc") String sortDir){
        return new ResponseEntity<>(orderService.getAllOrders(page, size, sortBy, sortDir).getContent(),HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int orderId){
        return new ResponseEntity<>(orderService.getOrderById(orderId),HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrderByStatus(@PathVariable("status") OrderStatus orderStatus){
        return new ResponseEntity<>(orderService.getOrderByStatus(orderStatus),HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getHistory(Authentication authentication) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        return new ResponseEntity<>(orderService.getOrderHistory(user.getId()),HttpStatus.OK);
    }

    @PostMapping("/direct")
    public ResponseEntity<OrderDTO> placeDirectOrder(Authentication authentication,@RequestParam int productId,@RequestParam(defaultValue = "1") int quantity) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User does not exist"));
        OrderDTO order = orderService.placeDirectOrder(user.getId(), productId, quantity);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
