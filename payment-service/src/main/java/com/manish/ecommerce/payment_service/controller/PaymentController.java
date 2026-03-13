package com.manish.ecommerce.payment_service.controller;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manish.ecommerce.payment_service.model.Payment;
import com.manish.ecommerce.payment_service.service.PaymentService;
import com.manish.ecommerce.payment_service.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${razorpay.webhook_secret}")
    private String webhookSecret;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@Valid @RequestBody Payment payment) {
        try {
            Order order = razorpayService.createOrder(payment.getOrderId(), payment.getAmount());
            return ResponseEntity.ok(order.toString());
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment initiation failed: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        Payment savedPayment = paymentService.createPayment(payment);
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @Valid @RequestBody Payment updatedPayment) {
        Optional<Payment> existingPaymentOpt = paymentService.getPaymentById(id);

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();

            existingPayment.setAmount(updatedPayment.getAmount());
            existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
            existingPayment.setPaymentStatus(updatedPayment.getPaymentStatus());
            existingPayment.setOrderId(updatedPayment.getOrderId());

            Payment saved = paymentService.createPayment(existingPayment); // reuses save logic
            return ResponseEntity.ok(saved);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        Optional<Payment> existingPayment = paymentService.getPaymentById(id);

        if (existingPayment.isPresent()) {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(payload);

            String razorpayOrderId = root.path("payload")
                    .path("payment")
                    .path("entity")
                    .path("order_id")
                    .asText();

            String paymentStatus = root.path("payload")
                    .path("payment")
                    .path("entity")
                    .path("status")
                    .asText();

            Map<String, Object> response = paymentService.updatePaymentStatusByRazorpayOrderId(razorpayOrderId,
                    paymentStatus);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}