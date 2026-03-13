package com.manish.ecommerce.payment_service.kafka;

import com.manish.ecommerce.payment_service.model.Payment;
import com.manish.ecommerce.payment_service.service.PaymentService;
import com.manish.ecommerce.payment_service.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestListener {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

@KafkaListener(topics = "orders", groupId = "payment-service-group")
public void handlePaymentRequest(String message) {
    System.out.println("Received order event: " + message);

    try {
        JSONObject json = new JSONObject(message);

        String eventType = json.getString("eventType");

        if (!"ORDER_CREATED".equals(eventType)) {
            System.out.println("Ignored event: " + eventType);
            return;
        }

        JSONObject data = json.getJSONObject("data");
        String orderId = data.getString("orderId");
        double amount = data.getDouble("totalAmount");

        Order order = razorpayService.createOrder(orderId, amount);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setRazorpayOrderId(order.get("id").toString());
        payment.setPaymentMethod("RAZORPAY");
        paymentService.createPayment(payment);

        JSONObject successPayload = new JSONObject();
        successPayload.put("orderId", orderId);
        successPayload.put("status", "SUCCESS");
        successPayload.put("razorpayOrderId", order.get("id").toString());

        kafkaTemplate.send("payment-status", successPayload.toString());

    } catch (RazorpayException e) {
        e.printStackTrace();

        JSONObject errorPayload = new JSONObject();
        errorPayload.put("status", "FAILED");
        errorPayload.put("error", e.getMessage());
        errorPayload.put("originalMessage", message);

        kafkaTemplate.send("payment-status", errorPayload.toString());

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}