package pomonatransit.gui.screens;

import pomonatransit.gui.HeaderPanel;
import pomonatransit.gui.StatusBar;
import pomonatransit.service.BusService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

/**
 * DeleteBusPanel deletes a bus by BusID.
 * It also handles the edge case where the bus is already assigned
 * to trip offerings by showing a replacement-bus modal workflow.
 */
public class DeleteBusPanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final BusService busService;

    private final JTextField busIdField;
    private final DefaultTableModel busTableModel;

    public DeleteBusPanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.busService = new BusService();

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        busIdField = new JTextField(18);
        busTableModel = new DefaultTableModel(new Object[]{"BusID", "Model", "Year"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable busTable = new JTable(busTableModel);
        busTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && busTable.getSelectedRow() >= 0) {
                busIdField.setText(String.valueOf(busTableModel.getValueAt(busTable.getSelectedRow(), 0)));
            }
        });

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildPreviewPanel(busTable), BorderLayout.CENTER);

        loadBusTable();
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Delete Bus"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("BusID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(busIdField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteButton = new JButton("Delete Bus");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh Table");

        deleteButton.addActionListener(event -> deleteBus());
        clearButton.addActionListener(event -> clearForm());
        refreshButton.addActionListener(event -> loadBusTable());

        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildPreviewPanel(JTable busTable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bus Preview"));
        panel.add(new JScrollPane(busTable), BorderLayout.CENTER);
        return panel;
    }

    private void deleteBus() {
        String busIdText = busIdField.getText().trim();

        if (busIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a BusID.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Delete Bus needs a BusID.");
            return;
        }

        try {
            int busId = Integer.parseInt(busIdText);
            List<Object[]> affectedTrips = busService.findTripOfferingsUsingBus(busId);

            if (affectedTrips.isEmpty()) {
                confirmSimpleDelete(busId);
            } else {
                showReplacementBusModal(busId, affectedTrips);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "BusID must be a whole number.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Delete Bus failed: invalid BusID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to check bus usage.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Delete Bus failed while checking related trips.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private void confirmSimpleDelete(int busId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this bus?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            statusBar.setMessage("Delete bus canceled.");
            return;
        }

        String message = busService.deleteBus(busId);
        headerPanel.refreshConnectionStatus();
        showResultMessage(message);

        if (isSuccessMessage(message)) {
            loadBusTable();
        }
    }

    /**
     * Shows a modal-style dialog when the selected bus is already used in trips.
     * The user can review the affected trips and choose a replacement bus.
     */
    private void showReplacementBusModal(int oldBusId, List<Object[]> affectedTrips) throws SQLException {
        List<Object[]> replacementBuses = busService.findReplacementBuses(oldBusId);

        if (replacementBuses.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "This bus is used by trips, but there are no replacement buses available.",
                    "Cannot Delete Bus",
                    JOptionPane.WARNING_MESSAGE
            );
            statusBar.setMessage("Delete Bus blocked because no replacement bus is available.");
            return;
        }

        JPanel modalPanel = new JPanel();
        modalPanel.setLayout(new BoxLayout(modalPanel, BoxLayout.Y_AXIS));

        JLabel explanationLabel = new JLabel(
                "<html>This bus is already assigned to trip offerings.<br/>Choose a replacement bus to reassign those trips before deleting it.</html>"
        );
        explanationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        DefaultTableModel affectedTableModel = new DefaultTableModel(
                new Object[]{"TripNumber", "Date", "ScheduledStartTime", "ScheduledArrivalTime", "DriverName", "BusID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Object[] row : affectedTrips) {
            affectedTableModel.addRow(row);
        }

        JTable affectedTable = new JTable(affectedTableModel);
        JScrollPane affectedScrollPane = new JScrollPane(affectedTable);
        affectedScrollPane.setBorder(BorderFactory.createTitledBorder("Affected Trip Offerings"));
        affectedScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<BusChoice> replacementComboBox = new JComboBox<>();
        for (Object[] row : replacementBuses) {
            replacementComboBox.addItem(new BusChoice(
                    (Integer) row[0],
                    String.valueOf(row[1]),
                    (Integer) row[2]
            ));
        }

        JPanel replacementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        replacementPanel.setBorder(BorderFactory.createTitledBorder("Replacement Bus"));
        replacementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        replacementPanel.add(new JLabel("Replacement Bus:"));
        replacementPanel.add(replacementComboBox);

        modalPanel.add(explanationLabel);
        modalPanel.add(affectedScrollPane);
        modalPanel.add(replacementPanel);

        int option = JOptionPane.showConfirmDialog(
                this,
                modalPanel,
                "Bus Is Used In Existing Trips",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            statusBar.setMessage("Delete bus canceled.");
            return;
        }

        BusChoice selectedReplacement = (BusChoice) replacementComboBox.getSelectedItem();

        if (selectedReplacement == null) {
            statusBar.setMessage("Delete Bus canceled because no replacement bus was selected.");
            return;
        }

        String message = busService.replaceBusInTripOfferingsAndDeleteOldBus(oldBusId, selectedReplacement.busId);
        headerPanel.refreshConnectionStatus();
        showResultMessage(message);

        if (isSuccessMessage(message)) {
            loadBusTable();
        }
    }

    private void loadBusTable() {
        try {
            List<Object[]> rows = busService.findAllBuses();
            busTableModel.setRowCount(0);

            for (Object[] row : rows) {
                busTableModel.addRow(row);
            }

            statusBar.setMessage("Loaded " + rows.size() + " bus row(s).");
            headerPanel.refreshConnectionStatus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load buses.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Bus preview failed to load.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private void showResultMessage(String message) {
        if (isSuccessMessage(message)) {
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, message, "Result", JOptionPane.WARNING_MESSAGE);
        }

        statusBar.setMessage(message);
    }

    private boolean isSuccessMessage(String message) {
        return message != null && message.toLowerCase().contains("successfully");
    }

    private void clearForm() {
        busIdField.setText("");
        statusBar.setMessage("Delete Bus form cleared.");
    }

    /**
     * Simple display object used by the replacement bus combo box.
     */
    private static class BusChoice {
        private final int busId;
        private final String model;
        private final int year;

        private BusChoice(int busId, String model, int year) {
            this.busId = busId;
            this.model = model;
            this.year = year;
        }

        @Override
        public String toString() {
            return busId + " - " + model + " (" + year + ")";
        }
    }
}
