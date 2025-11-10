package Section03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Example06 {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");

        List<String> linesToWrite = Arrays.asList(
                "Hello, Java NIO.2!",
                "This is an example of write(), readAllBytes(), and readAllLines().",
                "Enjoy learning!");

        try {
            Files.write(filePath, linesToWrite);
            System.out.println("Data written to " + filePath);

            byte[] allBytes = Files.readAllBytes(filePath);
            String fileContent = new String(allBytes);
            System.out.println("\nContent read using readAllBytes():");
            System.out.println(fileContent);

            List<String> readLines = Files.readAllLines(filePath);
            System.out.println("\nContent read using readAllLines():");
            for (String line : readLines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }
}
