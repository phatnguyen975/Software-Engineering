package org.chatapp.client.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

public class TimeFilter extends JPanel {
    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JButton filterBtn;

    public TimeFilter() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);

        // Date From
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        fromPanel.setOpaque(false);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        dateFrom = new JDateChooser();
        dateFrom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateFrom.setDateFormatString("yyyy-MM-dd");
        dateFrom.setPreferredSize(new Dimension(120, 25));

        fromPanel.add(fromLabel);
        fromPanel.add(dateFrom);

        // Date To
        JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        toPanel.setOpaque(false);

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        dateTo = new JDateChooser();
        dateTo.setDateFormatString("yyyy-MM-dd");
        dateTo.setPreferredSize(new Dimension(120, 25));
        dateTo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        toPanel.add(toLabel);
        toPanel.add(dateTo);

        // Filter Button
        filterBtn = new JButton("Filter");
        filterBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setBackground(new Color(0, 130, 180));
        filterBtn.setFocusPainted(false);

        add(fromPanel);
        add(toPanel);
        add(filterBtn);
    }

    public String getFromDate() {
        Date from = dateFrom.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return (from != null) ? sdf.format(from) : "";
    }

    public String getToDate() {
        Date to = dateTo.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return (to != null) ? sdf.format(to) : "";
    }

    public JButton getFilterButton() {
        return filterBtn;
    }
}
