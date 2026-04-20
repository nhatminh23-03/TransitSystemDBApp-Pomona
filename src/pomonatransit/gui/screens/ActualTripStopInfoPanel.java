package pomonatransit.gui.screens;

import pomonatransit.gui.HeaderPanel;
import pomonatransit.gui.StatusBar;
import pomonatransit.service.ActualTripStopInfoService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
 * ActualTripStopInfoPanel records actual stop data for a trip offering.
 */
public class ActualTripStopInfoPanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final ActualTripStopInfoService actualTripStopInfoService;

    private final JTextField tripNumberField;
    private final JTextField dateField;
    private final JTextField scheduledStartTimeField;
    private final JTextField stopNumberField;
    private final JTextField scheduledArrivalTimeField;
    private final JTextField actualStartTimeField;
    private final JTextField actualArrivalTimeField;
    private final JTextField passengersInField;
    private final JTextField passengersOutField;
    private final DefaultTableModel tableModel;

    public ActualTripStopInfoPanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.actualTripStopInfoService = new ActualTripStopInfoService();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tripNumberField = new JTextField(15);
        dateField = new JTextField(15);
        scheduledStartTimeField = new JTextField(15);
        stopNumberField = new JTextField(15);
        scheduledArrivalTimeField = new JTextField(15);
        actualStartTimeField = new JTextField(15);
        actualArrivalTimeField = new JTextField(15);
        passengersInField = new JTextField(15);
        passengersOutField = new JTextField(15);
        tableModel = new DefaultTableModel(
                new Object[]{"TripNumber", "Date", "ScheduledStartTime", "StopNumber", "ScheduledArrivalTime",
                        "ActualStartTime", "ActualArrivalTime", "NumberOfPassengerIn", "NumberOfPassengerOut"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        add(buildMainPanel(), BorderLayout.NORTH);
        add(buildPreviewPanel(), BorderLayout.CENTER);

        loadPreviewTable();
    }

    private JPanel buildMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Actual Trip Stop Data"));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(buildTripOfferingSection());
        contentPanel.add(buildTimingSection());
        contentPanel.add(buildPassengerSection());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveButton = new JButton("Save Record");
        JButton clearButton = new JButton("Clear Form");

        saveButton.addActionListener(event -> saveRecord());
        clearButton.addActionListener(event -> clearForm());

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        wrapper.add(contentPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Actual Trip Stop Preview"));
        panel.add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTripOfferingSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Trip Offering Info"));

        addFormRow(panel, 0, "TripNumber:", tripNumberField);
        addFormRow(panel, 1, "Date (YYYY-MM-DD):", dateField);
        addFormRow(panel, 2, "ScheduledStartTime (HH:MM:SS):", scheduledStartTimeField);
        addFormRow(panel, 3, "StopNumber:", stopNumberField);

        return panel;
    }

    private JPanel buildTimingSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Timing Info"));

        addFormRow(panel, 0, "ScheduledArrivalTime (HH:MM:SS):", scheduledArrivalTimeField);
        addFormRow(panel, 1, "ActualStartTime (HH:MM:SS):", actualStartTimeField);
        addFormRow(panel, 2, "ActualArrivalTime (HH:MM:SS):", actualArrivalTimeField);

        return panel;
    }

    private JPanel buildPassengerSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Passenger Info"));

        addFormRow(panel, 0, "NumberOfPassengerIn:", passengersInField);
        addFormRow(panel, 1, "NumberOfPassengerOut:", passengersOutField);

        return panel;
    }

    private void saveRecord() {
        if (hasEmpty(tripNumberField, dateField, scheduledStartTimeField, stopNumberField,
                scheduledArrivalTimeField, actualStartTimeField, actualArrivalTimeField,
                passengersInField, passengersOutField)) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields before saving.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Actual trip stop data needs all fields.");
            return;
        }

        try {
            int tripNumber = Integer.parseInt(tripNumberField.getText().trim());
            int stopNumber = Integer.parseInt(stopNumberField.getText().trim());
            int passengersIn = Integer.parseInt(passengersInField.getText().trim());
            int passengersOut = Integer.parseInt(passengersOutField.getText().trim());

            String message = actualTripStopInfoService.recordActualTripStopData(
                    tripNumber,
                    dateField.getText().trim(),
                    scheduledStartTimeField.getText().trim(),
                    stopNumber,
                    scheduledArrivalTimeField.getText().trim(),
                    actualStartTimeField.getText().trim(),
                    actualArrivalTimeField.getText().trim(),
                    passengersIn,
                    passengersOut
            );

            headerPanel.refreshConnectionStatus();

            if (message.toLowerCase().contains("successfully")) {
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPreviewTable();
            } else {
                JOptionPane.showMessageDialog(this, message, "Result", JOptionPane.WARNING_MESSAGE);
            }

            statusBar.setMessage(message);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "TripNumber, StopNumber, NumberOfPassengerIn, and NumberOfPassengerOut must be whole numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Actual trip stop data failed: invalid number input.");
        }
    }

    private void clearForm() {
        tripNumberField.setText("");
        dateField.setText("");
        scheduledStartTimeField.setText("");
        stopNumberField.setText("");
        scheduledArrivalTimeField.setText("");
        actualStartTimeField.setText("");
        actualArrivalTimeField.setText("");
        passengersInField.setText("");
        passengersOutField.setText("");
        statusBar.setMessage("Actual trip stop data form cleared.");
    }

    private void loadPreviewTable() {
        try {
            List<Object[]> rows = actualTripStopInfoService.findAllActualTripStopData();
            tableModel.setRowCount(0);

            for (Object[] row : rows) {
                tableModel.addRow(row);
            }

            statusBar.setMessage("Loaded " + rows.size() + " actual trip stop row(s).");
            headerPanel.refreshConnectionStatus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load actual trip stop records.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Actual trip stop preview failed to load.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private boolean hasEmpty(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void addFormRow(JPanel panel, int row, String labelText, JTextField textField) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(textField, gbc);
    }
}
