package org.example.demo.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.demo.models.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class ReadJSON {
    public static void readUserDataGson(Path filePath) {
        Gson gson = new Gson();

        try (Reader reader = Files.newBufferedReader(filePath)) {
            User user = gson.fromJson(reader, User.class);
            System.out.println("(Gson) JSON data read successfully");
            printUser(user);
        } catch (IOException e) {
            System.err.println("Error reading JSON with Gson: " + e.getMessage());
        }
    }

    public static void readUserDataJackson(Path filePath) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            User user = mapper.readValue(filePath.toFile(), User.class);
            System.out.println("(Jackson) JSON data read successfully");
            printUser(user);
        } catch (IOException e) {
            System.err.println("Error reading JSON with Jackson: " + e.getMessage());
        }
    }

    private static void printUser(User user) {
        if (user != null) {
            System.out.println("Name:  " + user.getName());
            System.out.println("Age:   " + user.getAge());
            System.out.println("Email: " + user.getEmail());
        } else {
            System.out.println("No user data found");
        }
    }
}
