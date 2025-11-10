package Section03;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Example09 {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        String content = "Hello from ByteChannel!\nJava NIO.2 provides efficient I/O handling.\n";

        try (SeekableByteChannel writeChannel = Files.newByteChannel(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(content.getBytes());
            buffer.flip();
            writeChannel.write(buffer);
            System.out.println("Data written successfully to " + path);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }

        try (SeekableByteChannel readChannel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            System.out.println("\nContent read from file:");
            while (readChannel.read(buffer) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.clear();
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
