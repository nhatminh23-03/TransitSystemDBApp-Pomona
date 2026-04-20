package pomonatransit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class handles the database connection for the project.
 * Keeping the connection code in one place makes the app easier
 * to understand and maintain.
 */
public class DBConnection {

    // JDBC URL for the MySQL database.
    // Update the database name if your schema uses a different one.
    private static final String URL = "jdbc:mysql://localhost:3306/pomona_transit_system";

    // MySQL username.
    private static final String USERNAME = "root";

    // MySQL password.
    // Replace this with your real MySQL password.
    private static final String PASSWORD = "your_mysql_password";

    /**
     * Opens and returns a connection to the MySQL database.
     *
     * @return a JDBC Connection object
     * @throws SQLException if the connection cannot be created
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager uses the connection details above
        // to connect the Java program to MySQL.
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Checks whether a database connection can be opened.
     * This is useful for GUI status labels.
     *
     * @return true if the connection works, false otherwise
     */
    public static boolean isConnectionAvailable() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
