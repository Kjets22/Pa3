package com.example.pa3;

//package com.example.javafxphotos;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private List<Photo> photos; // This should be retrieved from the Album object
    private int currentPhotoIndex = 0;
    private ImageView photoImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get album name passed from MainActivity
        String albumName = getIntent().getStringExtra("album_name");
        TextView textView = findViewById(R.id.albumName);
        textView.setText(albumName);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Load and display photos of the album
        photoImageView = findViewById(R.id.photoImageView);
        photos = loadPhotosForAlbum(albumName);  // You need to implement this method to fetch photos.
        setupButtons();
        displayCurrentPhoto();
        if (!photos.isEmpty()) {
            displayCurrentPhoto();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();  // This calls the default back button handling
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();  // This will handle the back navigation in line with the activity stack
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupButtons() {
        findViewById(R.id.addPhotoButton).setOnClickListener(v -> addPhoto());
        findViewById(R.id.removePhotoButton).setOnClickListener(v -> removePhoto());
        findViewById(R.id.nextPhotoButton).setOnClickListener(v -> showNextPhoto());
        findViewById(R.id.prevPhotoButton).setOnClickListener(v -> showPreviousPhoto());
    }

    private void displayCurrentPhoto() {
        if (photos != null && !photos.isEmpty()) {
            Photo currentPhoto = photos.get(currentPhotoIndex);
            Glide.with(this).load(new File(currentPhoto.getImagePath())).into(photoImageView);
        } else {
            photoImageView.setImageResource(android.R.color.transparent); // Clear the image if no photos
        }
    }

    private void addPhoto() {
        // Logic to add a photo to the album
    }

    private void removePhoto() {
        if (!photos.isEmpty() && currentPhotoIndex < photos.size()) {
            photos.remove(currentPhotoIndex);
            displayCurrentPhoto();
        }
    }

    private void showNextPhoto() {
        if (currentPhotoIndex < photos.size() - 1) {
            currentPhotoIndex++;
            displayCurrentPhoto();
        }
    }

    private void showPreviousPhoto() {
        if (currentPhotoIndex > 0) {
            currentPhotoIndex--;
            displayCurrentPhoto();
        }
    }

    private List<Photo> loadPhotosForAlbum(String albumName) {
        try {
            List<Album> albums = SerializationUtils.deserialize(getFilesDir() + "/albums.ser");
            for (Album album : albums) {
                if (album.getName().equals(albumName)) {
                    return album.getPhotos();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("AlbumActivity", "Error loading albums", e);
        }
        return new ArrayList<>();  // Return an empty list if no album found or an error occurs
    }

}