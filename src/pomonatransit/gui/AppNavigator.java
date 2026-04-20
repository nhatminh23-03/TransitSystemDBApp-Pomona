package pomonatransit.gui;

import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * AppNavigator keeps all screen navigation in one place.
 * It wraps a CardLayout so the frame and sidebar can switch
 * between different screens using simple screen IDs.
 */
public class AppNavigator {

    public static final String DASHBOARD = "dashboard";
    public static final String TRIP_SCHEDULE = "tripSchedule";
    public static final String TRIP_OFFERINGS = "tripOfferings";
    public static final String TRIP_STOPS = "tripStops";
    public static final String DRIVER_WEEKLY_SCHEDULE = "driverWeeklySchedule";
    public static final String ADD_DRIVER = "addDriver";
    public static final String ADD_BUS = "addBus";
    public static final String DELETE_BUS = "deleteBus";
    public static final String ACTUAL_TRIP_STOP_INFO = "actualTripStopInfo";

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public AppNavigator() {
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
    }

    /**
     * Adds a screen panel to the CardLayout container.
     *
     * @param screenId the unique screen name
     * @param screenPanel the panel to display for that screen
     */
    public void registerScreen(String screenId, JPanel screenPanel) {
        contentPanel.add(screenPanel, screenId);
    }

    /**
     * Shows a specific screen by its ID.
     *
     * @param screenId the screen to display
     */
    public void showScreen(String screenId) {
        cardLayout.show(contentPanel, screenId);
    }

    /**
     * Returns the shared content panel used by the frame center area.
     *
     * @return the panel managed by CardLayout
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }
}
