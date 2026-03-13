package com.manish.ecommerce.payment_service.service;

import com.manish.ecommerce.payment_service.model.Payment;
import com.manish.ecommerce.payment_service.model.PaymentStatus;
import com.manish.ecommerce.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> updatePaymentStatusByRazorpayOrderId(String razorpayOrderId, String razorpayStatus) {

        Map<String, Object> response = new HashMap<>();

        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);

        if (paymentOpt.isPresent()) {

            Payment payment = paymentOpt.get();
            PaymentStatus status = mapRazorpayStatus(razorpayStatus);

            payment.setPaymentStatus(status);
            paymentRepository.save(payment);

            response.put("status", "success");
            response.put("message", "Payment status updated");
            response.put("orderId", razorpayOrderId);
            response.put("paymentStatus", status);

        } else {

            response.put("status", "error");
            response.put("message", "Payment not found for Razorpay Order ID");
            response.put("orderId", razorpayOrderId);

            System.out.println("No payment found for Razorpay Order ID: " + razorpayOrderId);
        }

        return response;
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
