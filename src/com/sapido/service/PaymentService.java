package com.sapido.service;

public interface PaymentService {
    boolean processPayment(String rideId, double amount);
    void refund(String rideId, double amount);
}
