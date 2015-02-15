package mailer;

/*
 * Singleton for connecting to a database using JDBC
 *
 * @author blah-blah
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

    // Credentials for connections
    private static Connection connection = null;
    private static final String table_url = "jdbc:mysql://127.0.0.1/java_db_test";
    private static final String user = "***";
    private static final String password = "*****";
    private static final String driver_class = "com.mysql.jdbc.Driver";

    /**
     * Method that loads the specified driver
     *
     * @return void
     *
     */
    private static void loadDriver() {
        try {
            Class.forName(driver_class);
        } catch (Exception e) {
            errorHandler("Failed to load the driver " + driver_class, e);
        }
    }

    /**
     * This function is for connection loading
     *
     * @return void
     *
     */
    private static void loadConnection() {
        try {
            connection = DriverManager.getConnection(table_url, user, password);
        } catch (SQLException e) {
            errorHandler("Failed to connect to the database:", e);
        }
    }

    /**
     * Static method that returns the instance for the singleton
     *
     * @return {Connection} connection
     *
     */
    public static Connection getConnection() {
        if (connection == null) {               //Make if connection is not established.
            loadDriver();
            loadConnection();
        }
        return connection;
    }

    /**
     * Method that shows the errors
     *
     * @param {String} message
     * @option {Exception} e
     *
     */
    public static void errorHandler(String message, Exception e) {
        System.out.println(message);
        if (e != null) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Static function that close the connection to the database
     */
    public static void closeConnection() {
        if (connection == null) {
            errorHandler("No connection found", null);
        } else {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                errorHandler("Failed to close the connection", e);
            }
        }
    }
}
