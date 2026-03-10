package com.manish.ecommerce.payment_service.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface RazorpayService {
    Order createOrder(String orderId, Double amount) throws RazorpayException;
}