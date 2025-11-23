package org.chatapp.client.ui.admin.users;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.chatapp.client.ui.base.BaseDialog;

public class FriendsListDialog extends BaseDialog {
    private JTable friendTable;
    private DefaultTableModel tableModel;

    public FriendsListDialog(JFrame parent) {
        super(parent, "Friends List", 0.25, 0.4);
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout());

        String[] columns = { "Username", "Full Name", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        friendTable = new JTable(tableModel);
        friendTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(friendTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        fetchFriendsList();
    }

    private void fetchFriendsList() {
        // TODO: Replace with real data from server
        List<String[]> friendslist = generateDummyFriendsList();

        tableModel.setRowCount(0);
        for (String[] row : friendslist) {
            tableModel.addRow(row);
        }
    }

    private List<String[]> generateDummyFriendsList() {
        List<String[]> list = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            list.add(new String[] {
                    "user" + i,
                    "User " + i,
                    i % 2 == 0 ? "Pending" : "Accepted",
            });
        }
        return list;
    }
}
