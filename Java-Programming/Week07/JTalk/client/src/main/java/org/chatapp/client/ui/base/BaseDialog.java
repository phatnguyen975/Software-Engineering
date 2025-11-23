package org.chatapp.client.ui.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.chatapp.client.utils.AppIcon;

public abstract class BaseDialog extends JDialog {
    protected JPanel contentPanel;

    public BaseDialog(JFrame parent, String title, double... dimensionRatio) {
        super(parent, title, true);
        AppIcon.applyTo(this);

        double widthRatio = dimensionRatio.length > 0 ? dimensionRatio[0] : 0.35;
        double heightRatio = dimensionRatio.length > 1 ? dimensionRatio[1] : 0.55;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * widthRatio);
        int height = (int) (screenSize.height * heightRatio);
        setSize(width, height);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        getContentPane().setBackground(new Color(235, 235, 235));

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        add(contentPanel);
    }

    protected abstract void setupContent();

    public void showDialog() {
        setupContent();
        setVisible(true);
    }
}
