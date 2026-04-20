package pomonatransit;

import pomonatransit.gui.TransitAppFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main launches the Swing desktop application.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // If Nimbus is not available, Swing uses the default look and feel.
        }

        SwingUtilities.invokeLater(() -> {
            TransitAppFrame frame = new TransitAppFrame();
            frame.setVisible(true);
        });
    }
}
