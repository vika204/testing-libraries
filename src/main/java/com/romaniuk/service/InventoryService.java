package com.romaniuk.service;

/**
 * InventoryService is an interface that defines methods for
 * checking stock availability and reserving items in an inventory system
 */
public interface InventoryService {

    boolean checkStock(String item);

    void reserveItem(String item);
}
