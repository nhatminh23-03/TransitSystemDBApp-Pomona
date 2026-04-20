package pomonatransit.gui.screens;

import pomonatransit.gui.HeaderPanel;
import pomonatransit.gui.StatusBar;
import pomonatransit.service.TripService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

/**
 * TripSchedulePanel lets the user search trip schedules and view results in a table.
 */
public class TripSchedulePanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final TripService tripService;

    private final JTextField startLocationField;
    private final JTextField destinationField;
    private final JTextField dateField;
    private final DefaultTableModel tableModel;

    public TripSchedulePanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.tripService = new TripService();

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        startLocationField = new JTextField(18);
        destinationField = new JTextField(18);
        dateField = new JTextField(12);

        tableModel = new DefaultTableModel(
                new Object[]{"TripNumber", "StartLocationName", "DestinationName", "ScheduledStartTime",
                        "ScheduledArrivalTime", "DriverName", "BusID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
    }

    /**
     * Creates the search form.
     *
     * @return the form panel
     */
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Trip Schedule Search"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("StartLocationName:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startLocationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("DestinationName:"), gbc);
        gbc.gridx = 1;
        formPanel.add(destinationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(event -> searchTripSchedules());
        clearButton.addActionListener(event -> clearForm());

        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    /**
     * Validates input, runs the search, and loads the results into the table.
     */
    private void searchTripSchedules() {
        String startLocation = startLocationField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = dateField.getText().trim();

        if (startLocation.isEmpty() || destination.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all search fields.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Trip schedule search needs all fields.");
            return;
        }

        try {
            List<Object[]> results = tripService.findTripSchedules(startLocation, destination, date);
            loadRows(results);
            headerPanel.refreshConnectionStatus();

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No trip schedules were found.", "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                statusBar.setMessage("Trip schedule search returned 0 rows.");
            } else {
                statusBar.setMessage("Loaded " + results.size() + " trip schedule row(s).");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Invalid Date",
                    JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Trip schedule search failed: invalid date.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load trip schedules.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Trip schedule search failed.");
            headerPanel.refreshConnectionStatus();
        }
    }

    /**
     * Replaces the table contents with new rows.
     *
     * @param rows rows returned from the service
     */
    private void loadRows(List<Object[]> rows) {
        tableModel.setRowCount(0);

        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    /**
     * Clears all form fields and result rows.
     */
    private void clearForm() {
        startLocationField.setText("");
        destinationField.setText("");
        dateField.setText("");
        tableModel.setRowCount(0);
        statusBar.setMessage("Trip schedule form cleared.");
    }
}
