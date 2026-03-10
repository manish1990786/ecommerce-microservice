package com.group33.ecommerce.payment_service.service;

import com.group33.ecommerce.payment_service.model.Payment;
//import com.group33.ecommerce.payment_service.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Optional<Payment> getPaymentById(String id);
    List<Payment> getPaymentsByOrderId(String orderId);
    void deletePayment(String id);
    void updatePaymentStatusByRazorpayOrderId(String razorpayOrderId, String razorpayStatus);
}
