package org.chatapp.client.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ActiveUsersPanel extends JPanel {
    private JTable activeUserTable;
    private DefaultTableModel tableModel;
    private JPanel paginationPanel;

    private final int PAGE_SIZE = 27;
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalUsers = 0;

    private boolean loaded = false;

    public ActiveUsersPanel() {
        setLayout(new BorderLayout());
        initControlPanel();
        initActiveUserTable();
        initPagination();
    }

    private void initControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));

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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));

        JComboBox<String> filterBy = new JComboBox<>(new String[] { "Equal", "Less", "Greater" });
        filterBy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterBy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextField numberField = new JTextField(5);
        numberField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        numberField.setToolTipText("Filter number");

        JButton filterBtn = new JButton("Filter");
        filterBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setBackground(new Color(0, 130, 180));
        filterBtn.setFocusPainted(false);

        filterPanel.add(filterBy);
        filterPanel.add(numberField);
        filterPanel.add(filterBtn);

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

        controlPanel.add(searchPanel);
        controlPanel.add(filterPanel);
        controlPanel.add(sortPanel);
        controlPanel.add(refreshBtn);

        add(controlPanel, BorderLayout.NORTH);

        refreshBtn.addActionListener(e -> fetchActiveUserData(currentPage));

        filterBtn.addActionListener(e -> {
            int number = 0;
            if (numberField.getText() != "") {
                number = Integer.valueOf(numberField.getText());
            }
            String selectedOption = (String) filterBy.getSelectedItem();

            // TODO: Aggregate data with from and to value then LIMIT + OFFSET
            JOptionPane.showMessageDialog(this, "Filter " + selectedOption + " " + number);
        });
    }

    private void initActiveUserTable() {
        String[] columns = {
                "Username", "Full Name", "App Opens", "Chats with Friends", "Chats with Groups", "Created At"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        activeUserTable = new JTable(tableModel);
        activeUserTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeUserTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(activeUserTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initPagination() {
        paginationPanel = new JPanel(new BorderLayout());
        paginationPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private List<String[]> generateDummyFriendship() {
        List<String[]> list = new ArrayList<>();
        for (int i = 1; i <= 253; i++) {
            list.add(new String[] {
                    "user" + i,
                    "User " + i,
                    "1" + i,
                    "2" + ((i % 28) + 1),
                    "1" + ((i % 28) + 1),
                    "2025-10-" + ((i % 28) + 1),
            });
        }
        return list;
    }

    private void fetchActiveUserData(int page) {
        // TODO: Replace with real DB query with LIMIT + OFFSET
        // Example:
        // SELECT * FROM users ORDER BY username ASC LIMIT PAGE_SIZE OFFSET (page-1)*PAGE_SIZE

        List<String[]> allUsers = generateDummyFriendship();
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
        prevBtn.addActionListener(e -> fetchActiveUserData(currentPage - 1));
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
            pageBtn.addActionListener(e -> fetchActiveUserData(pageNumber));

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
        nextBtn.addActionListener(e -> fetchActiveUserData(currentPage + 1));
        pageControlPanel.add(nextBtn);

        JPanel totalUserPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel totalUserLabel = new JLabel("Total Active Users:");
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

    public void loadDataLazy() {
        if (loaded) {
            return;
        }

        currentPage = 1;
        fetchActiveUserData(currentPage);

        loaded = true;
    }
}
