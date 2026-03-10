package com.manish.ecommerce.payment_service.service;

import com.manish.ecommerce.payment_service.model.Payment;
import com.manish.ecommerce.payment_service.model.PaymentStatus;
import com.manish.ecommerce.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public void updatePaymentStatusByRazorpayOrderId(String razorpayOrderId, String razorpayStatus) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            PaymentStatus status = mapRazorpayStatus(razorpayStatus);
            payment.setPaymentStatus(status);
            paymentRepository.save(payment);
        } else {
            System.out.println("No payment found for Razorpay Order ID: " + razorpayOrderId);
        }
    }

    private static PaymentStatus mapRazorpayStatus(String razorpayStatus) {
        return switch (razorpayStatus.toLowerCase()) {
            case "captured" -> PaymentStatus.PAID;
            case "failed" -> PaymentStatus.FAILED;
            case "created", "authorized" -> PaymentStatus.PENDING;
            default -> PaymentStatus.PENDING;
        };
    }
}
