package com.manish.ecommerce.payment_service.service;
import com.manish.ecommerce.payment_service.model.Payment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getPaymentsByOrderId(String orderId);
    void deletePayment(Long id);
    Map<String, Object> updatePaymentStatusByRazorpayOrderId(String razorpayOrderId, String razorpayStatus);
}
