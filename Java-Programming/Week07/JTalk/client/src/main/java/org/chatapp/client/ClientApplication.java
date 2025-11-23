package org.chatapp.client;

import org.chatapp.client.ui.main.HomeWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ClientApplication {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    HomeWindow homeWindow = new HomeWindow();
                    homeWindow.showWindow();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }
}
