import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final String FILE_NAME = "dssv.dat";

    private static Set<String> existingIds = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        loadExistingIds();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadExistingIds() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 0) {
                    existingIds.add(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean saveStudent(Student s) {
        if (existingIds.contains(s.getMssv())) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(s.toString());
            bw.newLine();
            existingIds.add(s.getMssv());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                Object obj = ois.readObject();
                if (obj instanceof Student) {
                    Student sv = (Student) obj;

                    boolean isSuccess = saveStudent(sv);

                    if (isSuccess) {
                        dos.writeUTF("SUCCESS: Thêm sinh viên thành công!");
                    } else {
                        dos.writeUTF("FAIL: Mã số sinh viên " + sv.getMssv() + " đã tồn tại!");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
