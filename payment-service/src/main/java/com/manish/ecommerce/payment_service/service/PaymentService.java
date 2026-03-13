package com.manish.ecommerce.payment_service.service;

import com.manish.ecommerce.payment_service.model.Payment;
//import com.manish.ecommerce.payment_service.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getPaymentsByOrderId(String orderId);
    void deletePayment(Long id);
    void updatePaymentStatusByRazorpayOrderId(String razorpayOrderId, String razorpayStatus);
}
