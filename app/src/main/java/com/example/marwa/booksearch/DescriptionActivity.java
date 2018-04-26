package com.example.marwa.booksearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    // Declare TextViw, description of the book
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // Get the description of the book
        Intent intent = getIntent();
        String desc = intent.getStringExtra(MainActivity.DESC);
        // Initialize the TextView
        description = (TextView) findViewById(R.id.desc);
        // If there is no description
        if(desc.equals("")){
            description.setText(getString(R.string.noDescription));
        }else {
            description.setText(desc);
        }
    }
}
