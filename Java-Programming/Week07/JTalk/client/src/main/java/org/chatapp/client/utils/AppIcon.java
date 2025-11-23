package org.chatapp.client.utils;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class AppIcon {
    private static final Image ICON = Toolkit.getDefaultToolkit()
            .getImage(AppIcon.class.getResource("/assets/logo.png"));

    public static void applyTo(JFrame frame) {
        frame.setIconImage(ICON);
    }

    public static void applyTo(JDialog dialog) {
        dialog.setIconImage(ICON);
    }

    public static Image getIcon() {
        return ICON;
    }
}
