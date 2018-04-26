package com.example.marwa.booksearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Marwa on 11/30/2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();

    /**
     * Query URL
     */
    private Bundle mArgs;

    /**
     * Cache the old books
     */
    private List<Book> cachedBooks;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param args    to get data from
     */
    BookLoader(Context context, Bundle args) {
        super(context);
        mArgs = args;
    }

    @Override
    protected void onStartLoading() {
        // If args is null, return.
        if (mArgs == null) {
            return;
        }

        // If books is not null, deliver that result. Otherwise, force a load
        if (cachedBooks != null) {
            deliverResult(cachedBooks);
        } else {
            forceLoad();
        }
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        /* Extract the search query from the args using our constant */
        String searchQueryUrlString = mArgs.getString(MainActivity.SEARCH_QUERY_URL);

        //  If the URL is null or empty, return null
        if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books.
        List<Book> books = QueryUtils.fetchBookData(searchQueryUrlString);
        return books;
    }

    /**
     * Override deliverResult and store the data in returnedUrl
     */
    @Override
    public void deliverResult(List<Book> data) {
        cachedBooks = data;
        super.deliverResult(data);
    }
}
