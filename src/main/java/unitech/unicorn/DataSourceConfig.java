package unitech.unicorn;

import unitech.unicorn.exception.DatabaseConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DataSourceConfig {
    private static Properties properties;
    private static String propFileName;

    public static final String URL_PREFIX = "urlPrefix";
    public static final String CONNECTION_STRING = "connectionString";
    public static final String HOST_NAME = "hostname";
    public static final String PORT = "port";
    public static final String DB_NAME = "dbName";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";

    public static void init(String propFileName) {
        properties = new Properties();

        try {
            File propFile = new File(propFileName);
            properties.load(new FileInputStream(propFile));
        } catch (FileNotFoundException e) {
            throw new DatabaseConfigurationException("Database access properties file not found");
        } catch (IOException e) {
            throw new DatabaseConfigurationException("Something went wrong: " + e.getMessage());
        }
    }

    public static String getProperty(String propName) {
        if (properties == null) {
            throw new DatabaseConfigurationException("Database access property file not specified. " +
                    "First run `init` method.");
        }

        return properties.getProperty(propName, lookupDefault(propName));
    }

    private static String lookupDefault(String propName) {
        switch (propName) {
            case HOST_NAME:
                return DEFAULT_HOST;
            case PORT:
                return DEFAULT_PORT;
            default:
                return null;
        }
    }
}
