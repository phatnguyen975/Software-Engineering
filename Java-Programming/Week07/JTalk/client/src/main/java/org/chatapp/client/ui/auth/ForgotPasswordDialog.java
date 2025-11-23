package org.chatapp.client.ui.auth;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.chatapp.client.ui.base.BaseDialog;
import org.chatapp.client.ui.components.FormButton;
import org.chatapp.client.ui.components.InputField;

public class ForgotPasswordDialog extends BaseDialog {
    private InputField emailPanel;

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "Forgot Password", 0.25, 0.3);
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("Forgot Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        // Email Panel
        emailPanel = new InputField("Enter your email", "text");

        // Reset Button
        FormButton resetBtn = new FormButton("Reset Password");

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(emailPanel, BorderLayout.CENTER);
        contentPanel.add(resetBtn, BorderLayout.SOUTH);

        resetBtn.addActionListener(e -> handleResetPassword());
    }

    private void handleResetPassword() {
        String email = emailPanel.getContent();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Missing required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Send new password to user email

        JOptionPane.showMessageDialog(this, "New password sent to your email", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }
}
