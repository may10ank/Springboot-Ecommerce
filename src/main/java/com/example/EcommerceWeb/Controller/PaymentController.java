package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.PaymentDTO;
import com.example.EcommerceWeb.DTO.PaymentRequestDTO;
import com.example.EcommerceWeb.Repository.OrderRepository;
import com.example.EcommerceWeb.Repository.PaymentRepository;
import com.example.EcommerceWeb.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiate(@RequestBody PaymentRequestDTO paymentRequestDTO) throws Exception {
        if(paymentRequestDTO.getPaymentMethod()=="COD"){
            String result1=paymentService.cod(paymentRequestDTO);
            return new ResponseEntity<>(result1,HttpStatus.OK);
        }
        return new ResponseEntity<>(paymentService.initiate(paymentRequestDTO), HttpStatus.OK);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<PaymentDTO> getHistory(@PathVariable int id){
        return new ResponseEntity<>(paymentService.getById(id),HttpStatus.OK);
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("session_id") String sessionId) throws Exception {
        String result= paymentService.paymentSuccess(sessionId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        return new ResponseEntity<>("Payment was cancelled by the user",HttpStatus.OK);
    }
}
