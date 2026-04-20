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
 * TripStopsPanel displays the stop list for a given trip number.
 */
public class TripStopsPanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final TripService tripService;

    private final JTextField tripNumberField;
    private final DefaultTableModel tableModel;

    public TripStopsPanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.tripService = new TripService();

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tripNumberField = new JTextField(15);
        tableModel = new DefaultTableModel(
                new Object[]{"StopNumber", "StopAddress", "SequenceNumber", "DrivingTime"}, 0) {
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
        wrapper.setBorder(BorderFactory.createTitledBorder("Trip Stops"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("TripNumber:"), gbc);

        gbc.gridx = 1;
        formPanel.add(tripNumberField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadButton = new JButton("Load Stops");
        JButton clearButton = new JButton("Clear");

        loadButton.addActionListener(event -> loadStops());
        clearButton.addActionListener(event -> clearForm());

        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private void loadStops() {
        String tripNumberText = tripNumberField.getText().trim();

        if (tripNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a TripNumber.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Trip stops search needs a TripNumber.");
            return;
        }

        try {
            int tripNumber = Integer.parseInt(tripNumberText);
            List<Object[]> rows = tripService.findStopsForTrip(tripNumber);
            loadRows(rows);
            headerPanel.refreshConnectionStatus();

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No stops were found for that trip.", "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                statusBar.setMessage("Trip stops search returned 0 rows.");
            } else {
                statusBar.setMessage("Loaded " + rows.size() + " stop row(s).");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "TripNumber must be a whole number.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Trip stops search failed: invalid TripNumber.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load trip stops.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Trip stops search failed.");
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
        tripNumberField.setText("");
        tableModel.setRowCount(0);
        statusBar.setMessage("Trip stops form cleared.");
    }
}
