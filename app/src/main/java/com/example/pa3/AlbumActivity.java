package com.example.pa3;

//package com.example.javafxphotos;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private List<Album> albums; // Class level variable
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
        findViewById(R.id.addPhotoButton).setOnClickListener(v -> requestStoragePermission());
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
        requestStoragePermission();
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
            albums = SerializationUtils.deserialize(getFilesDir() + "/albums.ser"); // Properly initialize albums here
            for (Album album : albums) {
                if (album.getName().equals(albumName)) {
                    return album.getPhotos();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("AlbumActivity", "Error loading albums", e);
        }
        return new ArrayList<>();
    }
    private static final int REQUEST_STORAGE_PERMISSION = 200;
    private void requestStoragePermission() {
        // Log to diagnose
        Log.d("Permissions", "Requesting storage permission.");

        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permissions", "Permission not granted. Requesting permission.");

            // No explanation needed, just request the permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
            openGallery();
        } else {
            // Permission has already been granted
            Log.d("Permissions", "Permission already granted. Proceeding to open gallery.");
            openGallery();
        }
    }
/*
    private void requestStoragePermission() {
        System.out.println("in request storage");
        // Check if permission is not granted
        Log.d("Permission", "Checking permission.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            Log.d("Permission", "Permission not granted. Requesting...");
            //if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "reached", Toast.LENGTH_SHORT).show();
                // Show an explanatory dialog or toast before requesting permission again
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is needed to access photos from your device.")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(AlbumActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_STORAGE_PERMISSION))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                //requestStoragePermission();
        } else {
            Log.d("Permission", "Permission already granted.");
            // Permission has already been granted
            openGallery();
        }
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Log.d("Permissions", "Permission granted by user. Opening gallery.");
                openGallery();
            } else {
                // Permission denied
                Log.d("Permissions", "Permission denied by user.");
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, continue with opening the gallery
                openGallery();
            } else {
                // Permission denied, disable the functionality that depends on this permission.
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            displayImage(selectedImageUri);  // Display the image using the URI directly
        }
    }
    private void displayImage(Uri imageUri) {
        Glide.with(this).load(imageUri).into(photoImageView);
    }

/*
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }*/
private String getPathFromUri(Uri uri) {
    Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
    return null;
}

    private void addPhotoToAlbum(Uri imageUri) {
        String uriString = imageUri.toString(); // Save the URI String
        Photo newPhoto = new Photo(uriString); // Adjust Photo constructor to handle URI String
        photos.add(newPhoto); // Assuming 'photos' is your current album's photo list
        displayImage(imageUri); // Display newly added photo
        try {
            saveAlbums(); // Persist changes
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveAlbums() throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(new File(getFilesDir(), "albums.ser"));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(albums); // Correctly reference the class member
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }


}