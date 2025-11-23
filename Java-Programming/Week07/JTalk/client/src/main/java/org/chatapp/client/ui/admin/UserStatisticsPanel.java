package org.chatapp.client.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.toedter.calendar.JYearChooser;

public class UserStatisticsPanel extends JPanel {
    private JYearChooser yearChooser;
    private JButton viewRegistrationChartBtn;
    private JButton viewActiveChartBtn;

    private JPanel chartContainer;
    private JLabel placeholderLabel;

    public UserStatisticsPanel() {
        setLayout(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(Color.WHITE);

        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
        yearPanel.setOpaque(false);
        JLabel yearLabel = new JLabel("Select Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        yearChooser = new JYearChooser();
        yearChooser.setPreferredSize(new Dimension(55, 25));
        yearPanel.add(yearLabel);
        yearPanel.add(yearChooser);

        viewRegistrationChartBtn = new JButton("View New Registrations Chart");
        viewRegistrationChartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewRegistrationChartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewRegistrationChartBtn.setForeground(Color.WHITE);
        viewRegistrationChartBtn.setBackground(new Color(0, 130, 180));
        viewRegistrationChartBtn.setFocusPainted(false);

        viewActiveChartBtn = new JButton("View Active Users Chart");
        viewActiveChartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewActiveChartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewActiveChartBtn.setForeground(Color.WHITE);
        viewActiveChartBtn.setBackground(new Color(0, 130, 180));
        viewActiveChartBtn.setFocusPainted(false);

        controlPanel.add(yearPanel);
        controlPanel.add(viewRegistrationChartBtn);
        controlPanel.add(viewActiveChartBtn);

        add(controlPanel, BorderLayout.NORTH);

        // Chart Container
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        placeholderLabel = new JLabel("Select the year first", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        chartContainer.add(placeholderLabel, BorderLayout.CENTER);

        add(chartContainer, BorderLayout.CENTER);

        viewRegistrationChartBtn.addActionListener(e -> showRegistrationChart());
        viewActiveChartBtn.addActionListener(e -> showActiveUsersChart());
    }

    // New Registration Chart
    private void showRegistrationChart() {
        int selectedYear = yearChooser.getYear();

        // TODO: fetch real data from server using selectedYear
        // Map<Integer, Integer> month -> count
        Map<Integer, Integer> fakeData = mockRegistrationData(selectedYear);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int month = 1; month <= 12; month++) {
            dataset.addValue(fakeData.getOrDefault(month, 0), "Registrations", getMonthName(month));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "New Registrations in " + selectedYear,
                "Month",
                "Number of New Users",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        displayChart(chart);
    }

    // Active Users Chart
    private void showActiveUsersChart() {
        int selectedYear = yearChooser.getYear();

        // TODO: fetch real active-user data from server
        Map<Integer, Integer> fakeData = mockActiveUserData(selectedYear);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int month = 1; month <= 12; month++) {
            dataset.addValue(fakeData.getOrDefault(month, 0), "Active Users", getMonthName(month));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Active Users in " + selectedYear,
                "Month",
                "Number of Active Users",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        displayChart(chart);
    }

    private void displayChart(JFreeChart chart) {
        placeholderLabel.setVisible(false);

        chartContainer.removeAll();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private Map<Integer, Integer> mockRegistrationData(int year) {
        Map<Integer, Integer> map = new HashMap<>();
        Random r = new Random();

        for (int i = 1; i <= 12; i++) {
            map.put(i, r.nextInt(300)); // Fake 0-300
        }
        return map;
    }

    private Map<Integer, Integer> mockActiveUserData(int year) {
        Map<Integer, Integer> map = new HashMap<>();
        Random r = new Random();

        for (int i = 1; i <= 12; i++) {
            map.put(i, r.nextInt(500)); // Fake 0-500
        }
        return map;
    }

    private String getMonthName(int month) {
        return new java.text.DateFormatSymbols().getShortMonths()[month - 1];
    }
}
