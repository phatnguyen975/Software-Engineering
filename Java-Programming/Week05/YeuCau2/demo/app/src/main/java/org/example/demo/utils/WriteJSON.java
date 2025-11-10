package org.example.demo.utils;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.demo.models.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WriteJSON {
    public static void writeUserDataGson(Path filePath, User user) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(user, writer);
            System.out.println("(Gson) JSON data written successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing JSON with Gson: " + e.getMessage());
        }
    }

    public static void writeUserDataJackson(Path filePath, User user) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), user);
            System.out.println("(Jackson) JSON data written successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing JSON with Jackson: " + e.getMessage());
        }
    }
}
