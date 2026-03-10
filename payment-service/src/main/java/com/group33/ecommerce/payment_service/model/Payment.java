package com.group33.ecommerce.payment_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
@Entity
public class Payment {

	@Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull(message = "Order ID cannot be null")
    // @Positive(message = "Order ID must be a positive number")
    private String orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1", message = "Amount must be greater than 0")
    private Double amount;
    
    private String razorpayOrderId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    private LocalDateTime paymentDate;

    public Payment() {}
    
}
