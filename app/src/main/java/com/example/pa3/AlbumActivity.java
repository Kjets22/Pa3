package com.example.pa3;

//package com.example.javafxphotos;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class AlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get album name passed from MainActivity
        String albumName = getIntent().getStringExtra("album_name");
        TextView textView = findViewById(R.id.albumName);
        textView.setText(albumName);

        // Load and display photos of the album
    }
}