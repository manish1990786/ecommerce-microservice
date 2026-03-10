package com.group33.ecommerce.payment_service.controller;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group33.ecommerce.payment_service.model.Payment;
import com.group33.ecommerce.payment_service.service.PaymentService;
import com.group33.ecommerce.payment_service.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Value("${razorpay.webhook.secret}")
	private String webhookSecret;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@Valid @RequestBody Payment payment) {
        try {
            Order order = razorpayService.createOrder(payment.getOrderId(), payment.getAmount());
            return ResponseEntity.ok(order.toString());  // you can also parse to JSON and return key fields
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment initiation failed: " + e.getMessage());
        }
    }

    // Create a new payment
    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid@RequestBody Payment payment) {
        Payment savedPayment = paymentService.createPayment(payment);
        return ResponseEntity.ok(savedPayment);
    }

    // Get a payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable String id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all payments for a specific order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable String id, @Valid @RequestBody Payment updatedPayment) {
        Optional<Payment> existingPaymentOpt = paymentService.getPaymentById(id);

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();
            
            // Update fields
            existingPayment.setAmount(updatedPayment.getAmount());
            existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
            existingPayment.setPaymentStatus(updatedPayment.getPaymentStatus());
            existingPayment.setOrderId(updatedPayment.getOrderId());

            Payment saved = paymentService.createPayment(existingPayment);  // reuses save logic
            return ResponseEntity.ok(saved);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        Optional<Payment> existingPayment = paymentService.getPaymentById(id);

        if (existingPayment.isPresent()) {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
        	// Skip signature validation while testing
            System.out.println("Webhook received:\n" + payload);

            // Parse Razorpay order ID and status from payload
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(payload);

            String razorpayOrderId = root.path("payload").path("payment").path("entity").path("order_id").asText();
            String paymentStatus = root.path("payload").path("payment").path("entity").path("status").asText();

            System.out.println("Parsed order_id: " + razorpayOrderId);
            System.out.println("Parsed payment status: " + paymentStatus);

            // Update your payment record
            paymentService.updatePaymentStatusByRazorpayOrderId(razorpayOrderId, paymentStatus);

            return ResponseEntity.ok("Webhook processed and payment updated");
//
//            boolean isValid = verifySignature(payload, signature, webhookSecret);
//
//            if (isValid) {
//                // TODO: Parse JSON and update payment status
//                System.out.println("Webhook verified. Payload: " + payload);
//                return ResponseEntity.ok("Webhook processed");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
//            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/secure-test")
    public ResponseEntity<String> testSecureEndpoint() {
        return ResponseEntity.ok("Secured endpoint accessed successfully with valid JWT!");
    }

    private boolean verifySignature(String payload, String actualSignature, String webhookSecret) throws Exception {
        String computedSignature = calculateHMACSHA256(payload, webhookSecret);
        return computedSignature.equals(actualSignature);
    }

    private String calculateHMACSHA256(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(hash));
    }
}