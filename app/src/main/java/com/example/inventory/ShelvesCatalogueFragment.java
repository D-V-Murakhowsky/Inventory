package com.example.inventory;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventory.Search.SearchAdapter;
import com.example.inventory.data.DbContract;
import com.example.inventory.data.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShelvesCatalogueFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Adapter for the ListView
     */
    ShelfCursorAdapter mCursorAdapter;

    /**
     * Identifier for the item data loader
     */
    private static final int SHELF_LOADER = 0;

    /**
     * Sort order parameter for cursor loader and loader manager
     */
    private String DEFAULT_SORT_ORDER = null;

    /**
     * Options for Sorting dialog
     */
    private final String[] options = new String[]{"Alphabetical - Ascending", "Alphabetical - Descending", "Oldest first", "Newest first"};

    /**
     * Alphabetical - Ascending order
     */
    private static final int ASCENDING = 0;

    /**
     * Alphabetical - Descending order
     */
    private static final int DESCENDING = 1;

    /**
     * Oldest first order
     */
    private static final int OLDEST_FIRST = 2;

    /**
     * Newest first order
     */
    private static final int NEWEST_FIRST = 3;

    /**
     * Current Sort Choice
     */
    private static int sort_choice = 2;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchAdapter adapter;


    // Instance of the database
    DbHelper database;

    // Flag to determine when to stop loading suggestions
    public int flag1 = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.shelf_catalog, container,false);

        // Create a new instance of the database for access to the searchbar
        database = new DbHelper( getActivity() );

        // Create the search bar
//      LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // Find the ListView which will be populated with the pet data
        ListView shelfListView = (ListView) view.findViewById(R.id.catalog_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = view.findViewById(R.id.empty_view);
        shelfListView.setEmptyView(emptyView);

        mCursorAdapter = new ShelfCursorAdapter(getActivity(), null, 0);
        shelfListView.setAdapter(mCursorAdapter);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.catalog_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getActivity(), ShelfEditActivity.class);
                startActivity(intent);
            }
        });

        // Setup the item click listener


        shelfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent( getActivity(), ShelfViewActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentUri = ContentUris.withAppendedId(DbContract.ShelfEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(SHELF_LOADER, null, this);

        return view;

    }

    private void deleteAllItems() {
        int rowsDeleted = requireActivity().getApplicationContext().getContentResolver().delete(DbContract.ShelfEntry.CONTENT_URI, null, null);
        if (rowsDeleted >= 0) {
            Toast.makeText( requireActivity(), "All items deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText( requireActivity(), "An error occurred: Delete failed", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: 2018-07-08 put sortAllItems in setPositiveButton
    private void showSortConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( requireActivity(), R.style.RadioDialogTheme);
        builder.setTitle(R.string.sort_dialog_msg);
        builder.setSingleChoiceItems(options, sort_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortAllItems(which);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sortAllItems(int choice) {

        // Sort order
        switch (choice) {
            case ASCENDING:
                DEFAULT_SORT_ORDER = DbContract.ShelfEntry.COLUMN_SHELF_NAME + " COLLATE NOCASE ASC";
                sort_choice = 0;
                break;
            case DESCENDING:
                DEFAULT_SORT_ORDER = DbContract.ShelfEntry.COLUMN_SHELF_NAME + " COLLATE NOCASE DESC";
                sort_choice = 1;
                break;
            case OLDEST_FIRST:
                DEFAULT_SORT_ORDER = null;
                sort_choice = 2;
                break;
            case NEWEST_FIRST:
                DEFAULT_SORT_ORDER = DbContract.ShelfEntry._ID + " DESC";
                sort_choice = 3;
                break;

        }

        // Restart the LoaderManager so OnCreate can be called again with new parameters for the cursor
        getLoaderManager().restartLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DbContract.ShelfEntry._ID,
                DbContract.ShelfEntry.COLUMN_SHELF_NAME};

        Log.e("onCreateLoader", "DEFAULT_SORT_ORDER = " + DEFAULT_SORT_ORDER);


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(requireActivity(),   // Parent activity context
                DbContract.ShelfEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                DEFAULT_SORT_ORDER);                  // Default sort order


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        try {
            mCursorAdapter.swapCursor(null);
        } catch (NullPointerException ignored) {

        }
    }


}
