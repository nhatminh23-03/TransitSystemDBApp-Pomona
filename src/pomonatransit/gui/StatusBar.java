package pomonatransit.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * StatusBar shows short messages at the bottom of the window.
 * Screens can update it after search, insert, update, or delete actions.
 */
public class StatusBar extends JPanel {

    private final JLabel messageLabel;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        messageLabel = new JLabel("Ready.");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        add(messageLabel, BorderLayout.WEST);
    }

    /**
     * Updates the text shown in the status bar.
     *
     * @param message the message to display
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
