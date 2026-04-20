package pomonatransit.service;

import pomonatransit.db.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains database operations for the TripOffering table.
 * Each method handles one edit feature from the assignment.
 */
public class TripOfferingService {

    /**
     * Returns all trip offerings so the GUI can show a live preview table.
     *
     * @return all trip offering rows
     * @throws SQLException if the query fails
     */
    public List<Object[]> findAllTripOfferings() throws SQLException {
        String sql = """
                SELECT TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID
                FROM TripOffering
                ORDER BY Date, ScheduledStartTime
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                rows.add(mapTripOfferingRow(resultSet));
            }
        }

        return rows;
    }

    /**
     * Finds trip offerings by the composite key fields shown in the homework workflow.
     *
     * @param tripNumber the trip number
     * @param tripDate the date in YYYY-MM-DD format
     * @param scheduledStartTime the scheduled start time in HH:MM:SS format
     * @return matching rows, usually 0 or 1
     * @throws SQLException if the query fails
     */
    public List<Object[]> findTripOfferingsByKey(int tripNumber, String tripDate, String scheduledStartTime) throws SQLException {
        String sql = """
                SELECT TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID
                FROM TripOffering
                WHERE TripNumber = ?
                  AND Date = ?
                  AND ScheduledStartTime = ?
                ORDER BY Date, ScheduledStartTime
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, tripNumber);
            preparedStatement.setDate(2, Date.valueOf(tripDate));
            preparedStatement.setTime(3, Time.valueOf(scheduledStartTime));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapTripOfferingRow(resultSet));
                }
            }
        }

        return rows;
    }

    /**
     * Deletes one trip offering using its composite primary key.
     *
     * @param tripNumber the trip number
     * @param tripDate the date of the trip offering in YYYY-MM-DD format
     * @param scheduledStartTime the scheduled start time in HH:MM:SS format
     */
    public String deleteTripOffering(int tripNumber, String tripDate, String scheduledStartTime) {
        String sql = """
                DELETE FROM TripOffering
                WHERE TripNumber = ?
                  AND Date = ?
                  AND ScheduledStartTime = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, tripNumber);
            preparedStatement.setDate(2, Date.valueOf(tripDate));
            preparedStatement.setTime(3, Time.valueOf(scheduledStartTime));

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                return "Trip offering deleted successfully.";
            } else {
                return "No matching trip offering was found to delete.";
            }
        } catch (IllegalArgumentException e) {
            return "Invalid date or time format. Use YYYY-MM-DD and HH:MM:SS.";
        } catch (SQLException e) {
            return "Error: unable to delete the trip offering. Details: " + e.getMessage();
        }
    }

    /**
     * Adds one trip offering after checking that the foreign key values exist.
     *
     * @param tripNumber the trip number
     * @param tripDate the date in YYYY-MM-DD format
     * @param scheduledStartTime the start time in HH:MM:SS format
     * @param scheduledArrivalTime the arrival time in HH:MM:SS format
     * @param driverName the assigned driver
     * @param busId the assigned bus
     */
    public String addTripOffering(
            int tripNumber,
            String tripDate,
            String scheduledStartTime,
            String scheduledArrivalTime,
            String driverName,
            int busId) {

        String sql = """
                INSERT INTO TripOffering
                (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            // Before inserting, check that the related Trip, Driver, and Bus exist.
            // This gives the user clearer feedback than a raw foreign key error.
            if (!tripExists(connection, tripNumber)) {
                return "Cannot add trip offering: TripNumber " + tripNumber + " does not exist.";
            }

            if (!driverExists(connection, driverName)) {
                return "Cannot add trip offering: Driver '" + driverName + "' does not exist.";
            }

            if (!busExists(connection, busId)) {
                return "Cannot add trip offering: BusID " + busId + " does not exist.";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, tripNumber);
                preparedStatement.setDate(2, Date.valueOf(tripDate));
                preparedStatement.setTime(3, Time.valueOf(scheduledStartTime));
                preparedStatement.setTime(4, Time.valueOf(scheduledArrivalTime));
                preparedStatement.setString(5, driverName);
                preparedStatement.setInt(6, busId);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    return "Trip offering added successfully.";
                } else {
                    return "Trip offering was not added.";
                }
            }
        } catch (IllegalArgumentException e) {
            return "Invalid date or time format. Use YYYY-MM-DD and HH:MM:SS.";
        } catch (SQLException e) {
            return "Error: unable to add the trip offering. Details: " + e.getMessage();
        }
    }

    /**
     * Updates the driver for a specific trip offering.
     *
     * @param tripNumber the trip number
     * @param tripDate the date in YYYY-MM-DD format
     * @param scheduledStartTime the start time in HH:MM:SS format
     * @param newDriverName the new driver name
     */
    public String updateTripOfferingDriver(int tripNumber, String tripDate, String scheduledStartTime, String newDriverName) {
        String sql = """
                UPDATE TripOffering
                SET DriverName = ?
                WHERE TripNumber = ?
                  AND Date = ?
                  AND ScheduledStartTime = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            // Make sure the new driver exists before updating.
            if (!driverExists(connection, newDriverName)) {
                return "Cannot update trip offering: Driver '" + newDriverName + "' does not exist.";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newDriverName);
                preparedStatement.setInt(2, tripNumber);
                preparedStatement.setDate(3, Date.valueOf(tripDate));
                preparedStatement.setTime(4, Time.valueOf(scheduledStartTime));

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    return "Driver updated successfully for the trip offering.";
                } else {
                    return "No matching trip offering was found to update.";
                }
            }
        } catch (IllegalArgumentException e) {
            return "Invalid date or time format. Use YYYY-MM-DD and HH:MM:SS.";
        } catch (SQLException e) {
            return "Error: unable to update the driver. Details: " + e.getMessage();
        }
    }

    /**
     * Updates the bus for a specific trip offering.
     *
     * @param tripNumber the trip number
     * @param tripDate the date in YYYY-MM-DD format
     * @param scheduledStartTime the start time in HH:MM:SS format
     * @param newBusId the new bus id
     */
    public String updateTripOfferingBus(int tripNumber, String tripDate, String scheduledStartTime, int newBusId) {
        String sql = """
                UPDATE TripOffering
                SET BusID = ?
                WHERE TripNumber = ?
                  AND Date = ?
                  AND ScheduledStartTime = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            // Make sure the new bus exists before updating.
            if (!busExists(connection, newBusId)) {
                return "Cannot update trip offering: BusID " + newBusId + " does not exist.";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, newBusId);
                preparedStatement.setInt(2, tripNumber);
                preparedStatement.setDate(3, Date.valueOf(tripDate));
                preparedStatement.setTime(4, Time.valueOf(scheduledStartTime));

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    return "Bus updated successfully for the trip offering.";
                } else {
                    return "No matching trip offering was found to update.";
                }
            }
        } catch (IllegalArgumentException e) {
            return "Invalid date or time format. Use YYYY-MM-DD and HH:MM:SS.";
        } catch (SQLException e) {
            return "Error: unable to update the bus. Details: " + e.getMessage();
        }
    }

    /**
     * Checks whether a trip exists.
     */
    private boolean tripExists(Connection connection, int tripNumber) throws SQLException {
        String sql = "SELECT 1 FROM Trip WHERE TripNumber = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, tripNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Checks whether a driver exists.
     */
    private boolean driverExists(Connection connection, String driverName) throws SQLException {
        String sql = "SELECT 1 FROM Driver WHERE DriverName = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, driverName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
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

    /**
     * Builds one trip offering row for a JTable.
     */
    private Object[] mapTripOfferingRow(ResultSet resultSet) throws SQLException {
        return new Object[]{
                resultSet.getInt("TripNumber"),
                resultSet.getDate("Date"),
                resultSet.getTime("ScheduledStartTime"),
                resultSet.getTime("ScheduledArrivalTime"),
                resultSet.getString("DriverName"),
                resultSet.getInt("BusID")
        };
    }
}
