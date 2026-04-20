package pomonatransit.service;

import pomonatransit.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains database operations for buses.
 * It supports adding a new bus and deleting an existing bus.
 */
public class BusService {

    /**
     * Returns all buses for table display in the GUI.
     *
     * @return table rows for bus screens
     * @throws SQLException if the query fails
     */
    public List<Object[]> findAllBuses() throws SQLException {
        String sql = """
                SELECT BusID, Model, Year
                FROM Bus
                ORDER BY BusID
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                rows.add(new Object[]{
                        resultSet.getInt("BusID"),
                        resultSet.getString("Model"),
                        resultSet.getInt("Year")
                });
            }
        }

        return rows;
    }

    /**
     * Returns all buses except the one being deleted.
     * This is used in the replacement-bus modal dialog.
     *
     * @param excludedBusId the bus that should not appear in the replacement list
     * @return replacement bus rows
     * @throws SQLException if the query fails
     */
    public List<Object[]> findReplacementBuses(int excludedBusId) throws SQLException {
        String sql = """
                SELECT BusID, Model, Year
                FROM Bus
                WHERE BusID <> ?
                ORDER BY BusID
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, excludedBusId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new Object[]{
                            resultSet.getInt("BusID"),
                            resultSet.getString("Model"),
                            resultSet.getInt("Year")
                    });
                }
            }
        }

        return rows;
    }

    /**
     * Returns trip offerings that are currently assigned to a bus.
     * The GUI uses this to preview what will be affected before deletion.
     *
     * @param busId the bus to search for
     * @return affected trip offerings
     * @throws SQLException if the query fails
     */
    public List<Object[]> findTripOfferingsUsingBus(int busId) throws SQLException {
        String sql = """
                SELECT TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID
                FROM TripOffering
                WHERE BusID = ?
                ORDER BY Date, ScheduledStartTime
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, busId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new Object[]{
                            resultSet.getInt("TripNumber"),
                            resultSet.getDate("Date"),
                            resultSet.getTime("ScheduledStartTime"),
                            resultSet.getTime("ScheduledArrivalTime"),
                            resultSet.getString("DriverName"),
                            resultSet.getInt("BusID")
                    });
                }
            }
        }

        return rows;
    }

    /**
     * Adds a new bus if the BusID does not already exist.
     *
     * @param busId the bus ID
     * @param model the bus model
     * @param year the bus year
     */
    public String addBus(int busId, String model, int year) {
        String checkSql = "SELECT 1 FROM Bus WHERE BusID = ?";
        String insertSql = """
                INSERT INTO Bus (BusID, Model, Year)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            // Check for an existing BusID first so the user gets a clear message.
            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                checkStatement.setInt(1, busId);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return "A bus with that BusID already exists.";
                    }
                }
            }

            // If the BusID is new, insert the bus record.
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setInt(1, busId);
                insertStatement.setString(2, model);
                insertStatement.setInt(3, year);

                int rowsInserted = insertStatement.executeUpdate();

                if (rowsInserted > 0) {
                    return "Bus added successfully.";
                } else {
                    return "Bus was not added.";
                }
            }
        } catch (SQLException e) {
            return "Error: unable to add the bus. Details: " + e.getMessage();
        }
    }

    /**
     * Deletes a bus by BusID.
     * If the bus is still referenced by TripOffering, a helpful message is shown.
     *
     * @param busId the bus ID to delete
     */
    public String deleteBus(int busId) {
        String deleteSql = "DELETE FROM Bus WHERE BusID = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)
        ) {
            // Try to delete the bus. If it is still being used by TripOffering,
            // MySQL will reject the delete because of the foreign key constraint.
            deleteStatement.setInt(1, busId);

            int rowsDeleted = deleteStatement.executeUpdate();

            if (rowsDeleted > 0) {
                return "Bus deleted successfully.";
            } else {
                return "No bus with that BusID was found.";
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot delete this bus because it is still referenced by a trip offering. Update or delete the related trip offerings first, then try again.";
        } catch (SQLException e) {
            return "Error: unable to delete the bus. Details: " + e.getMessage();
        }
    }

    /**
     * Replaces a bus in all affected trip offerings and then deletes the old bus.
     * Both steps run in one transaction so the data stays consistent.
     *
     * @param oldBusId the bus being removed
     * @param replacementBusId the bus that will replace it
     * @return a user-friendly result message
     */
    public String replaceBusInTripOfferingsAndDeleteOldBus(int oldBusId, int replacementBusId) {
        String updateSql = "UPDATE TripOffering SET BusID = ? WHERE BusID = ?";
        String deleteSql = "DELETE FROM Bus WHERE BusID = ?";

        if (oldBusId == replacementBusId) {
            return "The replacement bus must be different from the bus being deleted.";
        }

        try (Connection connection = DBConnection.getConnection()) {
            if (!busExists(connection, oldBusId)) {
                return "No bus with that BusID was found.";
            }

            if (!busExists(connection, replacementBusId)) {
                return "The replacement bus does not exist.";
            }

            connection.setAutoCommit(false);

            try (
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)
            ) {
                updateStatement.setInt(1, replacementBusId);
                updateStatement.setInt(2, oldBusId);
                int updatedRows = updateStatement.executeUpdate();

                deleteStatement.setInt(1, oldBusId);
                int deletedRows = deleteStatement.executeUpdate();

                connection.commit();

                if (deletedRows > 0) {
                    return "Bus deleted successfully. " + updatedRows + " trip offering(s) were reassigned to BusID " + replacementBusId + ".";
                } else {
                    return "The bus replacement was applied, but the old bus was not deleted.";
                }
            } catch (SQLException e) {
                connection.rollback();
                return "Error: unable to replace and delete the bus. Details: " + e.getMessage();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return "Error: unable to replace and delete the bus. Details: " + e.getMessage();
        }
    }

    /**
     * Checks whether a bus exists.
     */
    private boolean busExists(Connection connection, int busId) throws SQLException {
        String sql = "SELECT 1 FROM Bus WHERE BusID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, busId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
