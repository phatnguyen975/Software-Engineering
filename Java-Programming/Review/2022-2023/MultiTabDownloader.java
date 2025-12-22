import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class MultiTabDownloader extends JFrame {

    private JTabbedPane tabbedPane;
    private int tabCounter = 1; // Đếm số tab để đặt tên Tab 1, Tab 2...

    public MultiTabDownloader() {
        setTitle("Java Downloader - Final Exam Solution");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Khởi tạo TabbedPane
        tabbedPane = new JTabbedPane();
        
        // Thêm tab đầu tiên
        addNewTab();
        
        // Thêm tab dấu "+" (Tab đặc biệt để tạo mới)
        tabbedPane.addTab("+", new JPanel());

        // Bắt sự kiện click chuột vào tab "+"
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tabbedPane.getSelectedIndex();
                // Nếu click vào tab cuối cùng (tab dấu +)
                if (index == tabbedPane.getTabCount() - 1) {
                    addNewTab();
                }
            }
        });

        add(tabbedPane);
    }

    // Hàm thêm một tab download mới
    private void addNewTab() {
        String title = "Tab " + (tabCounter++);
        DownloadPanel panel = new DownloadPanel();
        
        // Chèn tab mới vào TRƯỚC tab dấu "+"
        int count = tabbedPane.getTabCount();
        int insertIndex = (count == 0) ? 0 : count - 1; // Nếu chưa có tab nào thì insert 0, nếu có dấu + thì insert trước nó
        
        // Nếu là lần đầu chạy (chưa có dấu +) thì add bình thường, logic này để xử lý khởi tạo
        if (count == 0) {
            tabbedPane.addTab(title, panel);
        } else {
            tabbedPane.insertTab(title, null, panel, null, insertIndex);
            tabbedPane.setSelectedIndex(insertIndex); // Chuyển ngay sang tab vừa tạo
        }
    }

    public static void main(String[] args) {
        // Chạy trên EDT
        SwingUtilities.invokeLater(() -> new MultiTabDownloader().setVisible(true));
    }

    // ==================================================================================
    // INNER CLASS: DownloadPanel - Chứa giao diện và logic của MỘT tab download
    // ==================================================================================
    class DownloadPanel extends JPanel {
        private JTextField txtLink;
        private JTextField txtSaveTo;
        private JProgressBar progressBar;
        private JButton btnStart, btnStop, btnBrowse;
        private DownloadWorker worker; // Mỗi tab có một worker riêng để chạy đa luồng

        public DownloadPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Row 1: Link
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Link:"), gbc);
            
            txtLink = new JTextField("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
            gbc.gridx = 1; gbc.weightx = 1.0;
            add(txtLink, gbc);

            // Row 2: Save to
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            add(new JLabel("Save to:"), gbc);
            
            txtSaveTo = new JTextField("D:\\Download"); // Đường dẫn mẫu
            gbc.gridx = 1; gbc.weightx = 1.0;
            add(txtSaveTo, gbc);
            
            btnBrowse = new JButton("...");
            btnBrowse.addActionListener(e -> chooseDirectory());
            gbc.gridx = 2; gbc.weightx = 0;
            add(btnBrowse, gbc);

            // Row 3: Progress
            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Progress:"), gbc);
            
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            gbc.gridx = 1; gbc.gridwidth = 2;
            add(progressBar, gbc);

            // Row 4: Buttons
            JPanel pnlBtn = new JPanel();
            btnStart = new JButton("Start");
            btnStop = new JButton("Stop");
            btnStop.setEnabled(false); // Ban đầu chưa chạy thì disable Stop
            
            pnlBtn.add(btnStart);
            pnlBtn.add(btnStop);
            
            gbc.gridx = 1; gbc.gridy = 3;
            add(pnlBtn, gbc);

            // --- EVENTS ---
            btnStart.addActionListener(e -> startDownload());
            btnStop.addActionListener(e -> stopDownload());
        }

        private void chooseDirectory() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtSaveTo.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void startDownload() {
            String url = txtLink.getText().trim();
            String dir = txtSaveTo.getText().trim();

            if (url.isEmpty() || dir.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Link và nơi lưu!");
                return;
            }

            // Reset UI
            progressBar.setValue(0);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

            // Khởi tạo và chạy Worker
            worker = new DownloadWorker(url, dir);
            worker.execute();
        }

        private void stopDownload() {
            if (worker != null && !worker.isDone()) {
                worker.cancel(true); // Gửi tín hiệu cancel
            }
        }

        // ==========================================================
        // INNER CLASS: DownloadWorker - Xử lý tải file dưới nền
        // ==========================================================
        class DownloadWorker extends SwingWorker<Void, Integer> {
            private String downloadUrl;
            private String saveDir;

            public DownloadWorker(String downloadUrl, String saveDir) {
                this.downloadUrl = downloadUrl;
                this.saveDir = saveDir;
            }

            @Override
            protected Void doInBackground() throws Exception {
                // Tách tên file từ URL
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                File saveFile = new File(saveDir, fileName);

                URL url = URI.create(downloadUrl).toURL();
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                int responseCode = httpConn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    long contentLength = httpConn.getContentLengthLong();
                    
                    try (InputStream inputStream = httpConn.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(saveFile)) {
                        
                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;
                        long totalBytesRead = 0;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            // 1. Kiểm tra xem người dùng có bấm Stop không
                            if (isCancelled()) {
                                outputStream.close();
                                inputStream.close();
                                // Xóa file đang tải dở (tùy chọn)
                                // saveFile.delete(); 
                                return null;
                            }

                            // 2. Ghi file
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;

                            // 3. Tính % và update giao diện
                            if (contentLength > 0) {
                                int percent = (int) (totalBytesRead * 100 / contentLength);
                                publish(percent); // Gửi data sang hàm process()
                            }
                        }
                    }
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }
                httpConn.disconnect();
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // Cập nhật Progress Bar (chạy trên luồng giao diện)
                int val = chunks.get(chunks.size() - 1);
                progressBar.setValue(val);
            }

            @Override
            protected void done() {
                // Khi tải xong hoặc bị Stop hoặc lỗi
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                
                if (isCancelled()) {
                    JOptionPane.showMessageDialog(DownloadPanel.this, "Đã dừng tải!");
                    progressBar.setValue(0);
                } else {
                    try {
                        get(); // Kiểm tra xem có exception không
                        JOptionPane.showMessageDialog(DownloadPanel.this, "Download hoàn tất!");
                        progressBar.setValue(100);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(DownloadPanel.this, "Lỗi: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
