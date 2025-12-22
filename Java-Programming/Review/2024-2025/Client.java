import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField txtMssv, txtHoTen, txtNgaySinh, txtDiaChi, txtSoDienThoai;
    private JComboBox<String> cbGioiTinh;
    private JButton btnThem;

    public Client() {
        setTitle("Quản lý sinh viên - Client");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Mã số sinh viên:"));
        txtMssv = new JTextField();
        panel.add(txtMssv);

        panel.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        panel.add(txtHoTen);

        panel.add(new JLabel("Ngày sinh (dd/mm/yyyy):"));
        txtNgaySinh = new JTextField();
        panel.add(txtNgaySinh);

        panel.add(new JLabel("Giới tính:"));
        String[] genders = { "Nam", "Nữ", "Khác" };
        cbGioiTinh = new JComboBox<>(genders);
        panel.add(cbGioiTinh);

        panel.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        panel.add(txtDiaChi);

        panel.add(new JLabel("Số điện thoại:"));
        txtSoDienThoai = new JTextField();
        panel.add(txtSoDienThoai);

        btnThem = new JButton("Thêm Sinh Viên");
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnThem);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDataToServer();
            }
        });
    }

    private void sendDataToServer() {
        String mssv = txtMssv.getText().trim();
        String ten = txtHoTen.getText().trim();
        String ngaySinh = txtNgaySinh.getText().trim();
        String gioiTinh = (String) cbGioiTinh.getSelectedItem();
        String diaChi = txtDiaChi.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();

        if (mssv.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        Student sv = new Student(mssv, ten, ngaySinh, gioiTinh, diaChi, sdt);

        try (Socket socket = new Socket("localhost", 12345);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            oos.writeObject(sv);
            oos.flush();

            String response = dis.readUTF();
            JOptionPane.showMessageDialog(this, response);

            if (response.startsWith("SUCCESS")) {
                txtMssv.setText("");
                txtHoTen.setText("");
                txtNgaySinh.setText("");
                txtDiaChi.setText("");
                txtSoDienThoai.setText("");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối Server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client().setVisible(true));
    }
}
