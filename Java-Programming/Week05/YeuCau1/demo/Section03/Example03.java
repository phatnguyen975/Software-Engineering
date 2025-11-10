package Section03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Example03 {
    public static void main(String[] args) {
        Path source = Paths.get("source.txt");
        Path target = Paths.get("backup.txt");

        try {
            Files.copy(source, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES);
            System.out.println("File copied with attributes preserved");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
