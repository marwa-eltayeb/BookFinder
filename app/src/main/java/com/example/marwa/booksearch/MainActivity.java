package com.example.marwa.booksearch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /* A constant to save and restore the URL that is being displayed */
    public static final String SEARCH_QUERY_URL = "query";
    public int Book_LOADER_ID = 1;
    BookAdapter adapter;
    LoaderManager loaderManager;
    private MaterialSearchView searchView;
    private String myUrl;

    public static final String DESC = "desc";
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    // a ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView bookListView = (ListView) findViewById(R.id.book_list);
        // Create a new adapter that takes an empty list of books as input

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        //Find the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        if (savedInstanceState != null) {
            myUrl = savedInstanceState.getString(SEARCH_QUERY_URL);
        }


        adapter = new BookAdapter(this, new ArrayList<Book>());

        bookListView.setAdapter(adapter);


        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Book currentBook = adapter.getItem(position);

                String description = currentBook.getDescription();

                Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
                intent.putExtra(DESC, description);

                startActivity(intent);


            }
        });

        // Initialize the SearchView
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        // Initialize the Toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set Toolbar as Actionbar
        setSupportActionBar(mainToolbar);
        // Color of the title in the Toolbar
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        // Search for keyword
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        /*
        * Initialize the loader
        */
        if (myUrl != null) {
            getSupportLoaderManager().initLoader(Book_LOADER_ID, null, this);
        }
    }

    /**
     * Search for a certain category
     */
    private void search(String query) {
        //Clear the adapter of previous book data
        adapter.clear();
        mEmptyStateTextView.setVisibility(View.GONE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        try {
            if (networkInfo != null && networkInfo.isConnected()) {
                String bookName = URLEncoder.encode(query.trim().replaceAll(" ", "%20"), "UTF-8");
                // Set the URL with the suitable bookName
                myUrl = "https://www.googleapis.com/books/v1/volumes?q=" + bookName + "&maxResults=10";
                // Show the loading indicator.
                mLoadingIndicator.setVisibility(View.VISIBLE);
                // Create a bundle called queryBundle
                Bundle queryBundle = new Bundle();
                // Use putString with SEARCH_QUERY_URL as the key and the String value of the URL as the value
                queryBundle.putString(SEARCH_QUERY_URL, myUrl);
                // Call getSupportLoaderManager and store it in a LoaderManager variable
                loaderManager = getSupportLoaderManager();
                // Get our Loader by calling getLoader and passing the ID we specified
                Loader<String> BooksSearchLoader = loaderManager.getLoader(Book_LOADER_ID);
                if (BooksSearchLoader == null) {
                    loaderManager.initLoader(Book_LOADER_ID, queryBundle, MainActivity.this);
                } else {
                    loaderManager.restartLoader(Book_LOADER_ID, queryBundle, MainActivity.this);
                }
            } else {
                // Otherwise, display error
                // First, hide loading indicator so error message will be visible
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                // Update empty state with no connection error message
                //Toast.makeText(MainActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Set the methods of the Loader
    @Override
    public Loader<List<Book>> onCreateLoader(int id, final Bundle args) {
        return new BookLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);
        mEmptyStateTextView.setVisibility(View.VISIBLE);

        //Hide the indicator after the data is appeared
        mLoadingIndicator.setVisibility(View.GONE);

        //Clear the adapter pf previous book data
        adapter.clear();

        //If there is a valid list of {@link Book}s, then add them to the adapter's
        //data set. This will trigger the ListView to update
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    /**
     * Save the data of teh url
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY_URL, myUrl);
    }

    /**
     * Create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
}
