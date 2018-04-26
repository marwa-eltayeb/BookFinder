package com.example.marwa.booksearch;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marwa on 11/5/2017.
 */

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null; //store our URL
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        // It seems like open up a browser and we are gonna use it to fetch the contents of a URl
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            // Set up url connection
            // To make sure that it is the right type, we cast it to HttpURLConnection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link book} object
            for (int i = 0; i < bookArray.length(); i++) {
                //String for book's title, author ,and Thumbnail
                String title = "";
                String author = "";
                String author2 = "";
                String thumbnail = "";
                String description = "";

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);
                // For a given book, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that book.
                JSONObject properties = currentBook.getJSONObject("volumeInfo");
                try {
                    // Extract the value for the key called "title"
                    title = properties.getString("title");
                    Log.v("Title", "title");
                } catch (Exception e) {
                    Log.v("Title", "No title");
                }
                try {
                    // Extract the value for the key called "authors"
                    //Author index 0
                    author = properties.getJSONArray("authors").get(0).toString();
                    //Author index 1
                    author2 = properties.getJSONArray("authors").get(1).toString();
                    //merge 2 author in one line
                    author = author2 + " & " + author2;
                    //Debugging
                    Log.v("author", "authors");
                } catch (Exception e) {
                    Log.v("author", "No authors");
                }

                try {
                    //get photo path
                    JSONObject imageObject = properties.getJSONObject("imageLinks");
                    // Extract the value for the key called "thumbnail"
                    thumbnail = imageObject.getString("thumbnail");
                    Log.v("thumbnail", thumbnail);
                } catch (Exception e) {
                    Log.v("thumbnail", "No image for this book");
                }

                try {
                    // Extract the value for the key called "description"
                    description = properties.getString("description");
                    Log.v("Description", description);
                } catch (Exception e) {
                    Log.v("Description", "No Description");
                }


                // Create a new {@link book} object with the title, author, thumbnail and description.
                // and url from the JSON response.
                Book book = new Book(title, author, thumbnail, description);
                // Add the new {@link Book} to the list of books.
                books.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // Return the list of books
        return books;
    }
}
