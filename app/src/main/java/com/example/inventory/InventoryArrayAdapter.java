package com.example.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class InventoryArrayAdapter extends ArrayAdapter<InventoryItem>{

    public InventoryArrayAdapter(@NonNull Context context, ArrayList<InventoryItem> inventory) {
        super(context, 0, inventory);
    }

    public View getView(int position,
                        View convertView,
                        ViewGroup parent){

        InventoryItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_list_item,
                    parent, false);
        }
        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        TextView quantityView = (TextView) convertView.findViewById(R.id.quantity);

        nameView.setText(item.itemName);
        quantityView.setText(Integer.toString(item.itemQuantity));

        return convertView;

    }

}
