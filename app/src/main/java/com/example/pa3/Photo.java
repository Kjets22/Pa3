package com.example.pa3;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private String imagePath;
    private List<Tag> tags;

    public Photo(String imagePath) {
        this.imagePath = imagePath;
        this.tags = new ArrayList<>();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    // Add getters and setters as necessary
}