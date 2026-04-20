package pomonatransit.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/**
 * SidebarPanel provides simple left-side navigation for all screens.
 */
public class SidebarPanel extends JPanel {

    private final AppNavigator navigator;
    private final StatusBar statusBar;

    public SidebarPanel(AppNavigator navigator, StatusBar statusBar) {
        this.navigator = navigator;
        this.statusBar = statusBar;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)
        ));

        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(navTitle);
        add(Box.createVerticalStrut(12));

        addNavigationButton("Dashboard", AppNavigator.DASHBOARD, "Viewing Dashboard");
        addNavigationButton("Trip Schedule", AppNavigator.TRIP_SCHEDULE, "Viewing Trip Schedule");
        addNavigationButton("Trip Offerings", AppNavigator.TRIP_OFFERINGS, "Viewing Trip Offerings");
        addNavigationButton("Trip Stops", AppNavigator.TRIP_STOPS, "Viewing Trip Stops");
        addNavigationButton("Driver Weekly Schedule", AppNavigator.DRIVER_WEEKLY_SCHEDULE, "Viewing Driver Weekly Schedule");
        addNavigationButton("Add Driver", AppNavigator.ADD_DRIVER, "Viewing Add Driver");
        addNavigationButton("Add Bus", AppNavigator.ADD_BUS, "Viewing Add Bus");
        addNavigationButton("Delete Bus", AppNavigator.DELETE_BUS, "Viewing Delete Bus");
        addNavigationButton("Actual Trip Stop Data", AppNavigator.ACTUAL_TRIP_STOP_INFO, "Viewing Actual Trip Stop Data");

        add(Box.createVerticalGlue());
    }

    /**
     * Creates one sidebar button and connects it to the CardLayout navigator.
     *
     * @param text button label
     * @param screenId screen ID from AppNavigator
     * @param statusMessage short status bar message
     */
    private void addNavigationButton(String text, String screenId, String statusMessage) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setFocusable(false);

        button.addActionListener(event -> {
            navigator.showScreen(screenId);
            statusBar.setMessage(statusMessage);
        });

        add(button);
        add(Box.createVerticalStrut(8));
    }
}
