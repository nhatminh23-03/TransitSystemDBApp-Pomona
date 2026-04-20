package pomonatransit.service;

import pomonatransit.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains database operations for drivers.
 * For feature 5, it adds a new driver after checking for duplicates.
 */
public class DriverService {

    /**
     * Returns all drivers so the GUI can show a live preview table.
     *
     * @return table rows for the driver management screen
     * @throws SQLException if the query fails
     */
    public List<Object[]> findAllDrivers() throws SQLException {
        String sql = """
                SELECT DriverName, DriverTelephoneNumber
                FROM Driver
                ORDER BY DriverName
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                rows.add(new Object[]{
                        resultSet.getString("DriverName"),
                        resultSet.getString("DriverTelephoneNumber")
                });
            }
        }

        return rows;
    }

    /**
     * Adds a driver if the driver name does not already exist.
     *
     * @param driverName the name of the driver
     * @param driverTelephoneNumber the driver's phone number
     */
    public String addDriver(String driverName, String driverTelephoneNumber) {
        String checkSql = "SELECT 1 FROM Driver WHERE DriverName = ?";
        String insertSql = """
                INSERT INTO Driver (DriverName, DriverTelephoneNumber)
                VALUES (?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            // First, check whether a driver with the same name already exists.
            // This gives a clear message before trying the INSERT.
            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                checkStatement.setString(1, driverName);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return "A driver with that name already exists.";
                    }
                }
            }

            // If the driver does not exist yet, insert the new row.
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setString(1, driverName);
                insertStatement.setString(2, driverTelephoneNumber);

                int rowsInserted = insertStatement.executeUpdate();

                if (rowsInserted > 0) {
                    return "Driver added successfully.";
                } else {
                    return "Driver was not added.";
                }
            }
        } catch (SQLException e) {
            return "Error: unable to add the driver. Details: " + e.getMessage();
        }
    }
}
