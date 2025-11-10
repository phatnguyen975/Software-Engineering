package Section03;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example02 {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");

        try {
            Files.delete(filePath);
            System.out.println(filePath + " deleted successfully");
        } catch (NoSuchFileException e) {
            System.err.println(filePath + " does not exist");
        } catch (DirectoryNotEmptyException e) {
            System.err.println("Directory is not empty: " + filePath);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                System.out.println(filePath + " was deleted");
            } else {
                System.out.println(filePath + " does not exist");
            }
        } catch (DirectoryNotEmptyException e) {
            System.err.println("Directory is not empty: " + filePath);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}
