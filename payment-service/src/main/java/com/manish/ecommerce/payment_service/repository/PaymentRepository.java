package com.manish.ecommerce.payment_service.repository;

import com.manish.ecommerce.payment_service.model.Payment;
import com.manish.ecommerce.payment_service.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByOrderId(String orderId);
    List<Payment> findByPaymentStatus(PaymentStatus status);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
