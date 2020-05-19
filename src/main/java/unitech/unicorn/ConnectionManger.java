package unitech.unicorn;

import unitech.unicorn.exception.SchemaAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManger {
    private static String CONNECTION_STRING;

    public static Connection getConnection() {
        Connection connection;
        String connectionString = DataSourceConfig.getProperty(DataSourceConfig.CONNECTION_STRING);
        try {
            connection = DriverManager.getConnection(connectionString);

            return connection;
        } catch (SQLException e) {
            throw new SchemaAccessException("Couldn't connect to database: " + e.getMessage());
        }
    }

    public static void releaseConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new SchemaAccessException("Couldn't close connection: " + e.getMessage());
        }
    }
}
