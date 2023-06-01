package com.example.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.inventory.data.DbContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class ShelfViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Content URI for the existing item
     */
    private Uri mCurrentItemUri;

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_SHELF_LOADER = 0;

    /**
     * References to TextViews
     */
    TextView descriptionView;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shelf_view);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // find references to TextViews
        descriptionView = (TextView) findViewById(R.id.item_description_field);

        // get ID of custom toolbar and set as the desired toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // this line shows back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        getLoaderManager().initLoader(EXISTING_SHELF_LOADER, null, this);

    }

    // TODO: 2018-07-08 need a dialog here
    private void deleteItem() {
        int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                    Toast.LENGTH_SHORT).show();
        }

        finish();

    }

    /* Methods to create menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_current_entry:
                // delete item from database
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_entry:
                Intent intent = new Intent(ShelfViewActivity.this,  ShelfEditActivity.class);
                intent.setData(mCurrentItemUri);
                startActivity(intent);
                return true;
            case android.R.id.home:
                // Navigate up to parent activity
                // Show a dialog later on asking if user really wants to leave
//                NavUtils.navigateUpFromSameTask(ItemActivity.this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                DbContract.ShelfEntry._ID,
                DbContract.ShelfEntry.COLUMN_SHELF_NAME,
                DbContract.ShelfEntry.COLUMN_SHELF_DESCRIPTION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(DbContract.ShelfEntry.COLUMN_SHELF_NAME);
            int descriptionColumnIndex = data.getColumnIndex(DbContract.ShelfEntry.COLUMN_SHELF_DESCRIPTION);

            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            String description = data.getString(descriptionColumnIndex);

            // set the title of the toolbar
            Objects.requireNonNull(getSupportActionBar()).setTitle(name);

            // Update the views on the screen with the values from the database
            descriptionView.setText(description);

            // set the title of the toolbar
            ActionBar currentActionBar = getSupportActionBar();
            assert currentActionBar != null;
            currentActionBar.setTitle(name);
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
            currentActionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // set the title of the toolbar
        getSupportActionBar().setTitle("");

        // Update the views on the screen with the values from the database
        descriptionView.setText("");

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
