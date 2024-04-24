package com.example.pa3;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializationUtils {

    public static void serialize(List<Album> albums, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(albums);
        }
    }

    public static List<Album> deserialize(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>(); // Return an empty list if file doesn't exist
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Album>) ois.readObject();
        }
    }
}
