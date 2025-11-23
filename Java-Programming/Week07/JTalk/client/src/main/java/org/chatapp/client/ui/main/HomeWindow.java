package org.chatapp.client.ui.main;

import java.awt.Font;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.chatapp.client.ui.base.BaseFrame;
import org.chatapp.client.ui.auth.LoginDialog;
import org.chatapp.client.ui.auth.RegisterDialog;

public class HomeWindow extends BaseFrame {
    public HomeWindow() {
        super("JTalk Group - Home");
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("JTalk Group", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Banner
        JLabel bannerLabel = new JLabel();
        bannerLabel.setAlignmentX(CENTER_ALIGNMENT);
        try {
            ImageIcon banner = new ImageIcon(getClass().getResource("/assets/banner.png"));
            Image scaledBanner = banner.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
            bannerLabel.setIcon(new ImageIcon(scaledBanner));
        } catch (Exception e) {
            bannerLabel.setText("[Banner Image]");
        }

        // Introduction
        JLabel introLabel = new JLabel(
            "<html>" +
                "<div style='text-align: center; width: 450px;'>" +
                    "Welcome to the JTalk - a modern communication platform where you can " +
                    "chat, connect, and share in real-time with your friends and groups." +
                "</div>" +
            "</html>",
            SwingConstants.CENTER
        );
        introLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        introLabel.setAlignmentX(CENTER_ALIGNMENT);

        mainPanel.add(bannerLabel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(introLabel);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setOpaque(false);

        // Get Started
        JLabel getStartedLabel = new JLabel("Get Started", SwingConstants.CENTER);
        getStartedLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        getStartedLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Login/Register Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginBtn.setBackground(new Color(0, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(140, 50));

        JButton registerBtn = new JButton("Register");
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerBtn.setBackground(new Color(0, 130, 180));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(140, 50));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        controlPanel.add(getStartedLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(buttonPanel);

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(controlPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> new LoginDialog(this).showDialog());
        registerBtn.addActionListener(e -> new RegisterDialog(this).showDialog());
    }
}
