package org.chatapp.client.ui.admin.reports;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.chatapp.client.ui.base.BaseDialog;

public class ReviewReportDialog extends BaseDialog {
    private JLabel reporterValue;
    private JLabel reportedValue;
    private JLabel timeValue;
    private JLabel reasonValue;
    private JTextArea messageValue;
    private JLabel statusValue;

    private String reportId;

    public ReviewReportDialog(JFrame parent, String reportId) {
        super(parent, "Review Report", 0.25, 0.38);
        this.reportId = reportId;
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Reporter
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel reporterLabel = new JLabel("Reporter:");
        reporterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainPanel.add(reporterLabel, gbc);
        reporterValue = new JLabel("user1");
        gbc.gridx = 1;
        mainPanel.add(reporterValue, gbc);

        // Reported
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel reportedLabel = new JLabel("Reported:");
        reportedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainPanel.add(reportedLabel, gbc);
        reportedValue = new JLabel("user2");
        gbc.gridx = 1;
        mainPanel.add(reportedValue, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainPanel.add(timeLabel, gbc);
        timeValue = new JLabel("2025-01-02 10:34");
        gbc.gridx = 1;
        mainPanel.add(timeValue, gbc);

        // Reason
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainPanel.add(reasonLabel, gbc);
        reasonValue = new JLabel("Spam content");
        gbc.gridx = 1;
        mainPanel.add(reasonValue, gbc);

        // Message
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainPanel.add(messageLabel, gbc);

        messageValue = new JTextArea("This is the spam message");
        messageValue.setLineWrap(true);
        messageValue.setWrapStyleWord(true);
        messageValue.setEditable(false);

        JScrollPane scroll = new JScrollPane(messageValue);
        scroll.setPreferredSize(new Dimension(250, 100));

        gbc.gridx = 1;
        mainPanel.add(scroll, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainPanel.add(statusLabel, gbc);
        statusValue = new JLabel("Pending");
        gbc.gridx = 1;
        mainPanel.add(statusValue, gbc);

        // Enforce/Dismiss Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton enforceBtn = new JButton("Enforce");
        enforceBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        enforceBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enforceBtn.setForeground(Color.WHITE);
        enforceBtn.setBackground(new Color(0, 130, 180));
        enforceBtn.setFocusPainted(false);

        JButton dismissBtn = new JButton("Dismiss");
        dismissBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dismissBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dismissBtn.setForeground(Color.WHITE);
        dismissBtn.setBackground(new Color(0, 130, 180));
        dismissBtn.setFocusPainted(false);

        buttonPanel.add(enforceBtn);
        buttonPanel.add(dismissBtn);

        enforceBtn.addActionListener(e -> {
            // TODO: send enforce action to server
            JOptionPane.showMessageDialog(this, "Enforced successfully!");
            this.dispose();
        });

        dismissBtn.addActionListener(e -> {
            // TODO: send dismiss action to server
            JOptionPane.showMessageDialog(this, "Dismissed.");
            this.dispose();
        });

        // ----- Add to main layout -----
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fetchReportInfo() {
        // TODO: Fetch report information based on reportId
    }
}
