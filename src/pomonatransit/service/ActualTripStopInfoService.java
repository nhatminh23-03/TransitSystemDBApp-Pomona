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
 * This class contains database operations for ActualTripStopInfo.
 * It records the actual stop data for a specific trip offering.
 */
public class ActualTripStopInfoService {

    /**
     * Returns all actual trip stop records for the GUI preview table.
     *
     * @return table rows ordered by trip offering and stop
     * @throws SQLException if the query fails
     */
    public List<Object[]> findAllActualTripStopData() throws SQLException {
        String sql = """
                SELECT TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime,
                       ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut
                FROM ActualTripStopInfo
                ORDER BY Date, ScheduledStartTime, StopNumber
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                rows.add(new Object[]{
                        resultSet.getInt("TripNumber"),
                        resultSet.getDate("Date"),
                        resultSet.getTime("ScheduledStartTime"),
                        resultSet.getInt("StopNumber"),
                        resultSet.getTime("ScheduledArrivalTime"),
                        resultSet.getTime("ActualStartTime"),
                        resultSet.getTime("ActualArrivalTime"),
                        resultSet.getInt("NumberOfPassengerIn"),
                        resultSet.getInt("NumberOfPassengerOut")
                });
            }
        }

        return rows;
    }

    /**
     * Inserts one row into ActualTripStopInfo after validating the related trip offering
     * and confirming that the stop belongs to the trip.
     *
     * @param tripNumber the trip number
     * @param tripDate the trip date in YYYY-MM-DD format
     * @param scheduledStartTime the scheduled trip start time in HH:MM:SS format
     * @param stopNumber the stop number
     * @param scheduledArrivalTime the scheduled arrival time at the stop in HH:MM:SS format
     * @param actualStartTime the actual start time in HH:MM:SS format
     * @param actualArrivalTime the actual arrival time in HH:MM:SS format
     * @param numberOfPassengerIn passengers getting in
     * @param numberOfPassengerOut passengers getting out
     */
    public String recordActualTripStopData(
            int tripNumber,
            String tripDate,
            String scheduledStartTime,
            int stopNumber,
            String scheduledArrivalTime,
            String actualStartTime,
            String actualArrivalTime,
            int numberOfPassengerIn,
            int numberOfPassengerOut) {

        String insertSql = """
                INSERT INTO ActualTripStopInfo
                (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime,
                 ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            Date sqlDate = Date.valueOf(tripDate);
            Time sqlScheduledStartTime = Time.valueOf(scheduledStartTime);

            // First, make sure the trip offering exists so the insert references
            // a valid parent row in TripOffering.
            if (!tripOfferingExists(connection, tripNumber, sqlDate, sqlScheduledStartTime)) {
                return "Cannot record data: the referenced trip offering does not exist.";
            }

            // Next, make sure the stop belongs to the trip according to TripStopInfo.
            if (!stopBelongsToTrip(connection, tripNumber, stopNumber)) {
                return "Cannot record data: StopNumber " + stopNumber + " does not belong to TripNumber " + tripNumber + ".";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                // Prepared statements safely place the user's values into the SQL INSERT.
                preparedStatement.setInt(1, tripNumber);
                preparedStatement.setDate(2, sqlDate);
                preparedStatement.setTime(3, sqlScheduledStartTime);
                preparedStatement.setInt(4, stopNumber);
                preparedStatement.setTime(5, Time.valueOf(scheduledArrivalTime));
                preparedStatement.setTime(6, Time.valueOf(actualStartTime));
                preparedStatement.setTime(7, Time.valueOf(actualArrivalTime));
                preparedStatement.setInt(8, numberOfPassengerIn);
                preparedStatement.setInt(9, numberOfPassengerOut);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    return "Actual trip stop data recorded successfully.";
                } else {
                    return "Actual trip stop data was not recorded.";
                }
            }
        } catch (IllegalArgumentException e) {
            return "Invalid date or time format. Use YYYY-MM-DD and HH:MM:SS.";
        } catch (SQLException e) {
            return "Error: unable to record actual trip stop data. Details: " + e.getMessage();
        }
    }

    /**
     * Checks whether the referenced trip offering exists.
     */
    private boolean tripOfferingExists(Connection connection, int tripNumber, Date tripDate, Time scheduledStartTime)
            throws SQLException {
        String sql = """
                SELECT 1
                FROM TripOffering
                WHERE TripNumber = ?
                  AND Date = ?
                  AND ScheduledStartTime = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, tripNumber);
            preparedStatement.setDate(2, tripDate);
            preparedStatement.setTime(3, scheduledStartTime);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Checks whether a stop belongs to a trip in TripStopInfo.
     */
    private boolean stopBelongsToTrip(Connection connection, int tripNumber, int stopNumber) throws SQLException {
        String sql = """
                SELECT 1
                FROM TripStopInfo
                WHERE TripNumber = ?
                  AND StopNumber = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, tripNumber);
            preparedStatement.setInt(2, stopNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
