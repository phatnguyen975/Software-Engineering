package org.chatapp.client.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.chatapp.client.ui.admin.users.AddUserDialog;
import org.chatapp.client.ui.admin.users.EditUserDialog;
import org.chatapp.client.ui.admin.users.FriendsListDialog;
import org.chatapp.client.ui.admin.users.LoginHistoryDialog;
import org.chatapp.client.ui.components.TimeFilter;

public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JPanel paginationPanel;
    private JPopupMenu popupMenu;

    private final int PAGE_SIZE = 27;
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalUsers = 0;

    private boolean loaded = false;

    public UserManagementPanel() {
        setLayout(new BorderLayout());
        initControlPanel();
        initUserTable();
        initPagination();
        fetchUserData(currentPage);
    }

    private void initControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setToolTipText("Search by name or username");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Filter Panel
        TimeFilter filterPanel = new TimeFilter();
        JButton filterBtn = filterPanel.getFilterButton();

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        statusPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> statusFilter = new JComboBox<>(new String[] { "All", "Active", "Locked" });
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        statusPanel.add(statusLabel);
        statusPanel.add(statusFilter);

        // Sort Panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        sortPanel.setOpaque(false);

        JLabel sortLabel = new JLabel("Sort By:");
        sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> sortBy = new JComboBox<>(new String[] { "Name", "Created Date" });
        sortBy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sortBy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sortPanel.add(sortLabel);
        sortPanel.add(sortBy);

        // Refresh Button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(0, 130, 180));
        refreshBtn.setFocusPainted(false);

        // Add User Button
        JButton addUserBtn = new JButton("Add");
        addUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addUserBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addUserBtn.setForeground(Color.WHITE);
        addUserBtn.setBackground(new Color(0, 130, 180));
        addUserBtn.setFocusPainted(false);

        controlPanel.add(searchPanel);
        controlPanel.add(filterPanel);
        controlPanel.add(statusPanel);
        controlPanel.add(sortPanel);
        controlPanel.add(addUserBtn);
        controlPanel.add(refreshBtn);

        add(controlPanel, BorderLayout.NORTH);

        refreshBtn.addActionListener(e -> fetchUserData(currentPage));
        addUserBtn.addActionListener(e -> {
            new AddUserDialog((JFrame) SwingUtilities.getWindowAncestor(this)).showDialog();
            fetchUserData(currentPage);
        });

        filterBtn.addActionListener(e -> {
            String dateFrom = filterPanel.getFromDate();
            String dateTo = filterPanel.getToDate();

            // TODO: Aggregate data with from and to value then LIMIT + OFFSET
            JOptionPane.showMessageDialog(this, "From " + dateFrom + " To " + dateTo);
        });
    }

    private void initUserTable() {
        String[] columns = {
                "Username", "Full Name", "Address", "Date of Birth", "Gender", "Email", "Status", "Created At"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
        });
    }

    private void initPagination() {
        paginationPanel = new JPanel(new BorderLayout());
        paginationPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private List<String[]> generateDummyUsers() {
        List<String[]> list = new ArrayList<>();
        for (int i = 1; i <= 253; i++) {
            list.add(new String[] {
                    "user" + i,
                    "User " + i,
                    "Address " + i,
                    "2005-01-" + ((i % 28) + 1),
                    i % 2 == 0 ? "Male" : "Female",
                    "user" + i + "@gmail.com",
                    i % 2 == 0 ? "Active" : "Locked",
                    "2018-01-" + ((i % 28) + 1),
            });
        }
        return list;
    }

    private void fetchUserData(int page) {
        // TODO: Replace with real DB query with LIMIT + OFFSET
        // Example:
        // SELECT * FROM users ORDER BY username ASC LIMIT PAGE_SIZE OFFSET (page-1)*PAGE_SIZE

        List<String[]> allUsers = generateDummyUsers();
        totalUsers = allUsers.size();
        totalPages = (int) Math.ceil(totalUsers * 1.0 / PAGE_SIZE);

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalUsers);

        List<String[]> pageData = allUsers.subList(start, end);

        currentPage = page;
        reloadUserTable(pageData);
        reloadPagination();
    }

    private void reloadUserTable(List<String[]> data) {
        tableModel.setRowCount(0);
        for (String[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void reloadPagination() {
        paginationPanel.removeAll();

        JPanel pageControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton prevBtn = new JButton("Prev");
        prevBtn.setEnabled(currentPage > 1);
        prevBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (currentPage > 1) {
            prevBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            prevBtn.setForeground(Color.WHITE);
            prevBtn.setBackground(new Color(0, 130, 180));
        }
        prevBtn.setFocusPainted(false);
        prevBtn.addActionListener(e -> fetchUserData(currentPage - 1));
        pageControlPanel.add(prevBtn);

        int maxBtns = 7;
        int start = Math.max(1, currentPage - 3);
        int end = Math.min(totalPages, start + maxBtns - 1);

        for (int i = start; i <= end; i++) {
            JButton pageBtn = new JButton(String.valueOf(i));
            pageBtn.setEnabled(i != currentPage);
            pageBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            pageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            int pageNumber = i;
            pageBtn.addActionListener(e -> fetchUserData(pageNumber));

            pageControlPanel.add(pageBtn);
        }

        JButton nextBtn = new JButton("Next");
        nextBtn.setEnabled(currentPage < totalPages);
        nextBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (currentPage < totalPages) {
            nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            nextBtn.setForeground(Color.WHITE);
            nextBtn.setBackground(new Color(0, 130, 180));
        }
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(e -> fetchUserData(currentPage + 1));
        pageControlPanel.add(nextBtn);

        JPanel totalUserPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel totalUserLabel = new JLabel("Total Users:");
        totalUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel totalUserValue = new JLabel(String.valueOf(totalUsers));
        totalUserValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalUserPanel.add(totalUserLabel);
        totalUserPanel.add(totalUserValue);

        JPanel totalPagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel totalPageLabel = new JLabel("Page:");
        totalPageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel totalPageValue = new JLabel(currentPage + " / " + totalPages);
        totalPageValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalPagePanel.add(totalPageLabel);
        totalPagePanel.add(totalPageValue);

        paginationPanel.add(totalUserPanel, BorderLayout.WEST);
        paginationPanel.add(pageControlPanel, BorderLayout.CENTER);
        paginationPanel.add(totalPagePanel, BorderLayout.EAST);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private void showPopupMenu(MouseEvent e) {
        int row = userTable.rowAtPoint(e.getPoint());
        if (row >= 0) {
            userTable.setRowSelectionInterval(row, row);
            String accountStatus = (String) userTable.getValueAt(row, 6);
            initPopupMenu(accountStatus);
            popupMenu.show(userTable, e.getX(), e.getY());
        }
    }

    private void initPopupMenu(String accountStatus) {
        popupMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Edit User");
        JMenuItem deleteItem = new JMenuItem("Delete User");
        JMenuItem viewLoginHistoryItem = new JMenuItem("View Login History");
        JMenuItem viewFriendsItem = new JMenuItem("View Friends");
        JMenuItem resetPasswordItem = new JMenuItem("Reset Password");
        JMenuItem lockItem = new JMenuItem("Lock");
        JMenuItem unlockItem = new JMenuItem("Unlock");

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
        popupMenu.add(viewLoginHistoryItem);
        popupMenu.add(viewFriendsItem);
        popupMenu.addSeparator();
        popupMenu.add(resetPasswordItem);
        popupMenu.add(accountStatus == "Active" ? lockItem : unlockItem);

        editItem.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            List<String> userInfo = new ArrayList<>();
            for (int i = 0; i <= 5; i++) {
                userInfo.add((String) userTable.getValueAt(row, i));
            }
            new EditUserDialog((JFrame) SwingUtilities.getWindowAncestor(this), userInfo).showDialog();
            fetchUserData(currentPage);
        });

        deleteItem.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            String username = (String) userTable.getValueAt(row, 0);

            int isConfirmed = JOptionPane.showConfirmDialog(null,
                    "Are you sure to delete " + username + " user?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (isConfirmed == JOptionPane.YES_OPTION) {
                // TODO: Delete user from database
                tableModel.removeRow(row);
            }

            fetchUserData(currentPage);
        });

        viewLoginHistoryItem.addActionListener(e -> {
            new LoginHistoryDialog((JFrame) SwingUtilities.getWindowAncestor(this)).showDialog();
        });

        viewFriendsItem.addActionListener(e -> {
            new FriendsListDialog((JFrame) SwingUtilities.getWindowAncestor(this)).showDialog();
        });

        resetPasswordItem.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            String username = (String) userTable.getValueAt(row, 0);
            // TODO: Reset password and send email to user
            JOptionPane.showMessageDialog(null, "Reset " + username + " password successfully!");
        });

        lockItem.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            String username = (String) userTable.getValueAt(row, 0);
            // TODO: Lock user
            JOptionPane.showMessageDialog(null, "Lock user: " + username);
        });

        unlockItem.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            String username = (String) userTable.getValueAt(row, 0);
            // TODO: UnLock user
            JOptionPane.showMessageDialog(null, "Unlock user: " + username);
        });
    }

    public void loadDataLazy() {
        if (loaded) {
            return;
        }

        currentPage = 1;
        fetchUserData(currentPage);

        loaded = true;
    }
}
