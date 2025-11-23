package org.chatapp.client.ui.admin.groups;

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

public class GroupAdminsDialog extends BaseDialog {
    private JTable adminTable;
    private DefaultTableModel tableModel;

    public GroupAdminsDialog(JFrame parent) {
        super(parent, "Admins List", 0.25, 0.4);
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout());

        String[] columns = { "User ID", "Username", "Role" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        adminTable = new JTable(tableModel);
        adminTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(adminTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        fetchAdminsList();
    }

    private void fetchAdminsList() {
        // TODO: Replace with real data from server
        List<String[]> adminsList = generateDummyAdminsList();

        tableModel.setRowCount(0);
        for (String[] row : adminsList) {
            tableModel.addRow(row);
        }
    }

    private List<String[]> generateDummyAdminsList() {
        List<String[]> list = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            list.add(new String[] {
                    "uuid" + i,
                    "User " + i,
                    "Admin",
            });
        }
        return list;
    }
}
