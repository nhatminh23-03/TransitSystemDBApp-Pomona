package pomonatransit.service;

import pomonatransit.db.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TripService contains the read-only trip queries used by the GUI.
 * Each method returns rows that can be loaded into a JTable.
 */
public class TripService {

    /**
     * Finds trip schedules for a start location, destination, and date.
     *
     * @param startLocation the trip starting location
     * @param destination the trip destination
     * @param tripDate the date in YYYY-MM-DD format
     * @return table rows for the schedule screen
     * @throws SQLException if the query fails
     */
    public List<Object[]> findTripSchedules(String startLocation, String destination, String tripDate) throws SQLException {
        String sql = """
                SELECT t.TripNumber,
                       t.StartLocationName,
                       t.DestinationName,
                       o.ScheduledStartTime,
                       o.ScheduledArrivalTime,
                       o.DriverName,
                       o.BusID
                FROM Trip t
                JOIN TripOffering o ON t.TripNumber = o.TripNumber
                WHERE t.StartLocationName = ?
                  AND t.DestinationName = ?
                  AND o.Date = ?
                ORDER BY o.ScheduledStartTime
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, startLocation);
            preparedStatement.setString(2, destination);
            preparedStatement.setDate(3, Date.valueOf(tripDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new Object[]{
                            resultSet.getInt("TripNumber"),
                            resultSet.getString("StartLocationName"),
                            resultSet.getString("DestinationName"),
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
     * Finds the stops for one trip and includes the stop address through a join.
     *
     * @param tripNumber the trip number to search for
     * @return table rows for the trip stops screen
     * @throws SQLException if the query fails
     */
    public List<Object[]> findStopsForTrip(int tripNumber) throws SQLException {
        String sql = """
                SELECT tsi.StopNumber,
                       s.StopAddress,
                       tsi.SequenceNumber,
                       tsi.DrivingTime
                FROM TripStopInfo tsi
                LEFT JOIN Stop s ON tsi.StopNumber = s.StopNumber
                WHERE tsi.TripNumber = ?
                ORDER BY tsi.SequenceNumber ASC
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, tripNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new Object[]{
                            resultSet.getInt("StopNumber"),
                            resultSet.getString("StopAddress"),
                            resultSet.getInt("SequenceNumber"),
                            resultSet.getTime("DrivingTime")
                    });
                }
            }
        }

        return rows;
    }

    /**
     * Finds a weekly schedule for one driver starting from the given date.
     *
     * @param driverName the driver name
     * @param startDate the first date in YYYY-MM-DD format
     * @return table rows for the weekly schedule screen
     * @throws SQLException if the query fails
     */
    public List<Object[]> findWeeklyDriverSchedule(String driverName, String startDate) throws SQLException {
        String sql = """
                SELECT TripNumber,
                       Date,
                       ScheduledStartTime,
                       ScheduledArrivalTime,
                       BusID
                FROM TripOffering
                WHERE DriverName = ?
                  AND Date >= ?
                  AND Date < DATE_ADD(?, INTERVAL 7 DAY)
                ORDER BY Date ASC, ScheduledStartTime ASC
                """;

        List<Object[]> rows = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            Date sqlStartDate = Date.valueOf(startDate);

            preparedStatement.setString(1, driverName);
            preparedStatement.setDate(2, sqlStartDate);
            preparedStatement.setDate(3, sqlStartDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new Object[]{
                            resultSet.getInt("TripNumber"),
                            resultSet.getDate("Date"),
                            resultSet.getTime("ScheduledStartTime"),
                            resultSet.getTime("ScheduledArrivalTime"),
                            resultSet.getInt("BusID")
                    });
                }
            }
        }

        return rows;
    }
}
