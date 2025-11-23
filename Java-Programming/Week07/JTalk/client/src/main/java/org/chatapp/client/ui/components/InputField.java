package org.chatapp.client.ui.components;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class InputField extends JPanel {
    private JLabel inputLabel;
    private JComponent inputField;
    private String inputType;

    public InputField(String inputText, String inputType) {
        this.inputType = inputType;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        inputLabel = new JLabel(inputText);
        inputLabel.setAlignmentX(LEFT_ALIGNMENT);
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        inputField = inputType.equals("password") ? new JPasswordField() : new JTextField();
        inputField.setAlignmentX(LEFT_ALIGNMENT);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        this.add(inputLabel);
        this.add(Box.createVerticalStrut(2));
        this.add(inputField);
    }

    public String getContent() {
        if (this.inputType.equals("password")) {
            return new String(((JPasswordField) inputField).getPassword());
        } else {
            return ((JTextField) inputField).getText().trim();
        }
    }

    public void setContent(String text) {
        if (this.inputType.equals("password")) {
            ((JPasswordField) inputField).setText(text);
        } else {
            ((JTextField) inputField).setText(text);
        }
    }

    public void setEditable(boolean isEditable) {
        if (this.inputType.equals("password")) {
            ((JPasswordField) inputField).setEditable(isEditable);
        } else {
            ((JTextField) inputField).setEditable(isEditable);
        }
    }

    public void togglePassword(boolean isShow) {
        if (!inputType.equals("password")) {
            return;
        }

        JPasswordField passwordField = (JPasswordField) inputField;

        if (isShow) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('\u002A');
        }
    }
}
