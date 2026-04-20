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
 * DriverWeeklySchedulePanel displays trip offerings for one driver
 * during the 7-day period starting from the entered date.
 */
public class DriverWeeklySchedulePanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final TripService tripService;

    private final JTextField driverNameField;
    private final JTextField startDateField;
    private final DefaultTableModel tableModel;

    public DriverWeeklySchedulePanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.tripService = new TripService();

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        driverNameField = new JTextField(18);
        startDateField = new JTextField(12);

        tableModel = new DefaultTableModel(
                new Object[]{"TripNumber", "Date", "ScheduledStartTime", "ScheduledArrivalTime", "BusID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Driver Weekly Schedule"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("DriverName:"), gbc);
        gbc.gridx = 1;
        formPanel.add(driverNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(startDateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(event -> searchSchedule());
        clearButton.addActionListener(event -> clearForm());

        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void searchSchedule() {
        String driverName = driverNameField.getText().trim();
        String startDate = startDateField.getText().trim();

        if (driverName.isEmpty() || startDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in DriverName and Start Date.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Driver weekly schedule search needs all fields.");
            return;
        }

        try {
            List<Object[]> rows = tripService.findWeeklyDriverSchedule(driverName, startDate);
            loadRows(rows);
            headerPanel.refreshConnectionStatus();

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No trip offerings were found for that driver.", "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                statusBar.setMessage("Driver weekly schedule returned 0 rows.");
            } else {
                statusBar.setMessage("Loaded " + rows.size() + " weekly schedule row(s).");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Invalid Date",
                    JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Driver weekly schedule failed: invalid date.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load weekly schedule.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Driver weekly schedule failed.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private void loadRows(List<Object[]> rows) {
        tableModel.setRowCount(0);

        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void clearForm() {
        driverNameField.setText("");
        startDateField.setText("");
        tableModel.setRowCount(0);
        statusBar.setMessage("Driver weekly schedule form cleared.");
    }
}
