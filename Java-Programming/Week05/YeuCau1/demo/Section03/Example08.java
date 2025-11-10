package Section03;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example08 {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");

        try (OutputStream out = Files.newOutputStream(filePath)) {
            String content = "Hello, this is written using newOutputStream!\n"
                    + "Java NIO.2 makes stream I/O simple.\n"
                    + "Enjoy learning!";
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            out.write(data);
            System.out.println("Data written successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }

        try (InputStream in = Files.newInputStream(filePath)) {
            System.out.println("\nContent read from file:");
            int byteData;
            while ((byteData = in.read()) != -1) {
                System.out.print((char) byteData);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
