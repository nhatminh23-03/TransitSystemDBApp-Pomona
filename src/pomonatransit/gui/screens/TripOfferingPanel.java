package pomonatransit.gui.screens;

import pomonatransit.gui.HeaderPanel;
import pomonatransit.gui.StatusBar;
import pomonatransit.service.TripOfferingService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
 * TripOfferingPanel groups all TripOffering actions into tabs.
 * It follows a more table-driven workflow so the user can preview,
 * search, select, and then apply changes.
 */
public class TripOfferingPanel extends JPanel {

    private static final Object[] TRIP_OFFERING_COLUMNS = {
            "TripNumber", "Date", "ScheduledStartTime", "ScheduledArrivalTime", "DriverName", "BusID"
    };

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final TripOfferingService tripOfferingService;

    private final JTextField addTripNumberField;
    private final JTextField addDateField;
    private final JTextField addStartTimeField;
    private final JTextField addArrivalTimeField;
    private final JTextField addDriverField;
    private final JTextField addBusIdField;

    private final JTextField deleteTripNumberField;
    private final JTextField deleteDateField;
    private final JTextField deleteStartTimeField;

    private final JTextField changeDriverTripNumberField;
    private final JTextField changeDriverDateField;
    private final JTextField changeDriverStartTimeField;
    private final JTextField changeDriverNameField;

    private final JTextField changeBusTripNumberField;
    private final JTextField changeBusDateField;
    private final JTextField changeBusStartTimeField;
    private final JTextField changeBusIdField;

    private final DefaultTableModel addPreviewTableModel;
    private final DefaultTableModel deleteResultsTableModel;
    private final DefaultTableModel changeDriverResultsTableModel;
    private final DefaultTableModel changeBusResultsTableModel;

    private JTable deleteResultsTable;
    private JTable changeDriverResultsTable;
    private JTable changeBusResultsTable;

    public TripOfferingPanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.tripOfferingService = new TripOfferingService();

        addTripNumberField = new JTextField(15);
        addDateField = new JTextField(15);
        addStartTimeField = new JTextField(15);
        addArrivalTimeField = new JTextField(15);
        addDriverField = new JTextField(15);
        addBusIdField = new JTextField(15);

        deleteTripNumberField = new JTextField(15);
        deleteDateField = new JTextField(15);
        deleteStartTimeField = new JTextField(15);

        changeDriverTripNumberField = new JTextField(15);
        changeDriverDateField = new JTextField(15);
        changeDriverStartTimeField = new JTextField(15);
        changeDriverNameField = new JTextField(15);

        changeBusTripNumberField = new JTextField(15);
        changeBusDateField = new JTextField(15);
        changeBusStartTimeField = new JTextField(15);
        changeBusIdField = new JTextField(15);

        addPreviewTableModel = createTableModel();
        deleteResultsTableModel = createTableModel();
        changeDriverResultsTableModel = createTableModel();
        changeBusResultsTableModel = createTableModel();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Trip Offering", buildAddTab());
        tabbedPane.addTab("Delete Trip Offering", buildDeleteTab());
        tabbedPane.addTab("Change Driver", buildChangeDriverTab());
        tabbedPane.addTab("Change Bus", buildChangeBusTab());

        add(tabbedPane, BorderLayout.CENTER);

        loadAllTripOfferings();
    }

    private JPanel buildAddTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel formWrapper = createFormWrapper("Add a New Trip Offering");
        JPanel formPanel = createFormPanel();

        addFormRow(formPanel, 0, "TripNumber:", addTripNumberField);
        addFormRow(formPanel, 1, "Date (YYYY-MM-DD):", addDateField);
        addFormRow(formPanel, 2, "ScheduledStartTime (HH:MM:SS):", addStartTimeField);
        addFormRow(formPanel, 3, "ScheduledArrivalTime (HH:MM:SS):", addArrivalTimeField);
        addFormRow(formPanel, 4, "DriverName:", addDriverField);
        addFormRow(formPanel, 5, "BusID:", addBusIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Trip Offering");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh Table");

        addButton.addActionListener(event -> addTripOffering());
        clearButton.addActionListener(event -> clearFields(
                addTripNumberField, addDateField, addStartTimeField, addArrivalTimeField, addDriverField, addBusIdField
        ));
        refreshButton.addActionListener(event -> loadAllTripOfferings());

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);

        JTable previewTable = new JTable(addPreviewTableModel);
        previewTable.setAutoCreateRowSorter(true);

        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(createTablePanel("Trip Offering Preview", previewTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDeleteTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel formWrapper = createFormWrapper("Find a Trip Offering to Delete");
        JPanel formPanel = createFormPanel();

        addFormRow(formPanel, 0, "TripNumber:", deleteTripNumberField);
        addFormRow(formPanel, 1, "Date (YYYY-MM-DD):", deleteDateField);
        addFormRow(formPanel, 2, "ScheduledStartTime (HH:MM:SS):", deleteStartTimeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(event -> searchDeleteRows());
        deleteButton.addActionListener(event -> deleteSelectedTripOffering());
        clearButton.addActionListener(event -> {
            clearFields(deleteTripNumberField, deleteDateField, deleteStartTimeField);
            deleteResultsTableModel.setRowCount(0);
            statusBar.setMessage("Delete Trip Offering form cleared.");
        });

        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);

        deleteResultsTable = new JTable(deleteResultsTableModel);
        deleteResultsTable.setAutoCreateRowSorter(true);

        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(createTablePanel("Matching Trip Offerings", deleteResultsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChangeDriverTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel formWrapper = createFormWrapper("Find a Trip Offering to Change Driver");
        JPanel formPanel = createFormPanel();

        addFormRow(formPanel, 0, "TripNumber:", changeDriverTripNumberField);
        addFormRow(formPanel, 1, "Date (YYYY-MM-DD):", changeDriverDateField);
        addFormRow(formPanel, 2, "ScheduledStartTime (HH:MM:SS):", changeDriverStartTimeField);
        addFormRow(formPanel, 3, "New DriverName:", changeDriverNameField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton updateButton = new JButton("Change Driver for Selected");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(event -> searchChangeDriverRows());
        updateButton.addActionListener(event -> updateDriverForSelectedRow());
        clearButton.addActionListener(event -> {
            clearFields(changeDriverTripNumberField, changeDriverDateField, changeDriverStartTimeField, changeDriverNameField);
            changeDriverResultsTableModel.setRowCount(0);
            statusBar.setMessage("Change Driver form cleared.");
        });

        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);

        changeDriverResultsTable = new JTable(changeDriverResultsTableModel);
        changeDriverResultsTable.setAutoCreateRowSorter(true);

        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(createTablePanel("Matching Trip Offerings", changeDriverResultsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChangeBusTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel formWrapper = createFormWrapper("Find a Trip Offering to Change Bus");
        JPanel formPanel = createFormPanel();

        addFormRow(formPanel, 0, "TripNumber:", changeBusTripNumberField);
        addFormRow(formPanel, 1, "Date (YYYY-MM-DD):", changeBusDateField);
        addFormRow(formPanel, 2, "ScheduledStartTime (HH:MM:SS):", changeBusStartTimeField);
        addFormRow(formPanel, 3, "New BusID:", changeBusIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton updateButton = new JButton("Change Bus for Selected");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(event -> searchChangeBusRows());
        updateButton.addActionListener(event -> updateBusForSelectedRow());
        clearButton.addActionListener(event -> {
            clearFields(changeBusTripNumberField, changeBusDateField, changeBusStartTimeField, changeBusIdField);
            changeBusResultsTableModel.setRowCount(0);
            statusBar.setMessage("Change Bus form cleared.");
        });

        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);

        changeBusResultsTable = new JTable(changeBusResultsTableModel);
        changeBusResultsTable.setAutoCreateRowSorter(true);

        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(createTablePanel("Matching Trip Offerings", changeBusResultsTable), BorderLayout.CENTER);
        return panel;
    }

    private void addTripOffering() {
        if (hasEmpty(addTripNumberField, addDateField, addStartTimeField, addArrivalTimeField, addDriverField, addBusIdField)) {
            showWarning("Please fill in all Add Trip Offering fields.");
            return;
        }

        try {
            int tripNumber = Integer.parseInt(addTripNumberField.getText().trim());
            int busId = Integer.parseInt(addBusIdField.getText().trim());

            String message = tripOfferingService.addTripOffering(
                    tripNumber,
                    addDateField.getText().trim(),
                    addStartTimeField.getText().trim(),
                    addArrivalTimeField.getText().trim(),
                    addDriverField.getText().trim(),
                    busId
            );

            showServiceMessage(message);

            if (isSuccessMessage(message)) {
                loadAllTripOfferings();

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Do you want to add another trip offering?",
                        "Add Another Trip Offering",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    clearFields(addTripNumberField, addDateField, addStartTimeField, addArrivalTimeField, addDriverField, addBusIdField);
                    addTripNumberField.requestFocusInWindow();
                    statusBar.setMessage("Ready to add another trip offering.");
                }
            }
        } catch (NumberFormatException e) {
            showError("TripNumber and BusID must be whole numbers.");
        }
    }

    private void searchDeleteRows() {
        searchRowsByKey(deleteTripNumberField, deleteDateField, deleteStartTimeField, deleteResultsTableModel,
                deleteResultsTable, "delete");
    }

    private void searchChangeDriverRows() {
        searchRowsByKey(changeDriverTripNumberField, changeDriverDateField, changeDriverStartTimeField,
                changeDriverResultsTableModel, changeDriverResultsTable, "change driver");
    }

    private void searchChangeBusRows() {
        searchRowsByKey(changeBusTripNumberField, changeBusDateField, changeBusStartTimeField,
                changeBusResultsTableModel, changeBusResultsTable, "change bus");
    }

    private void searchRowsByKey(
            JTextField tripNumberField,
            JTextField dateField,
            JTextField startTimeField,
            DefaultTableModel model,
            JTable table,
            String actionName) {

        if (hasEmpty(tripNumberField, dateField, startTimeField)) {
            showWarning("Please fill in TripNumber, Date, and ScheduledStartTime first.");
            return;
        }

        try {
            int tripNumber = Integer.parseInt(tripNumberField.getText().trim());
            List<Object[]> rows = tripOfferingService.findTripOfferingsByKey(
                    tripNumber,
                    dateField.getText().trim(),
                    startTimeField.getText().trim()
            );

            loadRows(model, rows);
            headerPanel.refreshConnectionStatus();

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matching trip offering was found.", "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                statusBar.setMessage("No trip offering found for " + actionName + ".");
            } else {
                table.setRowSelectionInterval(0, 0);
                statusBar.setMessage("Loaded " + rows.size() + " matching trip offering row(s).");
            }
        } catch (NumberFormatException e) {
            showError("TripNumber must be a whole number.");
        } catch (IllegalArgumentException e) {
            showError("Invalid date or time format. Use YYYY-MM-DD and HH:MM:SS.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to search trip offerings.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Trip offering search failed.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private void deleteSelectedTripOffering() {
        int selectedRow = deleteResultsTable.getSelectedRow();

        if (selectedRow < 0) {
            showWarning("Please search and select a trip offering row first.");
            return;
        }

        int modelRow = deleteResultsTable.convertRowIndexToModel(selectedRow);
        int tripNumber = (Integer) deleteResultsTableModel.getValueAt(modelRow, 0);
        String date = String.valueOf(deleteResultsTableModel.getValueAt(modelRow, 1));
        String startTime = String.valueOf(deleteResultsTableModel.getValueAt(modelRow, 2));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected trip offering?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            statusBar.setMessage("Delete trip offering canceled.");
            return;
        }

        String message = tripOfferingService.deleteTripOffering(tripNumber, date, startTime);
        showServiceMessage(message);

        if (isSuccessMessage(message)) {
            searchDeleteRows();
            loadAllTripOfferings();
        }
    }

    private void updateDriverForSelectedRow() {
        int selectedRow = changeDriverResultsTable.getSelectedRow();

        if (selectedRow < 0) {
            showWarning("Please search and select a trip offering row first.");
            return;
        }

        if (changeDriverNameField.getText().trim().isEmpty()) {
            showWarning("Please enter the new driver name.");
            return;
        }

        int modelRow = changeDriverResultsTable.convertRowIndexToModel(selectedRow);
        int tripNumber = (Integer) changeDriverResultsTableModel.getValueAt(modelRow, 0);
        String date = String.valueOf(changeDriverResultsTableModel.getValueAt(modelRow, 1));
        String startTime = String.valueOf(changeDriverResultsTableModel.getValueAt(modelRow, 2));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Change the driver for the selected trip offering?",
                "Confirm Driver Update",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            statusBar.setMessage("Change driver canceled.");
            return;
        }

        String message = tripOfferingService.updateTripOfferingDriver(
                tripNumber,
                date,
                startTime,
                changeDriverNameField.getText().trim()
        );

        showServiceMessage(message);

        if (isSuccessMessage(message)) {
            searchChangeDriverRows();
            loadAllTripOfferings();
        }
    }

    private void updateBusForSelectedRow() {
        int selectedRow = changeBusResultsTable.getSelectedRow();

        if (selectedRow < 0) {
            showWarning("Please search and select a trip offering row first.");
            return;
        }

        if (changeBusIdField.getText().trim().isEmpty()) {
            showWarning("Please enter the new BusID.");
            return;
        }

        int modelRow = changeBusResultsTable.convertRowIndexToModel(selectedRow);
        int tripNumber = (Integer) changeBusResultsTableModel.getValueAt(modelRow, 0);
        String date = String.valueOf(changeBusResultsTableModel.getValueAt(modelRow, 1));
        String startTime = String.valueOf(changeBusResultsTableModel.getValueAt(modelRow, 2));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Change the bus for the selected trip offering?",
                "Confirm Bus Update",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            statusBar.setMessage("Change bus canceled.");
            return;
        }

        try {
            int newBusId = Integer.parseInt(changeBusIdField.getText().trim());

            String message = tripOfferingService.updateTripOfferingBus(
                    tripNumber,
                    date,
                    startTime,
                    newBusId
            );

            showServiceMessage(message);

            if (isSuccessMessage(message)) {
                searchChangeBusRows();
                loadAllTripOfferings();
            }
        } catch (NumberFormatException e) {
            showError("BusID must be a whole number.");
        }
    }

    private void loadAllTripOfferings() {
        try {
            List<Object[]> rows = tripOfferingService.findAllTripOfferings();
            loadRows(addPreviewTableModel, rows);
            statusBar.setMessage("Loaded " + rows.size() + " trip offering row(s).");
            headerPanel.refreshConnectionStatus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load trip offerings.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Trip offering preview failed to load.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private void loadRows(DefaultTableModel model, List<Object[]> rows) {
        model.setRowCount(0);

        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    private void showServiceMessage(String message) {
        headerPanel.refreshConnectionStatus();

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

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Missing Input", JOptionPane.WARNING_MESSAGE);
        statusBar.setMessage(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
        statusBar.setMessage(message);
    }

    private boolean hasEmpty(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private JPanel createFormWrapper(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private JPanel createFormPanel() {
        return new JPanel(new GridBagLayout());
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(TRIP_OFFERING_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
