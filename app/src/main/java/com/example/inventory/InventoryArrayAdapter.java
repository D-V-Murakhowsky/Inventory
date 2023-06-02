package com.example.inventory;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;

import com.example.inventory.data.DbHelper;

import java.util.Map;


public class InventoryArrayAdapter extends ArrayAdapter<InventoryItem> implements SpinnerAdapter {

    Map<String, InventoryItem> values;
    DbHelper database;

    public InventoryArrayAdapter(@NonNull Context context, int resource) {
        super(context, android.R.layout.simple_spinner_item);
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        database = new DbHelper( context );
        values = database.getInventoryItemWithQuantities("1");
        for (Map.Entry<String, InventoryItem> value: values.entrySet()
             ) {
                this.add(value.getValue());
        }
    }

}
