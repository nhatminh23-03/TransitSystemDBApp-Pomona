package pomonatransit.gui;

import pomonatransit.gui.screens.ActualTripStopInfoPanel;
import pomonatransit.gui.screens.AddBusPanel;
import pomonatransit.gui.screens.AddDriverPanel;
import pomonatransit.gui.screens.DashboardPanel;
import pomonatransit.gui.screens.DeleteBusPanel;
import pomonatransit.gui.screens.DriverWeeklySchedulePanel;
import pomonatransit.gui.screens.TripOfferingPanel;
import pomonatransit.gui.screens.TripSchedulePanel;
import pomonatransit.gui.screens.TripStopsPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * TransitAppFrame is the main application window.
 * It builds the overall shell and registers all GUI screens.
 */
public class TransitAppFrame extends JFrame {

    private final HeaderPanel headerPanel;
    private final StatusBar statusBar;
    private final AppNavigator navigator;

    public TransitAppFrame() {
        super("Pomona Transit System");

        this.headerPanel = new HeaderPanel();
        this.statusBar = new StatusBar();
        this.navigator = new AppNavigator();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(headerPanel, BorderLayout.NORTH);
        add(new SidebarPanel(navigator, statusBar), BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        headerPanel.refreshConnectionStatus();
        navigator.showScreen(AppNavigator.DASHBOARD);
    }

    /**
     * Builds the center area and registers all screen panels with CardLayout.
     *
     * @return the center content panel
     */
    private JPanel buildContentArea() {
        navigator.registerScreen(AppNavigator.DASHBOARD, new DashboardPanel(navigator, statusBar));
        navigator.registerScreen(AppNavigator.TRIP_SCHEDULE, new TripSchedulePanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.TRIP_OFFERINGS, new TripOfferingPanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.TRIP_STOPS, new TripStopsPanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.DRIVER_WEEKLY_SCHEDULE, new DriverWeeklySchedulePanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.ADD_DRIVER, new AddDriverPanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.ADD_BUS, new AddBusPanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.DELETE_BUS, new DeleteBusPanel(statusBar, headerPanel));
        navigator.registerScreen(AppNavigator.ACTUAL_TRIP_STOP_INFO, new ActualTripStopInfoPanel(statusBar, headerPanel));

        return navigator.getContentPanel();
    }
}
