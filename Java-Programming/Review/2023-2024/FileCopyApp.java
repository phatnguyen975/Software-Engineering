import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileCopyApp extends JFrame {
    private JTextField txtSource, txtDest;
    private JButton btnBrowseSource, btnBrowseDest, btnCopy;
    private JProgressBar progressBar;
    private JLabel lblStatus;

    public FileCopyApp() {
        setTitle("File Copy Utility");
        setSize(500, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 1, 10, 10));

        JPanel pnlSource = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSource = new JTextField(30);
        txtSource.setEditable(false);
        btnBrowseSource = new JButton("Chọn Nguồn...");
        pnlSource.add(new JLabel("Nguồn: "));
        pnlSource.add(txtSource);
        pnlSource.add(btnBrowseSource);

        JPanel pnlDest = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtDest = new JTextField(30);
        txtDest.setEditable(false);
        btnBrowseDest = new JButton("Chọn Đích...");
        pnlDest.add(new JLabel("Đích:   "));
        pnlDest.add(txtDest);
        pnlDest.add(btnBrowseDest);

        JPanel pnlAction = new JPanel();
        btnCopy = new JButton("Bắt đầu Copy");
        pnlAction.add(btnCopy);

        JPanel pnlProgress = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        lblStatus = new JLabel("Sẵn sàng");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
        pnlProgress.add(lblStatus, BorderLayout.NORTH);
        pnlProgress.add(progressBar, BorderLayout.CENTER);
        pnlProgress.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(pnlSource);
        add(pnlDest);
        add(pnlAction);
        add(pnlProgress);

        btnBrowseSource.addActionListener(e -> chooseFile(txtSource, JFileChooser.FILES_AND_DIRECTORIES));
        btnBrowseDest.addActionListener(e -> chooseFile(txtDest, JFileChooser.DIRECTORIES_ONLY));
        
        btnCopy.addActionListener(e -> {
            String srcPath = txtSource.getText();
            String destPath = txtDest.getText();
            if (srcPath.isEmpty() || destPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nguồn và đích!");
                return;
            }
            new CopyTask(new File(srcPath), new File(destPath)).execute();
        });
    }

    private void chooseFile(JTextField txt, int mode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(mode);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txt.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    class CopyTask extends SwingWorker<Void, Integer> {
        private File source;
        private File destDir;
        private long totalBytes = 0;
        private long copiedBytes = 0;

        public CopyTask(File source, File destDir) {
            this.source = source;
            this.destDir = destDir;
            btnCopy.setEnabled(false);
        }

        @Override
        protected Void doInBackground() throws Exception {
            lblStatus.setText("Đang tính toán kích thước...");
            totalBytes = calculateSize(source);
            
            lblStatus.setText("Đang copy...");
            File destFile = new File(destDir, source.getName());
            copyRecursive(source, destFile);
            return null;
        }

        private long calculateSize(File file) {
            if (file.isFile()) return file.length();
            long size = 0;
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) size += calculateSize(f);
            }
            return size;
        }

        private void copyRecursive(File src, File dest) throws IOException {
            if (src.isDirectory()) {
                if (!dest.exists()) dest.mkdirs();
                File[] files = src.listFiles();
                if (files != null) {
                    for (File f : files) {
                        copyRecursive(f, new File(dest, f.getName()));
                    }
                }
            } else {
                try (InputStream in = new FileInputStream(src);
                     OutputStream out = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                        copiedBytes += length;

                        int progress = (int) ((double) copiedBytes / totalBytes * 100);
                        publish(progress);
                    }
                }
            }
        }

        @Override
        protected void process(List<Integer> chunks) {
            int val = chunks.get(chunks.size() - 1);
            progressBar.setValue(val);
        }

        @Override
        protected void done() {
            btnCopy.setEnabled(true);
            lblStatus.setText("Hoàn tất copy!");
            progressBar.setValue(100);
            JOptionPane.showMessageDialog(FileCopyApp.this, "Copy thành công!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileCopyApp().setVisible(true));
    }
}
