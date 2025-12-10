package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.PaymentDTO;
import com.example.EcommerceWeb.DTO.PaymentRequestDTO;
import com.example.EcommerceWeb.Repository.OrderRepository;
import com.example.EcommerceWeb.Repository.PaymentRepository;
import com.example.EcommerceWeb.model.*;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import com.stripe.param.checkout.SessionCreateParams;
import java.time.LocalDateTime;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.model.checkout.Session;
import com.stripe.model.PaymentIntent;

@Service
public class PaymentService {
    @Autowired
PaymentRepository paymentRepository;
    @Autowired
OrderRepository orderRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    NotificationService notificationService;

    String STRIPE_API_KEY =System.getenv().get("STRIPE_API_KEY");
    String CLIENT_BASE_URL="http://localhost:8080";
    @Transactional
    public String initiate(PaymentRequestDTO requestDTO) throws Exception {
        Stripe.apiKey = STRIPE_API_KEY;
        String clientBaseURL = CLIENT_BASE_URL;
        Order order=orderRepository.findById(requestDTO.getOrderId()).orElseThrow(()->new RuntimeException("Order"+requestDTO.getOrderId()+"not found"));

        Customer customer = CustomerUtil.findOrCreateCustomer(order.getUser().getEmail(), order.getUser().getName());
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customer.getId())
                        .setSuccessUrl("http://localhost:8080/api/payments/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:8080/api/payments/cancel")
                        .putMetadata("order_id", String.valueOf(order.getId()));

        for (OrderItem orderItem : order.getItems()) {
            Product product=orderItem.getProduct();
            long unitAmount = product.getDiscountedPrice() * 100L;
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) orderItem.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .putMetadata("app_id", String.valueOf(product.getProductId()))
                                                            .setName(product.getProductName())
                                                            .build()
                                            )
                                            .setCurrency("inr")
                                            .setUnitAmount(unitAmount)
                                            .build())
                            .build());
        }
        Session session = Session.create(paramsBuilder.build());
        return session.getUrl();
    }

    public PaymentDTO getById(int id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        PaymentDTO dto = new PaymentDTO();
        dto.setId(p.getId());
        dto.setTransactionId(p.getTransactionId());
        dto.setStatus(p.getStatus().name());
        dto.setMethod(p.getPaymentMethod().name());
        dto.setAmount(p.getAmount());
        dto.setPaymentDate(p.getPaymentDate());
        return dto;
    }

    public String cod(PaymentRequestDTO paymentRequestDTO){
        Order order = orderRepository.findById(paymentRequestDTO.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CONFIRMED);
        Payment payment=new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setTransactionId(null);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        order.setPayment(saved);
        orderRepository.save(order);
        return "Order placed successfully";
    }
    public String paymentSuccess(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
        if ("succeeded".equals(paymentIntent.getStatus())) {
            String orderId = session.getMetadata().get("order_id");
            Order order = orderRepository.findById(Integer.parseInt(orderId))
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            Payment payment=new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.CARD);
            payment.setTransactionId(paymentIntent.getId());
            payment.setStatus(paymentIntent.getStatus().equals("succeeded") ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            payment.setPaymentDate(LocalDateTime.now());
            Payment saved = paymentRepository.save(payment);
            order.setPayment(saved);
            order.setStatus(paymentIntent.getStatus().equals("succeeded")? OrderStatus.CONFIRMED:OrderStatus.CANCELLED);
            orderRepository.save(order);

            String customerEmail = order.getUser().getEmail();
            String emailSubject = "Order Confirmation - #" + orderId;
            String emailBody = emailBody(order,paymentIntent);
            notificationService.sendEmail(customerEmail, emailSubject, emailBody);
            String customerPhone = order.getUser().getPhoneNumber();
            String smsMessage = "Your order #" + orderId + " is confirmed! Txn ID: " + paymentIntent.getId();
            notificationService.sendSms(customerPhone, smsMessage);
            return "Payment successful for session"+sessionId;
        } else {
            return"Payment not completed yet for session"+sessionId;
        }
    }

    public String emailBody(Order order,PaymentIntent intent){
        String emailBody = "Hi " + order.getUser().getName() + ",\n\n" +
                "Your order with ID " + order.getId() + " has been successfully confirmed and paid.\n" +
                "Transaction ID: " + intent.getId() + "\n" +
                "Amount: " + order.getTotalAmount() + "\n\n" +
                "Order details:\n";

        for (OrderItem item : order.getItems()) {
            emailBody += "- " + item.getProduct().getProductName() + " x " + item.getQuantity() +
                    " = " + (item.getQuantity() * item.getPrice()) + "\n";
        }
        emailBody += "\nThank you for shopping with us!";
       return emailBody;
    }
}
