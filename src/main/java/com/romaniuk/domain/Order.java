package com.romaniuk.domain;

import java.util.List;
import java.util.Objects;

/** Order is a class that represents a customer's order
 * It contains details about the order such as the unique identifier
 * list of items, total amount, and the customers email address
 */
public class Order {

    private final String id;
    private final List<String> items;
    private final double amount;
    private final String customerEmail;

    public Order(String id, List<String> items, double amount, String customerEmail) {
        this.id = id;
        this.items = items;
        this.amount = amount;
        this.customerEmail = customerEmail;
    }

    public String getId() {
        return id;
    }

    public List<String> getItems() {
        return items;
    }

    public double getAmount() {
        return amount;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(order.amount, amount) == 0 &&
                Objects.equals(id, order.id) &&
                Objects.equals(items, order.items) &&
                Objects.equals(customerEmail, order.customerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, items, amount, customerEmail);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", items=" + items +
                ", amount=" + amount +
                ", customerEmail='" + customerEmail + '\'' +
                '}';
    }
}
