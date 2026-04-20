package pomonatransit.gui.screens;

import pomonatransit.gui.AppNavigator;
import pomonatransit.gui.StatusBar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * DashboardPanel is the home screen of the GUI.
 * It shows a welcome message and shortcut buttons for all features.
 */
public class DashboardPanel extends JPanel {

    private final AppNavigator navigator;
    private final StatusBar statusBar;

    public DashboardPanel(AppNavigator navigator, StatusBar statusBar) {
        this.navigator = navigator;
        this.statusBar = statusBar;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildWelcomePanel(), BorderLayout.NORTH);
        add(buildShortcutPanel(), BorderLayout.CENTER);
    }

    /**
     * Creates the top welcome section.
     *
     * @return the welcome panel
     */
    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Welcome to the Pomona Transit System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Use the shortcuts below or the sidebar to open each project feature.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(subtitleLabel);

        return panel;
    }

    /**
     * Creates a grid of shortcut buttons to all screens.
     *
     * @return the shortcuts panel
     */
    private JPanel buildShortcutPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 16, 16));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        panel.add(createShortcutButton("Trip Schedule", AppNavigator.TRIP_SCHEDULE, "Open Trip Schedule"));
        panel.add(createShortcutButton("Trip Offerings", AppNavigator.TRIP_OFFERINGS, "Open Trip Offerings"));
        panel.add(createShortcutButton("Trip Stops", AppNavigator.TRIP_STOPS, "Open Trip Stops"));
        panel.add(createShortcutButton("Driver Weekly Schedule", AppNavigator.DRIVER_WEEKLY_SCHEDULE, "Open Driver Weekly Schedule"));
        panel.add(createShortcutButton("Add Driver", AppNavigator.ADD_DRIVER, "Open Add Driver"));
        panel.add(createShortcutButton("Add Bus", AppNavigator.ADD_BUS, "Open Add Bus"));
        panel.add(createShortcutButton("Delete Bus", AppNavigator.DELETE_BUS, "Open Delete Bus"));
        panel.add(createShortcutButton("Actual Trip Stop Data", AppNavigator.ACTUAL_TRIP_STOP_INFO, "Open Actual Trip Stop Data"));

        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel infoLabel = new JLabel("");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoCard.add(infoLabel, BorderLayout.CENTER);

        panel.add(infoCard);

        return panel;
    }

    /**
     * Builds one shortcut button that navigates to a screen.
     *
     * @param text button label
     * @param screenId target screen ID
     * @param statusMessage status bar text
     * @return the button
     */
    private JButton createShortcutButton(String text, String screenId, String statusMessage) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.addActionListener(event -> {
            navigator.showScreen(screenId);
            statusBar.setMessage(statusMessage);
        });
        return button;
    }
}
