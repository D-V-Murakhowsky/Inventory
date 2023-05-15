package com.example.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;

import com.example.inventory.data.DbHelper;

import java.util.Map;


public class SpinnerArrayAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

    Map<String, String> values;
    DbHelper database;

    public SpinnerArrayAdapter(@NonNull Context context, int resource) {
        super(context, android.R.layout.simple_spinner_item);
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        database = new DbHelper( context );
        values = database.getShelvesNames();
        for (Map.Entry<String, String> value: values.entrySet()
             ) {
                this.add(value.getValue());
        }
    }

}
