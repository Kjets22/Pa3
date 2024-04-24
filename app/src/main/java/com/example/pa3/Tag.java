package com.example.pa3;

import java.io.Serializable;

public class Tag implements Serializable {
    private String type; // e.g., "location", "person"
    private String value;

    public Tag(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // Add getters and setters as necessary
}