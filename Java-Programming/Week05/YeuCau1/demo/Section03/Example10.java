package Section03;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example10 {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");

        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                System.out.println("File created: " + filePath.toAbsolutePath());
            } else {
                System.out.println("File already exists: " + filePath.toAbsolutePath());
            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists: " + filePath);
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }

        try {
            Path tempFile = Files.createTempFile("myTemp_", ".txt");
            System.out.println("Temporary file created: " + tempFile.toAbsolutePath());
            Files.writeString(tempFile, "This is a temporary file content!");
            System.out.println("Wrote data to temporary file.");
        } catch (IOException e) {
            System.out.println("Error creating temporary file: " + e.getMessage());
        }
    }
}
