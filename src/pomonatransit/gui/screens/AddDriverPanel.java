package pomonatransit.gui.screens;

import pomonatransit.gui.HeaderPanel;
import pomonatransit.gui.StatusBar;
import pomonatransit.service.DriverService;

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
 * AddDriverPanel inserts a new driver and shows a preview table
 * of the current Driver rows.
 */
public class AddDriverPanel extends JPanel {

    private final StatusBar statusBar;
    private final HeaderPanel headerPanel;
    private final DriverService driverService;

    private final JTextField driverNameField;
    private final JTextField telephoneField;
    private final DefaultTableModel tableModel;

    public AddDriverPanel(StatusBar statusBar, HeaderPanel headerPanel) {
        this.statusBar = statusBar;
        this.headerPanel = headerPanel;
        this.driverService = new DriverService();

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        driverNameField = new JTextField(18);
        telephoneField = new JTextField(18);

        tableModel = new DefaultTableModel(new Object[]{"DriverName", "DriverTelephoneNumber"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable driverTable = new JTable(tableModel);
        driverTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && driverTable.getSelectedRow() >= 0) {
                driverNameField.setText(String.valueOf(tableModel.getValueAt(driverTable.getSelectedRow(), 0)));
                telephoneField.setText(String.valueOf(tableModel.getValueAt(driverTable.getSelectedRow(), 1)));
            }
        });

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildPreviewPanel(driverTable), BorderLayout.CENTER);

        loadDriverTable();
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Add Driver"));

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
        formPanel.add(new JLabel("DriverTelephoneNumber:"), gbc);
        gbc.gridx = 1;
        formPanel.add(telephoneField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Driver");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh Table");

        addButton.addActionListener(event -> addDriver());
        clearButton.addActionListener(event -> clearForm());
        refreshButton.addActionListener(event -> loadDriverTable());

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildPreviewPanel(JTable driverTable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Driver Preview"));
        panel.add(new JScrollPane(driverTable), BorderLayout.CENTER);
        return panel;
    }

    private void addDriver() {
        String driverName = driverNameField.getText().trim();
        String phoneNumber = telephoneField.getText().trim();

        if (driverName.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all driver fields.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            statusBar.setMessage("Add Driver needs all fields.");
            return;
        }

        String message = driverService.addDriver(driverName, phoneNumber);
        headerPanel.refreshConnectionStatus();

        if (message.toLowerCase().contains("successfully")) {
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDriverTable();
        } else {
            JOptionPane.showMessageDialog(this, message, "Result", JOptionPane.WARNING_MESSAGE);
        }

        statusBar.setMessage(message);
    }

    private void loadDriverTable() {
        try {
            List<Object[]> rows = driverService.findAllDrivers();
            tableModel.setRowCount(0);

            for (Object[] row : rows) {
                tableModel.addRow(row);
            }

            statusBar.setMessage("Loaded " + rows.size() + " driver row(s).");
            headerPanel.refreshConnectionStatus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to load drivers.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setMessage("Driver preview failed to load.");
            headerPanel.refreshConnectionStatus();
        }
    }

    private void clearForm() {
        driverNameField.setText("");
        telephoneField.setText("");
        statusBar.setMessage("Add Driver form cleared.");
    }
}
