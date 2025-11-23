package org.chatapp.client.ui.auth;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import org.chatapp.client.ui.main.UserWindow;

public class RegisterDialog extends BaseDialog {
    private InputField usernamePanel;
    private InputField emailPanel;
    private InputField passwordPanel;
    private InputField confirmPasswordPanel;
    private JCheckBox showPasswordCheckBox;

    public RegisterDialog(JFrame parent) {
        super(parent, "Register", 0.25, 0.55);
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        usernamePanel = new InputField("Enter your username", "text");
        emailPanel = new InputField("Enter your email", "text");
        passwordPanel = new InputField("Enter your password", "password");
        confirmPasswordPanel = new InputField("Confirm your password", "password");

        showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(confirmPasswordPanel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(showPasswordCheckBox);

        // Register Button
        FormButton registerBtn = new FormButton("Register");

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(registerBtn, BorderLayout.SOUTH);

        showPasswordCheckBox.addActionListener(e -> handleShowPassword());
        registerBtn.addActionListener(e -> handleRegister());
    }

    private void handleShowPassword() {
        boolean isShow = showPasswordCheckBox.isSelected();
        passwordPanel.togglePassword(isShow);
        confirmPasswordPanel.togglePassword(isShow);
    }

    private void handleRegister() {
        String username = usernamePanel.getContent();
        String email = emailPanel.getContent();
        String password = passwordPanel.getContent();
        String confirmPassword = confirmPasswordPanel.getContent();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Missing required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Confirm password is not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Add user information to database

        boolean registerSuccess = true;

        if (registerSuccess) {
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            getParent().setVisible(false);
            new UserWindow().showWindow();
        } else {
            JOptionPane.showMessageDialog(this, "Email has been existed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
