package pomonatransit.gui.screens;

import pomonatransit.gui.HeaderPanel;
import pomonatransit.gui.StatusBar;
import pomonatransit.service.BusService;

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
 * AddBusPanel inserts a new bus and shows a preview table
 * of the current Bus rows.
 */
public class AddBusPanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final BusService busService;

    private final JTextField busIdField;
    private final JTextField modelField;
    private final JTextField yearField;
    private final DefaultTableModel tableModel;

    public AddBusPanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.busService = new BusService();

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        busIdField = new JTextField(18);
        modelField = new JTextField(18);
        yearField = new JTextField(18);

        tableModel = new DefaultTableModel(new Object[]{"BusID", "Model", "Year"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable busTable = new JTable(tableModel);
        busTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && busTable.getSelectedRow() >= 0) {
                busIdField.setText(String.valueOf(tableModel.getValueAt(busTable.getSelectedRow(), 0)));
                modelField.setText(String.valueOf(tableModel.getValueAt(busTable.getSelectedRow(), 1)));
                yearField.setText(String.valueOf(tableModel.getValueAt(busTable.getSelectedRow(), 2)));
            }
        });

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildPreviewPanel(busTable), BorderLayout.CENTER);

        loadBusTable();
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Add Bus"));

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

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1;
        formPanel.add(modelField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        formPanel.add(yearField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Bus");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh Table");

        addButton.addActionListener(event -> addBus());
        clearButton.addActionListener(event -> clearForm());
        refreshButton.addActionListener(event -> loadBusTable());

        buttonPanel.add(addButton);
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

    private void addBus() {
        String busIdText = busIdField.getText().trim();
        String model = modelField.getText().trim();
        String yearText = yearField.getText().trim();

        if (busIdText.isEmpty() || model.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all bus fields.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Add Bus needs all fields.");
            return;
        }

        try {
            int busId = Integer.parseInt(busIdText);
            int year = Integer.parseInt(yearText);

            String message = busService.addBus(busId, model, year);
            headerPanel.refreshConnectionStatus();

            if (message.toLowerCase().contains("successfully")) {
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBusTable();
            } else {
                JOptionPane.showMessageDialog(this, message, "Result", JOptionPane.WARNING_MESSAGE);
            }

            statusBar.setMessage(message);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "BusID and Year must be whole numbers.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Add Bus failed: invalid number input.");
        }
    }

    private void loadBusTable() {
        try {
            List<Object[]> rows = busService.findAllBuses();
            tableModel.setRowCount(0);

            for (Object[] row : rows) {
                tableModel.addRow(row);
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

    private void clearForm() {
        busIdField.setText("");
        modelField.setText("");
        yearField.setText("");
        statusBar.setMessage("Add Bus form cleared.");
    }
}
