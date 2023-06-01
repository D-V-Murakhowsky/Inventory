package com.example.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventory.data.DbContract;

public class ShelfCursorAdapter extends CursorAdapter {

    public ShelfCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.shelves_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(DbContract.ShelfEntry.COLUMN_SHELF_NAME);

        // Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
    }
}