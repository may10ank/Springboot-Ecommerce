package com.example.EcommerceWeb.service;

import com.example.EcommerceWeb.DTO.PaymentRequestDTO;
import com.example.EcommerceWeb.Repository.OrderRepository;
import com.example.EcommerceWeb.Repository.PaymentRepository;
import com.example.EcommerceWeb.Service.NotificationService;
import com.example.EcommerceWeb.Service.OrderService;
import com.example.EcommerceWeb.Service.PaymentService;
import com.example.EcommerceWeb.model.*;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.billingportal.SessionCreateParams;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class PaymentServiceTest {
    @InjectMocks
    PaymentService paymentService;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderService orderService;
    @Mock
    NotificationService notificationService;

    @Test
    void testPaymentInitiation() throws Exception{
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setOrderId(1);

        User user = new User();
        user.setEmail("abc@x.com");
        user.setName("xyz");

        Product product = new Product();
        product.setProductId(5);
        product.setProductName("Phone");
        product.setDiscountedPrice(200);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(1);
        order.setUser(user);
        order.getItems().add(item);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn("2");

        try (MockedStatic<CustomerUtil> utilMock = mockStatic(CustomerUtil.class);
             MockedStatic<Session> sessionMock = mockStatic(Session.class)) {

            utilMock.when(() ->
                    CustomerUtil.findOrCreateCustomer("x@y.com", "John")
            ).thenReturn(customer);

            Session session = mock(Session.class);
            when(session.getUrl()).thenReturn("http://stripe-session-url");

            sessionMock.when(() ->Session.create(anyMap())).thenReturn(session);

            String url = paymentService.initiate(requestDTO);

            assertThat(url).isEqualTo("http://stripe-session-url");
        }
    }

    @Test
    void paymentSuccessSuccessCase() throws Exception {
        User user = new User();
        user.setEmail("abc@x.com");
        user.setName("xyz");

        Order order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setTotalAmount(500);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Session session = mock(Session.class);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("order_id", "1");

        when(session.getPaymentIntent()).thenReturn("pi_123");
        when(session.getMetadata()).thenReturn(metadata);

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getStatus()).thenReturn("succeeded");
        when(intent.getId()).thenReturn("pi_123");

        try (MockedStatic<Session> mockSession = mockStatic(Session.class);
             MockedStatic<PaymentIntent> mockIntent = mockStatic(PaymentIntent.class)) {

            mockSession.when(() -> Session.retrieve("abc")).thenReturn(session);
            mockIntent.when(() -> PaymentIntent.retrieve("pi_123")).thenReturn(intent);

            String response = paymentService.paymentSuccess("abc");

            assertThat(response).contains("Payment successful");

            verify(orderRepository).save(order);
            verify(paymentRepository).save(any());
            verify(notificationService).sendEmail(any(), any(), any());
            verify(notificationService).sendSms(any(), any());
        }
    }

}

