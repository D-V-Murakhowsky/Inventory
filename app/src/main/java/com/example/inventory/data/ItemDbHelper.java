package com.example.inventory.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.inventory.Search.SearchResult;
import com.example.inventory.data.ModelsContract.ItemEntry;

import java.util.ArrayList;
import java.util.List;

public class ItemDbHelper extends SQLiteOpenHelper{

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
    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e("ItemDbHelper","inside onCreate");
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TAG1 + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TAG2 + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TAG3 + " TEXT, "
                + ItemEntry.COLUMN_ITEM_IMAGE + " BLOB, "
                + ItemEntry.COLUMN_ITEM_URI + " TEXT); ";


        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
        /*
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + PetEntry.TABLE_NAME + " ("
                + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetEntry.COLUMN_PET_BREED + " TEXT, "
                + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
         */
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
                ModelsContract.ItemEntry._ID,
                ModelsContract.ItemEntry.COLUMN_ITEM_NAME,
                ModelsContract.ItemEntry.COLUMN_ITEM_QUANTITY};

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
                ModelsContract.ItemEntry.COLUMN_ITEM_NAME};

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

    public List<SearchResult> getResultNames(String name){

        String[] projection = {
                ModelsContract.ItemEntry._ID,
                ModelsContract.ItemEntry.COLUMN_ITEM_NAME};

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