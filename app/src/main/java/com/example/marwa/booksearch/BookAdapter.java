package com.example.marwa.booksearch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Marwa on 11/5/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Resource ID for the background color for this list of books
     */
    private int mColorResourceId;

    /**
     * Construct a new {@link BookAdapter}
     *
     * @param context of the app
     * @param books   is the list of books which is the data source of the adapter
     */
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Check if there is an existing list item view{called convertView} that we can
        // reuse, otherwise, if convertView is null, then inflate a new list item layout
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_text_view);
        // Display the title of the current book in that TextView
        titleView.setText(currentBook.getTitle());

        // Find the TextView with view ID author
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_text_view);
        // Display the author of the current book in that TextView
        authorView.setText(currentBook.getAuthor());

        // Find the ImageView in the list_item.xml layout with the ID image.
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        final String imageUrl = currentBook.getImageResource();

        // Check if an image is provided for this book or not
        if (currentBook.hasImage()) {
            // Set the ImageView to the image resource specified in the current book
            // If an image is available, display the provided image based on the resource ID
            // Display the image of the current book in that ImageView
            new DownLoadImageTask(imageView).execute(imageUrl);
        } else {
            // Otherwise set Virtual Image
            imageView.setImageResource(R.mipmap.ic_launcher);
        }


        // Magnify image when user click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Take This Screen Height and width
                DisplayMetrics display = new DisplayMetrics();
                // Find this screen
                ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(display);
                // Get height
                int height = display.heightPixels;
                // Get width
                int width = display.widthPixels;
                // Now make inflater
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflate layout take my layout id , id of layout that have imageView
                View v = inflater.inflate(R.layout.zoom_layout, (ViewGroup) ((Activity) getContext()).findViewById(R.id.layout));
                // Define imageview from this view c
                ImageView img = (ImageView) v.findViewById(R.id.zoom_img);
                // Set image in imageView
                if (imageUrl.equals("")) {
                    img.setImageResource(R.mipmap.ic_launcher);
                } else {
                    new DownLoadImageTask(img).execute(imageUrl);
                }
                // Set height of image is height from DisplayMetrics Full Screen
                img.setMinimumHeight(height);
                // Set width of image is width from DisplayMetrics Full Screen
                img.setMinimumWidth(width);
                img.setMaxHeight(height);
                img.setMaxWidth(width);
                // Show this sub layout in Toast
                Toast toast = new Toast(getContext().getApplicationContext());
                // Set view
                toast.setView(v);
                // Show this toast
                toast.show();
            }
        });

        // Return the listItem
        return listItemView;
    }

    // Download the Image
    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                Log.v("download", e.getMessage());

            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
