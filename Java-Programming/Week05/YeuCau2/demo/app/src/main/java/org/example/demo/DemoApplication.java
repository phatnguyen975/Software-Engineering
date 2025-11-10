package org.example.demo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.example.demo.models.User;
import org.example.demo.utils.ReadJSON;
import org.example.demo.utils.WriteJSON;

public class DemoApplication {
    private static final Path FILE_PATH = Paths.get("src/main/resources/data.json");

    public static void main(String[] args) {
        User user1 = new User("Alice", 25, "alice@example.com");
        WriteJSON.writeUserDataGson(FILE_PATH, user1);
        ReadJSON.readUserDataGson(FILE_PATH);

        User user2 = new User("Bob", 30, "bob@example.com");
        WriteJSON.writeUserDataJackson(FILE_PATH, user2);
        ReadJSON.readUserDataJackson(FILE_PATH);
    }
}
