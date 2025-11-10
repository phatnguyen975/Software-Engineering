package Section03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Example04 {
    public static void main(String[] args) {
        Path source = Paths.get("data.txt");
        Path target = Paths.get("archive/backup.txt");

        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File moved and replaced if existed");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
