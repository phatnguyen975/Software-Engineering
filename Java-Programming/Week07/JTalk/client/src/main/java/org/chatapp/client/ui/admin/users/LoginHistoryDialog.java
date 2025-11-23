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

public class LoginHistoryDialog extends BaseDialog {
    private JTable loginHistoryTable;
    private DefaultTableModel tableModel;

    public LoginHistoryDialog(JFrame parent) {
        super(parent, "Login History", 0.25, 0.4);
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout());

        String[] columns = { "Date", "Time", "IP Address" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        loginHistoryTable = new JTable(tableModel);
        loginHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loginHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(loginHistoryTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        fetchLoginHistory();
    }

    private void fetchLoginHistory() {
        // TODO: Replace with real data from server
        List<String[]> loginHistory = generateDummyLoginHistory();

        tableModel.setRowCount(0);
        for (String[] row : loginHistory) {
            tableModel.addRow(row);
        }
    }

    private List<String[]> generateDummyLoginHistory() {
        List<String[]> list = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            list.add(new String[] {
                    "2018-01-" + ((i % 28) + 1),
                    ((i % 20) + 1) + ":10:" + i,
                    "192.168." + ((i % 28) + 1),
            });
        }
        return list;
    }
}
