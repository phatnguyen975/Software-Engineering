package org.chatapp.client.ui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.chatapp.client.ui.base.BaseDialog;
import org.chatapp.client.ui.components.FormButton;
import org.chatapp.client.ui.components.InputField;
import org.chatapp.client.ui.main.AdminWindow;
import org.chatapp.client.ui.main.UserWindow;

public class LoginDialog extends BaseDialog {
    private InputField emailPanel;
    private InputField passwordPanel;
    private JCheckBox showPasswordCheckBox;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", 0.25, 0.4);
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setOpaque(false);

        // Input Field
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        emailPanel = new InputField("Enter your email", "text");
        passwordPanel = new InputField("Enter your password", "password");

        inputPanel.add(emailPanel);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(passwordPanel);

        // Show/Forgot Password
        JPanel showForgotPanel = new JPanel(new BorderLayout());
        showForgotPanel.setOpaque(false);

        showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton forgotBtn = new JButton("Forgot password?");
        forgotBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.setForeground(new Color(0, 102, 204));
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorder(new EmptyBorder(0, 0, 0, 0));

        showForgotPanel.add(showPasswordCheckBox, BorderLayout.WEST);
        showForgotPanel.add(forgotBtn, BorderLayout.EAST);

        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(showForgotPanel, BorderLayout.SOUTH);

        // Login Button
        FormButton loginBtn = new FormButton("Login");

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(loginBtn, BorderLayout.SOUTH);

        showPasswordCheckBox.addActionListener(e -> handleShowPassword());
        forgotBtn.addActionListener(e -> handleForgotPassword());
        loginBtn.addActionListener(e -> handleLogin());
    }

    private void handleShowPassword() {
        boolean isShow = showPasswordCheckBox.isSelected();
        passwordPanel.togglePassword(isShow);
    }

    private void handleForgotPassword() {
        new ForgotPasswordDialog((JFrame) getParent()).showDialog();
    }

    private void handleLogin() {
        String email = emailPanel.getContent();
        String password = passwordPanel.getContent();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Missing required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Validate user information

        boolean loginSuccess = true;
        String role = "admin";

        if (loginSuccess) {
            this.dispose();
            getParent().setVisible(false);

            if (role == "admin") {
                new AdminWindow().showWindow();
            } else if (role == "user") {
                new UserWindow().showWindow();
            } else {
                System.err.println("Invalid role");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username or password is wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
