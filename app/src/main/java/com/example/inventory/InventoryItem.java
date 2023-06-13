package com.example.inventory;


public class InventoryItem{

    public final String itemName;
    public Integer itemQuantity;

    public final String barcode;

    public InventoryItem(String itemName, Integer itemQuantity, String barCode){
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.barcode = barCode;
    }
}
