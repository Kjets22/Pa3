package com.example.pa3;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Album> albums;
    private ArrayAdapter<String> adapter; // Changed to ArrayAdapter<String> for display purposes
    private ListView listViewAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewAlbums = findViewById(R.id.listViewAlbums);

        // Load albums
        try {
            albums = SerializationUtils.deserialize(getFilesDir() + "/albums.ser");
        } catch (IOException | ClassNotFoundException e) {
            albums = new ArrayList<>(); // Create a new list if there was an error loading
        }

        // Set up the adapter to display album names
        List<String> albumNames = new ArrayList<>();
        for (Album album : albums) {
            albumNames.add(album.getName());
        }
        AlbumAdapter adapter = new AlbumAdapter(this, albums);
        listViewAlbums.setAdapter(adapter);
        listViewAlbums.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            intent.putExtra("album_name", albums.get(position).getName());
            startActivity(intent);
        });
    }

    // Add new album
    public void onAddAlbumClicked(View view) {
        EditText input = new EditText(this); // For entering new album name
        new AlertDialog.Builder(this)
                .setTitle("New Album Name")
                .setView(input)
                .setPositiveButton("Add", (dialog, whichButton) -> {
                    String name = input.getText().toString();
                    if (!name.isEmpty()) {
                        Album newAlbum = new Album(name);
                        albums.add(newAlbum);
                        ((AlbumAdapter)listViewAlbums.getAdapter()).notifyDataSetChanged();
                        try {
                            SerializationUtils.serialize(albums, getFilesDir() + "/albums.ser");
                        } catch (IOException e) {
                            Toast.makeText(this, "Failed to save albums", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {})
                .show();
    }

}
