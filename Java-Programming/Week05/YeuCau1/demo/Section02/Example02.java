package Section02;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Example02 {
    public static void main(String[] args) {
        Path absolutePath = Paths.get("C:\\Users\\Admin\\23127449-BTLT-W05-01\\YeuCau1\\src\\Section02\\file.txt");
        System.out.println(absolutePath.isAbsolute());
        System.out.println(absolutePath.getFileName());
        System.out.println(absolutePath.getRoot());
        System.out.println(absolutePath.getParent());
        System.out.println(absolutePath.getNameCount());
        System.out.println(absolutePath.getName(0));
        System.out.println(absolutePath.subpath(0, 2));

        Path relativePath = Paths.get("file.txt");
        System.out.println(relativePath.toAbsolutePath());

        Path originalPath = Paths.get("C:\\Users\\Admin\\Coding\\..\\23127449-BTLT-W05-01");
        Path normalizedPath = originalPath.normalize();
        System.out.println(normalizedPath);

        Path basePath = Paths.get("/data");
        Path subPath = Paths.get("/data/subdata/subsubdata/file.txt");
        Path baseToSubPath = basePath.relativize(subPath);
        Path subToBasePath = subPath.relativize(basePath);
        System.out.println(baseToSubPath);
        System.out.println(subToBasePath);

        Path path = Paths.get("/data/images");
        Path resolvedPath = path.resolve("favicon.png");
        Path resolvedSiblingPath = path.resolveSibling("/docs/file.txt");
        System.out.println(resolvedPath);
        System.out.println(resolvedSiblingPath);
    }
}
