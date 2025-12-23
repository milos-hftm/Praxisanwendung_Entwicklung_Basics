package ch.hftm.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.hftm.App;
import ch.hftm.persistence.DatabaseConnection;
import ch.hftm.util.DialogUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller für die Hauptnavigation
 */
@SuppressWarnings("unused") // FXML bindet Handler zur Laufzeit
public class MainController {

    @FXML
    private Label dbStatusLabel;

    private ScheduledExecutorService scheduler;
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    public void initialize() {
        // DB-Status regelmäßig prüfen (alle 30 Sekunden)
        startDatabaseStatusChecker(); // DB-Status regelmäßig prüfen

        // Scheduler sauber stoppen, wenn diese View (Main-Menü) verlassen wird
        if (dbStatusLabel != null) {
            dbStatusLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null && scheduler != null) {
                    scheduler.shutdownNow();
                }
            });
        }
    }

    private void startDatabaseStatusChecker() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "db-status-checker");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::checkDatabaseConnection, 0, 30, TimeUnit.SECONDS);
    }

    private void checkDatabaseConnection() {
        try {
            // Verbindung holen
            Connection conn = DatabaseConnection.getConnection();

            // Echte Verbindungsprüfung: SELECT 1 Query
            if (conn != null && !conn.isClosed()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT 1");

                    // Erfolg: Label grün aktualisieren
                    Platform.runLater(() -> {
                        if (dbStatusLabel != null) {
                            dbStatusLabel.setText("✓ PostgreSQL verbunden");
                            dbStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #0f766e; -fx-font-weight: 700; "
                                    + "-fx-background-color: #d1fae5; -fx-padding: 6 10; -fx-background-radius: 12; "
                                    + "-fx-border-color: #0f766e; -fx-border-width: 1; -fx-border-radius: 12;");
                        }
                    });
                }
            } else {
                throw new SQLException("Connection is null or closed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "DB-Statuscheck fehlgeschlagen", e);
            Platform.runLater(() -> {
                if (dbStatusLabel != null) {
                    dbStatusLabel.setText("✗ PostgreSQL Fehler");
                    dbStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f1d1d; -fx-font-weight: 700; "
                            + "-fx-background-color: #fee2e2; -fx-padding: 6 10; -fx-background-radius: 12; "
                            + "-fx-border-color: #7f1d1d; -fx-border-width: 1; -fx-border-radius: 12;");
                }
            });
        }
    }

    @FXML
    private void handleTerminverwaltung() {
        try {
            App.setSceneRoot("termin");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Terminverwaltung", e);
            DialogUtil.showError("Navigation fehlgeschlagen", "Fehler beim Laden der Terminverwaltung: " + e.getMessage());
        }
    }

    @FXML
    private void handleMitgliederverwaltung() {
        try {
            App.setSceneRoot("mitglied");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Mitgliederverwaltung", e);
            DialogUtil.showError("Navigation fehlgeschlagen", "Fehler beim Laden der Mitgliederverwaltung: " + e.getMessage());
        }
    }

    @FXML
    private void handleFormularverwaltung() {
        try {
            App.setSceneRoot("formular");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Formularverwaltung", e);
            DialogUtil.showError("Navigation fehlgeschlagen", "Fehler beim Laden der Formularverwaltung: " + e.getMessage());
        }
    }

    @FXML
    private void handleTeilnahmeverwaltung() {
        try {
            App.setSceneRoot("teilnahme");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Teilnahmeverwaltung", e);
            DialogUtil.showError("Navigation fehlgeschlagen", "Fehler beim Laden der Teilnahmeverwaltung: " + e.getMessage());
        }
    }

    @FXML
    private void handleBeenden() {
        System.exit(0);
    }

}
