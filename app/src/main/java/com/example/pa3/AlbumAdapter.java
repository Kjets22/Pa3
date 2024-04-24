package com.example.pa3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class AlbumAdapter extends ArrayAdapter<Album> {
    private final Context context;
    private final List<Album> albums;

    public AlbumAdapter(Context context, List<Album> albums) {
        super(context, 0, albums);
        this.context = context;
        this.albums = albums;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewAlbumName);
        Album album = getItem(position);
        textViewName.setText(album.getName());

        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        Button btnRename = convertView.findViewById(R.id.btnRename);
        Button btnOpen = convertView.findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(view -> {
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.putExtra("album_name", albums.get(position).getName());  // Pass album name
            context.startActivity(intent);
        });
        btnDelete.setOnClickListener(view -> {
            albums.remove(position);
            notifyDataSetChanged();
            saveAlbums();
        });

        btnRename.setOnClickListener(view -> {
            EditText input = new EditText(context);
            new AlertDialog.Builder(context)
                    .setTitle("Rename Album")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newName = input.getText().toString();
                        if (!newName.isEmpty()) {
                            getItem(position).setName(newName);
                            notifyDataSetChanged();
                            saveAlbums();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, whichButton) -> {})
                    .show();
        });

        return convertView;
    }

    private void saveAlbums() {
        try {
            SerializationUtils.serialize(albums, context.getFilesDir() + "/albums.ser");
        } catch (IOException e) {
            Toast.makeText(context, "Failed to save albums", Toast.LENGTH_SHORT).show();
        }
    }
}
