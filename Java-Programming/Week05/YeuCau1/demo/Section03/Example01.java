package Section03;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example01 {
    public static void main(String[] args) {
        Path existFile = Paths.get("example.txt");
        Path notExistFile = Paths.get("missing.txt");
        System.out.println(Files.exists(existFile));
        System.out.println(Files.notExists(notExistFile));
        System.out.println(!Files.exists(existFile));
        System.out.println(!Files.notExists(notExistFile));

        boolean isRegularExecutableFile = Files.isRegularFile(existFile) &
                Files.isReadable(existFile) & Files.isExecutable(existFile);
        System.out.println(isRegularExecutableFile);
    }
}
