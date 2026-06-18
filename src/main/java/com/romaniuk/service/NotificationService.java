package com.romaniuk.service;

/**
 * NotificationService is an interface that defines methods for sending notifications
 * to customers about the status of their orders
 */
public interface NotificationService {

    void notifySuccess(String email);

    void notifyFailure(String email, String reason);
}
