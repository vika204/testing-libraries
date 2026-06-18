package com.romaniuk.service;

/**
 * PaymentGateway is an interface that defines a method for processing payments
 */
public interface PaymentGateway {

    boolean process(double amount);
}