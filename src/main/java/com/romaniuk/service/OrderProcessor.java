package com.romaniuk.service;

import com.romaniuk.domain.Order;

/**
 * OrderProcessor is a class that processes customer orders
 */
public class OrderProcessor {

    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    public OrderProcessor(InventoryService inventoryService, PaymentGateway paymentGateway, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    public boolean processOrder(Order order) {

        for (String item : order.getItems()) {
            if (!inventoryService.checkStock(item)) {
                notificationService.notifyFailure(order.getCustomerEmail(), "Out of stock: " + item);
                return false;
            }
        }

        if (order.getAmount() <= 0) {
            notificationService.notifyFailure(order.getCustomerEmail(), "Invalid amount");
            return false;
        }

        if (paymentGateway.process(order.getAmount())) {

            for (String item : order.getItems()) {
                inventoryService.reserveItem(item);
            }

            notificationService.notifySuccess(order.getCustomerEmail());
            return true;
        } else {
            notificationService.notifyFailure(order.getCustomerEmail(), "Payment failed");
            return false;
        }
    }
}