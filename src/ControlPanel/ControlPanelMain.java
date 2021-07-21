package ControlPanel;

import javax.swing.*;

/**
 * This is the Main method that begins a session by running the 'Login' class
 */
public class ControlPanelMain {
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        new Login().setVisible(true);

    }
}
