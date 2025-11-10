package Section02;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Example01 {
    public static void main(String[] args) {
        // Create relative path
        Path relativePath = Paths.get("file.txt");
        System.out.println(relativePath);
        
        // Create absolute path
        Path absolutePath = Paths.get("C:\\Users\\Admin\\23127449-BTLT-W05-01\\YeuCau1\\src\\Section02\\file.txt");
        System.out.println(absolutePath);
    }
}
