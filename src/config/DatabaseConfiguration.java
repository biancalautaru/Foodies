package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfiguration {
    private static final String PROPERTIES_FILE = "db.properties";

    private static DatabaseConfiguration instance;

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    private DatabaseConfiguration() {
        Path propertiesPath = Paths.get(PROPERTIES_FILE).toAbsolutePath();
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(propertiesPath.toFile())) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("DatabaseConfiguration: failed to load " + propertiesPath, e);
        }

        this.url = props.getProperty("db.url");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");

        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("DatabaseConfiguration: failed to open connection to " + url, e);
        }
    }

    public static synchronized DatabaseConfiguration getInstance() {
        if (instance == null) {
            instance = new DatabaseConfiguration();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DatabaseConfiguration: failed to reopen connection", e);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("DatabaseConfiguration: error while closing connection: " + e.getMessage());
            }
        }
    }
}