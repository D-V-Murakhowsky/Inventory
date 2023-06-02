package com.example.inventory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

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
        int idColumnIndex = cursor.getColumnIndex(DbContract.ShelfEntry._ID);

        // Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String idColumn = cursor.getString(idColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);

        TextView menuTextView = (TextView) view.findViewById(R.id.shelf_menu);
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, menuTextView);
                //inflating menu from xml resource
                popup.inflate(R.menu.shelf_list_view_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.start_inventory:
                                Intent intent = new Intent(context,  InventoryCatalogueActivity.class);
                                intent.putExtra("SHELF_ID", idColumn);
                                intent.putExtra("SHELF_NAME", itemName);
                                context.startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }
}
