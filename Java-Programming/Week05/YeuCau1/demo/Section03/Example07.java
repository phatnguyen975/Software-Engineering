package Section03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example07 {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("Hello, this is a BufferedWriter example!");
            writer.newLine();
            writer.write("We are using Java NIO.2 for file operations.");
            writer.newLine();
            writer.write("Buffered I/O is efficient and easy to use.");
            System.out.println("Data written successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            System.out.println("\nContent read from file:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
