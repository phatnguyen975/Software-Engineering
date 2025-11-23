package org.chatapp.client.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

public class FormButton extends JButton {
    public FormButton(String text) {
        this.setText(text);
        this.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setForeground(Color.WHITE);
        this.setBackground(new Color(0, 130, 180));
        this.setFocusPainted(false);
        this.setAlignmentX(CENTER_ALIGNMENT);
        this.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
    }
}
