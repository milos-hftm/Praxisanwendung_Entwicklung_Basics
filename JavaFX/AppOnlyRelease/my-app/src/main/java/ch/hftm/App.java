package ch.hftm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import ch.hftm.persistence.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * JavaFX App - KUD Karadjordje Termin- und Formularverwaltung
 */
public class App extends Application {

    private static Scene scene;
    private static String currentView = "main";
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static Stage primaryStage;

    public static Scene getScene() {
        return scene;
    }

    private static void applyTheme() {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(App.class.getResource("/main.css").toExternalForm());
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("main"), 900, 650);
        currentView = "main";
        applyTheme(); // Initial theme

        // Globale Shortcuts (z.B. F11/F12) als EventFilter, damit sie in allen Views funktionieren
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

        // Standard: echtes Vollbild (deckt unter Windows auch die Taskleiste ab).
        // Hinweis: Per F11 kann jederzeit zurück in den Fenstermodus gewechselt werden.
        stage.setFullScreenExitHint("");
        // Optional: verhindere, dass ESC automatisch Fullscreen verlässt
        // (Benutzer kann dann ausschließlich per F11 toggeln)
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.setTitle("KUD Karadjordje Bern - Verwaltungssystem");
        stage.setScene(scene);
        stage.show();

        // Nach dem Anzeigen aktivieren (ist auf manchen Systemen zuverlässiger als vor show()).
        Platform.runLater(() -> stage.setFullScreen(true));
    }

    @Override
    public void stop() {
        // Ressourcen sauber freigeben
        try {
            DatabaseConnection.closeConnection();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Fehler beim Schliessen der DB-Verbindung", e);
        }
    }

    /**
     * Globale Tastatur-Shortcuts (z.B. F11/F12)
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.F11) {
            toggleFullScreen();
            event.consume();
            return;
        }
        if (event.getCode() == KeyCode.F12) {
            try {
                Path filePath = saveScreenshotAuto();
                LOGGER.log(Level.INFO, "Screenshot gespeichert: {0}", filePath);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Fehler beim Screenshot: {0}", e.getMessage());
            }
            event.consume();
        }
    }

    private static void toggleFullScreen() {
        if (primaryStage == null) {
            return;
        }
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    public static void setSceneRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        currentView = fxml;
        applyTheme(); // Theme nach Navigation neu anwenden
    }

    private static Parent loadFXML(String fxml) throws IOException {
        var resource = App.class.getResource("/" + fxml + ".fxml");
        if (resource == null) {
            IOException ex = new IOException("FXML nicht gefunden: /" + fxml + ".fxml");
            LOGGER.log(Level.SEVERE, "FXML nicht gefunden: {0}", "/" + fxml + ".fxml");
            throw ex;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            Throwable cause = e.getCause();
            IOException wrapped = new IOException("Fehler beim Laden von '" + fxml + ".fxml': " + (cause != null ? cause.toString() : e.toString()), cause != null ? cause : e);
            LOGGER.log(Level.SEVERE, "Fehler beim Laden von {0}: {1}", new Object[]{fxml + ".fxml", wrapped.getMessage()});
            throw wrapped;
        }
    }

    /**
     * Speichert einen Screenshot der aktuellen Scene-Root als PNG unter
     * bericht/screenshots. Der Dateiname wird basierend auf der aktuellen View
     * automatisch gewählt.
     *
     * @return Pfad zur gespeicherten Datei
     * @throws IOException bei Schreibfehlern
     */
    public static Path saveScreenshotAuto() throws IOException {
        if (scene == null || scene.getRoot() == null) {
            throw new IOException("Scene oder Root nicht vorhanden");
        }

        String fileName = switch (currentView) {
            case "main" ->
                "01_main_menu.png";
            case "termin" ->
                "02_terminverwaltung.png";
            case "mitglied" ->
                "03_mitgliederverwaltung.png";
            case "formular" ->
                "04_formularverwaltung.png";
            case "teilnahme" ->
                "05_teilnahmeverwaltung.png";
            default ->
                "screenshot.png";
        };

        Path targetDir = resolveScreenshotsDir();
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(fileName);

        WritableImage image = scene.getRoot().snapshot(new SnapshotParameters(), null);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", targetFile.toFile());
        return targetFile;
    }

    /**
     * Ermittelt einen sinnvollen Speicherort für Screenshots.
     * <ul>
     * <li>Im Workspace: <code>bericht/screenshots</code> (wenn vorhanden)</li>
     * <li>Fallback:
     * <code>%USERPROFILE%/.kud-karadjordje/screenshots</code></li>
     * </ul>
     */
    private static Path resolveScreenshotsDir() {
        Path working = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path cursor = working;

        // Suche nach Workspace-Root (Ordner enthält 'bericht') – robust gegen unterschiedliche Startpfade
        for (int i = 0; i < 8 && cursor != null; i++) {
            Path berichtDir = cursor.resolve("bericht");
            if (Files.isDirectory(berichtDir)) {
                return berichtDir.resolve("screenshots");
            }
            cursor = cursor.getParent();
        }

        // Fallback für z.B. jlink/Release oder wenn im Arbeitsverzeichnis keine Repo-Struktur existiert
        return Paths.get(System.getProperty("user.home"), ".kud-karadjordje", "screenshots");
    }

}
