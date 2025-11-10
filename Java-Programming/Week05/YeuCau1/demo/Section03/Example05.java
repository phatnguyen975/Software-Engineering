package Section03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Example05 {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");

        try {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);

            System.out.println("File: " + filePath);
            System.out.println("Creation Time: " + attrs.creationTime());
            System.out.println("Last Modified Time: " + attrs.lastModifiedTime());
            System.out.println("Last Access Time: " + attrs.lastAccessTime());
            System.out.println("Is Directory: " + attrs.isDirectory());
            System.out.println("Is Regular File: " + attrs.isRegularFile());
            System.out.println("Is Symbolic Link: " + attrs.isSymbolicLink());
            System.out.println("Size: " + attrs.size() + " bytes");
        } catch (IOException e) {
            System.err.println("Error reading attributes: " + e.getMessage());
        }
    }
}
