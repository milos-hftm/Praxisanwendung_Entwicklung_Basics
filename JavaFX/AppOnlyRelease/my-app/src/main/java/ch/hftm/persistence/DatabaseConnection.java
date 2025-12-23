package ch.hftm.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Verwaltet die Datenbankverbindung für die Anwendung
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // Konfiguration wird geladen aus (Priorität):
    // 1) Java System Properties: db.url / db.user / db.password
    // 2) Environment: KUD_DB_URL / KUD_DB_USER / KUD_DB_PASSWORD
    // 3) Classpath: /db.properties
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/transferdemo";
    private static final String DEFAULT_DB_USER = "transferdemo";
    private static final String DEFAULT_DB_PASSWORD = "transferdemo";

    private static Connection connection = null;

    private static final Properties DB_PROPS = loadDbProperties();

    private static Properties loadDbProperties() {
        Properties p = new Properties();
        try (var in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                p.load(in);
            } else {
                LOGGER.warning("db.properties nicht gefunden – verwende Defaults/Environment/System Properties");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Konnte db.properties nicht laden – verwende Defaults/Environment/System Properties", e);
        }
        return p;
    }

    private static String getConfigValue(String sysProp, String envVar, String propKey, String fallback) {
        String fromSys = System.getProperty(sysProp);
        if (fromSys != null && !fromSys.isBlank()) {
            return fromSys.trim();
        }

        String fromEnv = System.getenv(envVar);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }

        String fromFile = DB_PROPS.getProperty(propKey);
        if (fromFile != null && !fromFile.isBlank()) {
            return fromFile.trim();
        }

        return fallback;
    }

    private static String dbUrl() {
        return getConfigValue("db.url", "KUD_DB_URL", "db.url", DEFAULT_DB_URL);
    }

    private static String dbUser() {
        return getConfigValue("db.user", "KUD_DB_USER", "db.user", DEFAULT_DB_USER);
    }

    private static String dbPassword() {
        return getConfigValue("db.password", "KUD_DB_PASSWORD", "db.password", DEFAULT_DB_PASSWORD);
    }

    /**
     * Stellt eine Verbindung zur Datenbank her oder gibt die bestehende zurück
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(dbUrl(), dbUser(), dbPassword());
                LOGGER.info("Datenbankverbindung erfolgreich hergestellt");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Fehler beim Verbinden zur Datenbank", e);
                throw e;
            }
        }
        return connection;
    }

    /**
     * Schliesst die Datenbankverbindung
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Datenbankverbindung geschlossen");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Fehler beim Schliessen der Datenbankverbindung", e);
            }
        }
    }

    /**
     * Testet die Datenbankverbindung
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Verbindungstest fehlgeschlagen", e);
            return false;
        }
    }
}
