package org.chatapp.client.ui.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.chatapp.client.utils.AppIcon;

public abstract class BaseFrame extends JFrame {
    protected JPanel contentPanel;

    public BaseFrame(String title) {
        setTitle(title);
        AppIcon.applyTo(this);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.68);
        int height = (int) (screenSize.height * 0.7);
        setSize(width, height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(235, 235, 235));
        
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        add(contentPanel);
    }

    protected abstract void setupContent();

    public void showWindow() {
        setupContent();
        setVisible(true);
    }
}
