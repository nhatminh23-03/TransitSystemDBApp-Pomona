package pomonatransit.gui;

import pomonatransit.db.DBConnection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * HeaderPanel displays the app title and current database connection status.
 */
public class HeaderPanel extends JPanel {

    private final JLabel connectionStatusLabel;

    public HeaderPanel() {
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLabel = new JLabel("Pomona Transit System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        connectionStatusLabel = new JLabel("Checking database...");
        connectionStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        add(titleLabel, BorderLayout.WEST);
        add(connectionStatusLabel, BorderLayout.EAST);
    }

    /**
     * Sets the connection label text and color.
     *
     * @param connected true if connected, false otherwise
     * @param text the text to show next to the status
     */
    public void setConnectionStatus(boolean connected, String text) {
        connectionStatusLabel.setText(text);
        connectionStatusLabel.setForeground(connected ? new Color(34, 139, 34) : new Color(178, 34, 34));
    }

    /**
     * Checks the database connection using DBConnection and updates the label.
     */
    public void refreshConnectionStatus() {
        if (DBConnection.isConnectionAvailable()) {
            setConnectionStatus(true, "Database: Connected");
        } else {
            setConnectionStatus(false, "Database: Disconnected");
        }
    }
}
