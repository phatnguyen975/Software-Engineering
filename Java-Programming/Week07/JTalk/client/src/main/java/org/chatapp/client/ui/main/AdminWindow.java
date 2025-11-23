package org.chatapp.client.ui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.chatapp.client.ui.admin.ActiveUsersPanel;
import org.chatapp.client.ui.admin.FriendshipOverviewPanel;
import org.chatapp.client.ui.admin.GroupManagementPanel;
import org.chatapp.client.ui.admin.LoginHistoryPanel;
import org.chatapp.client.ui.admin.SpamReportPanel;
import org.chatapp.client.ui.admin.UserManagementPanel;
import org.chatapp.client.ui.admin.UserStatisticsPanel;
import org.chatapp.client.ui.base.BaseFrame;

public class AdminWindow extends BaseFrame {
    public AdminWindow() {
        super("JTalk Group - Admin");
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setVerticalAlignment(SwingConstants.CENTER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(0, 130, 180));
        logoutBtn.setFocusPainted(false);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        UserManagementPanel userManagement = new UserManagementPanel();
        LoginHistoryPanel loginHistoryPanel = new LoginHistoryPanel();
        GroupManagementPanel groupManagementPanel = new GroupManagementPanel();
        SpamReportPanel spamReportPanel = new SpamReportPanel();
        UserStatisticsPanel userStatisticsPanel = new UserStatisticsPanel();
        FriendshipOverviewPanel friendshipOverviewPanel = new FriendshipOverviewPanel();
        ActiveUsersPanel activeUsersPanel = new ActiveUsersPanel();

        tabs.addTab("Users", userManagement);
        tabs.addTab("Login Tracking", loginHistoryPanel);
        tabs.addTab("Groups", groupManagementPanel);
        tabs.addTab("Reports", spamReportPanel);
        tabs.addTab("Statistics", userStatisticsPanel);
        tabs.addTab("Friendship", friendshipOverviewPanel);
        tabs.addTab("Active Users", activeUsersPanel);

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = tabs.getSelectedIndex();
                switch (index) {
                    case 0 -> userManagement.loadDataLazy();
                    case 1 -> loginHistoryPanel.loadDataLazy();
                    case 2 -> groupManagementPanel.loadDataLazy();
                    case 3 -> spamReportPanel.loadDataLazy();
                    case 5 -> friendshipOverviewPanel.loadDataLazy();
                    case 6 -> activeUsersPanel.loadDataLazy();
                }
            }
        });

        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(tabs, BorderLayout.CENTER);

        logoutBtn.addActionListener(e -> handleLogout());
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Send logout request to server
            // clientSocket.send("ADMIN_LOGOUT");
            this.dispose();
            new HomeWindow().showWindow();
        }
    }
}
