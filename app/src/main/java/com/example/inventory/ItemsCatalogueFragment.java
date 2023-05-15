package com.example.inventory;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventory.Search.CustomSuggestionsAdapter;
import com.example.inventory.Search.RecyclerTouchListener;
import com.example.inventory.Search.SearchAdapter;
import com.example.inventory.Search.SearchResult;
import com.example.inventory.data.DbContract;
import com.example.inventory.data.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemsCatalogueFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Adapter for the ListView
     */
    ItemCursorAdapter mCursorAdapter;

    /**
     * Identifier for the item data loader
     */
    private static final int ITEM_LOADER = 0;

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

    MaterialSearchBar materialSearchBar;
    CustomSuggestionsAdapter customSuggestionsAdapter;


    // Contains all suggestions
    List<SearchResult> searchResultList = new ArrayList<>();


    // Instance of the database
    DbHelper database;

    // Flag to determine when to stop loading suggestions
    public int flag1 = 0;

    @Override
    public void onStart() {
        super.onStart();

        Log.e("catalog", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("catalog", "onResume");
        flag1 = 0;
        loadSearchResultList();
        customSuggestionsAdapter.setSuggestions(searchResultList);
    }

    @Override
    public void onPause() {
        super.onPause();

        flag1 = 1;
        Log.e("catalog", "onPause");
        materialSearchBar.clearSuggestions();
        materialSearchBar.disableSearch();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.item_catalog, container,false);

        // Create a new instance of the database for access to the searchbar
        database = new DbHelper( getActivity() );

        // Create the search bar
        materialSearchBar = (MaterialSearchBar) view.findViewById(R.id.search_bar1);
        materialSearchBar.setCardViewElevation(0);
        materialSearchBar.setPlaceHolder("Пошук");
        materialSearchBar.setHint("Введіть назву для пошуку");

//        try {
//            Field searchEdit = materialSearchBar.getClass().getDeclaredField("searchEdit");
//            searchEdit.setAccessible(true);
//            final EditText editText = (EditText) searchEdit.get(materialSearchBar);
//            assert editText != null;
//            editText.setText("Пошук");
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }


//      LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customSuggestionsAdapter = new CustomSuggestionsAdapter(inflater);

        if (flag1 == 0) {
            Log.e("catalog", "tried to set adapter");
            loadSearchResultList();
            customSuggestionsAdapter.setSuggestions(searchResultList);
            materialSearchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);
//          ^---- this line causes problems when starting a new intent
        }

        // Add flags to determine when to stop loading search results
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                materialSearchBar.disableSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (flag1 == 0) {
                    List<SearchResult> newSuggestions = loadNewSearchResultList();
                    customSuggestionsAdapter.setSuggestions(newSuggestions);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (flag1 == 0) {
//                    if (!materialSearchBar.isSuggestionsVisible()) {
//                        if (s.toString() != null && !s.toString().isEmpty()) {
//                            materialSearchBar.enableSearch();
//                        }
//                    }
//                }
            }
        });
        // Useless
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
//                if (!enabled)
//                    adapter = new SearchAdapter(getBaseContext(), database.getResult());
////                    recyclerView.setAdapter(null);i
//                if(enabled) {
//                    materialSearchBar.enableSearch();
//                    materialSearchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);
//                }
//                else {
//                    materialSearchBar.clearSuggestions();
//                    materialSearchBar.disableSearch();
//                }
//                if (flag1 == 0) {
//                    if (enabled)
//                        materialSearchBar.showSuggestionsList();
//                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                List<SearchResult> testResult1 = loadNewSearchResultList();
                if(testResult1.isEmpty()) {
                    Toast.makeText( requireActivity().getBaseContext(), "No Results Found",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                SearchResult testResult2 = testResult1.get(0);
                String testResult4 = testResult2.getName();
                int testResult3 = testResult2.getId();

                if(text.toString().toLowerCase().equals(testResult4.toLowerCase())){
//                    Toast.makeText(getBaseContext(), "Search Success!",
//                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent( getActivity(), ItemViewActivity.class);

                    Uri currentPetUri = ContentUris.withAppendedId(DbContract.ItemEntry.CONTENT_URI, testResult3);
                    // Set the URI on the data field of the intent
                    intent.setData(currentPetUri);

                    flag1 = 1;
                    materialSearchBar.clearSuggestions();
                    // probably dont need this line
                    materialSearchBar.disableSearch();

                    startActivity(intent);

                }
                else{
                    Toast.makeText( requireActivity().getBaseContext(), "No Results Found",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onButtonClicked(int buttonCode) {

//                recyclerView.setAdapter(null);

                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        Log.e("catalog", "button clicked");
                        materialSearchBar.clearSuggestions();
                        materialSearchBar.disableSearch();
                        break;
                    case MaterialSearchBar.BUTTON_SPEECH:
                        break;
                    default:
                        break;

                }
            }
        });
        // Doesn't work for custom adapters
        materialSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {

            @Override
            public void OnItemClickListener(int position, View v) {
                Log.e("catalog", "on item click");
                Log.e("on item click", String.valueOf(position));
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                Log.e("catalog", "on item delete");
            }
        });

        // On click method for suggestions
        RecyclerView searchrv = view.findViewById(R.id.mt_recycler);
        searchrv.addOnItemTouchListener(new RecyclerTouchListener( requireActivity().getApplicationContext(), searchrv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //id works with original search list but not new???
//                int _id = searchResultList.get(position)._id;
// TODO: 2018-07-13 clean up code here since we dont need _id anymore now that we have the searchResult object
                // need testResult1 to work for some reason???
                List<SearchResult> testResult1 = loadNewSearchResultList();
                SearchResult testResult2 = testResult1.get(position);
                int testResult3 = testResult2.getId();

//                Log.e("catalog", "position = " + String.valueOf(position));
//                Log.e("catalog", "_id = " + String.valueOf(_id));
//                Log.e("catalog", "testResult3 = " + String.valueOf(testResult3));


                Intent intent = new Intent( getActivity(), ItemViewActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(DbContract.ItemEntry.CONTENT_URI, testResult3);
                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                Log.e("catalog", "list item click");
                flag1 = 1;
                materialSearchBar.clearSuggestions();
                // probably dont need this line
                materialSearchBar.disableSearch();

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                TextView tv = (TextView) view.findViewById(R.id.search_text);
                materialSearchBar.setText(String.valueOf(tv.getText()));
            }
        }));

        // Find the ListView which will be populated with the pet data
        ListView itemListView = (ListView) view.findViewById(R.id.catalog_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = view.findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ItemCursorAdapter(getActivity(), null, 0);
        itemListView.setAdapter(mCursorAdapter);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.catalog_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getActivity(), ItemEditActivity.class);
                startActivity(intent);
            }
        });

        // Setup the item click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent( getActivity(), ItemViewActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(DbContract.ItemEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                Log.e("catalog", "list item click");
                flag1 = 1;
                materialSearchBar.clearSuggestions();
                materialSearchBar.disableSearch();

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        return view;

    }

    private void startSearch(String s) {

//        adapter = new SearchAdapter(this, database.getResultNames(s));
    }

    private void loadSearchResultList() {
        searchResultList = database.getResult();
    }

    private List<SearchResult> loadNewSearchResultList() {
        MySuggestions.newSuggestions = new ArrayList<>();
        MySuggestions.newSuggestions_id = new ArrayList<Integer>(10);
        loadSearchResultList();
        int i = 0;
        for (SearchResult searchResult : searchResultList) {
            if (searchResult.getName().toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                MySuggestions.newSuggestions.add(searchResult);
                MySuggestions.newSuggestions_id.add(searchResult.getId());
                MySuggestions.moreresults[i] = searchResult.getId();
                i++;

//                MySuggestions.newSuggestions_id.add(1);
                Log.d("_id", String.valueOf(searchResult.getId()));
            }
        }

        return MySuggestions.newSuggestions;

    }

    /* Methods to create menu */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu options from the res/menu/menu_catalog.xml file.
//        // This adds menu items to the app bar.
//        getMenuInflater().inflate(R.menu.menu_catalog, menu);
//
//        return true;
//
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // User clicked on a menu option in the app bar overflow menu
//        switch (item.getItemId()) {
//            // Respond to a click on the "Delete all entries" menu option
//            case R.id.action_delete_all_entries:
//                // delete entries
//                showDeleteAllConfirmationDialog();
//                return true;
//            case R.id.action_sort_all_entries:
//                // sort entries
//                showSortConfirmationDialog();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( requireActivity() );
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteAllItems();
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

    private void deleteAllItems() {
        int rowsDeleted = requireActivity().getApplicationContext().getContentResolver().delete(DbContract.ItemEntry.CONTENT_URI, null, null);
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
                DEFAULT_SORT_ORDER = DbContract.ItemEntry.COLUMN_ITEM_NAME + " COLLATE NOCASE ASC";
                sort_choice = 0;
                break;
            case DESCENDING:
                DEFAULT_SORT_ORDER = DbContract.ItemEntry.COLUMN_ITEM_NAME + " COLLATE NOCASE DESC";
                sort_choice = 1;
                break;
            case OLDEST_FIRST:
                DEFAULT_SORT_ORDER = null;
                sort_choice = 2;
                break;
            case NEWEST_FIRST:
                DEFAULT_SORT_ORDER = DbContract.ItemEntry._ID + " DESC";
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
                DbContract.ItemEntry._ID,
                DbContract.ItemEntry.COLUMN_ITEM_NAME,
                DbContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                DbContract.ItemEntry.COLUMN_ITEM_SHELF_NAME};

        Log.e("onCreateLoader", "DEFAULT_SORT_ORDER = " + DEFAULT_SORT_ORDER);


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(requireActivity(),   // Parent activity context
                DbContract.ItemEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                DEFAULT_SORT_ORDER);                  // Default sort order


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


}
