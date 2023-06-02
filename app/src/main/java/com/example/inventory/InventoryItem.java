package com.example.inventory;


public class InventoryItem{

    public final String itemName;
    public Integer itemQuantity;

    public InventoryItem(String itemName, Integer itemQuantity){
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;

    }
}
