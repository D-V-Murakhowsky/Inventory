package com.example.inventory.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.inventory.InventoryItem;
import com.example.inventory.Search.SearchResult;
import com.example.inventory.data.DbContract.ItemEntry;
import com.example.inventory.data.DbContract.ShelfEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DbHelper extends SQLiteOpenHelper{

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "myInventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Context
     */

    Context context;

    /**
     * Constructs a new instance of ItemDbHelper
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e("DbHelper","inside onCreate");
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_SHELF_ID + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_ITEM_SHELF_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TAG1 + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TAG2 + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TAG3 + " TEXT, "
                + ItemEntry.COLUMN_ITEM_IMAGE + " BLOB, "
                + ItemEntry.COLUMN_ITEM_URI + " TEXT); ";

        String SQL_CREATE_SHELVES_TABLE = "CREATE TABLE " + DbContract.ShelfEntry.TABLE_NAME + " ("
                + ShelfEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ShelfEntry.COLUMN_SHELF_NAME + " TEXT NOT NULL, "
                + ShelfEntry.COLUMN_SHELF_DESCRIPTION + " TEXT); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
        db.execSQL(SQL_CREATE_SHELVES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("ItemDbHelper","inside onUpgrade");
    }

    // Function to get the search results
    public List<SearchResult> getResult(){

        // need to get all results but show only 5 somehow ...
        String sortOrder = "ROWID LIMIT 5";

        String[] projection = {
                DbContract.ItemEntry._ID,
                DbContract.ItemEntry.COLUMN_ITEM_NAME,
                DbContract.ItemEntry.COLUMN_ITEM_QUANTITY};

        Cursor cursor = context.getContentResolver().query(ItemEntry.CONTENT_URI, projection, null, null, null);

        List<SearchResult> searchResults = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int idColumnIndex = cursor.getColumnIndex( ItemEntry._ID );
                int nameColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_NAME );
                int quantityColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_QUANTITY );

                if ((idColumnIndex > - 1) && (nameColumnIndex > -1)
                        && (quantityColumnIndex > -1) ) {
                    SearchResult result = new SearchResult();
                    result.setId(cursor.getInt(idColumnIndex));
                    result.setName(cursor.getString(nameColumnIndex));
                    result.setQuantity(cursor.getDouble(quantityColumnIndex));
                    searchResults.add(result);
                }

            }while(cursor.moveToNext());
        }

        return searchResults;
    }

    // added an 's' after getName
    // dont really need these 2 methods
    public List<String> getNames(){

        String[] projection = {
                DbContract.ItemEntry.COLUMN_ITEM_NAME};

        Cursor cursor = context.getContentResolver().query(ItemEntry.CONTENT_URI, projection, null, null,null);

        List<String> searchResults = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int nameColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_NAME );
                if (nameColumnIndex > -1) {
                    searchResults.add(cursor.getString(nameColumnIndex));
                }
            }while(cursor.moveToNext());
        }

        return searchResults;
    }

    public HashMap<String, String> getShelvesNames(){
        String [] projection = {ShelfEntry._ID, ShelfEntry.COLUMN_SHELF_NAME};

        Cursor cursor = context.getContentResolver().query(ShelfEntry.CONTENT_URI, projection,
                null, null,null);

        HashMap<String, String> searchResults = new HashMap<>();
        if(cursor.moveToFirst()){
            do{
                int idColumnIndex = cursor.getColumnIndex( ShelfEntry._ID );
                int nameColumnIndex = cursor.getColumnIndex( ShelfEntry.COLUMN_SHELF_NAME );
                if ((nameColumnIndex > -1) && (idColumnIndex > -1)){
                    searchResults.put(cursor.getString(idColumnIndex),
                            cursor.getString(nameColumnIndex));
                }
            }while(cursor.moveToNext());
        }

        return searchResults;
    }

    public ArrayList<InventoryItem> getInventoryItemsWithQuantities(String shelfIdValue){
        String [] projection = {ItemEntry._ID,
                                ItemEntry.COLUMN_ITEM_NAME,
                                ItemEntry.COLUMN_ITEM_SHELF_ID,
                                ItemEntry.COLUMN_ITEM_QUANTITY};

        Cursor cursor = context.getContentResolver().query(ItemEntry.CONTENT_URI, projection,
                null, null,null);

        ArrayList<InventoryItem> searchResults = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int idColumnIndex = cursor.getColumnIndex( ItemEntry._ID );
                int nameColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_NAME );
                int shelfIdColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_SHELF_ID );
                int qtyColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_QUANTITY );

                if ((nameColumnIndex > -1) && (idColumnIndex > -1) &&
                        (shelfIdColumnIndex > -1) && (qtyColumnIndex > -1)){
                    if (Objects.equals(cursor.getString(shelfIdColumnIndex), shelfIdValue)) {
                        searchResults.add(new InventoryItem(cursor.getString(nameColumnIndex),
                                                            Integer.valueOf(cursor.getString(qtyColumnIndex))
                                ));
                    }}
            }while(cursor.moveToNext());
        }

        return searchResults;
    }

    public List<SearchResult> getResultNames(String name){

        String[] projection = {
                DbContract.ItemEntry._ID,
                DbContract.ItemEntry.COLUMN_ITEM_NAME};

        String selection = ItemEntry.COLUMN_ITEM_NAME + " LIKE ?";
        String[] selectionArgs = new String[] {"&" + name + "%"};
        String[] selectionArgs2 = new String[] {name};

        Cursor cursor = context.getContentResolver().query(ItemEntry.CONTENT_URI, projection, selection, selectionArgs2,null);

        List<SearchResult> searchResults = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int idColumnIndex = cursor.getColumnIndex( ItemEntry._ID );
                int nameColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_NAME );
                int quantityColumnIndex = cursor.getColumnIndex( ItemEntry.COLUMN_ITEM_QUANTITY );

                if ((idColumnIndex > - 1) && (nameColumnIndex > -1)
                        && (quantityColumnIndex > -1)) {
                    SearchResult result = new SearchResult();
                    result.setId(cursor.getInt(idColumnIndex));
                    result.setName(cursor.getString(nameColumnIndex));
                    result.setQuantity(cursor.getDouble(quantityColumnIndex));
                    searchResults.add(result);
                }

            }while(cursor.moveToNext());
        }

        return searchResults;
    }

}