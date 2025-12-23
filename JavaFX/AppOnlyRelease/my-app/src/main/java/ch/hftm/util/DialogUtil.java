package ch.hftm.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utility-Klasse für Dialoge und Benutzer-Feedback
 */
public class DialogUtil {

    /**
     * Zeigt eine Erfolgs-Meldung
     */
    public static void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Zeigt eine Fehler-Meldung
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Zeigt eine Warnung
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Zeigt eine Bestätigungs-Abfrage
     *
     * @return true wenn Benutzer "OK" klickt, false bei "Abbrechen"
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText(title);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Zeigt eine Validierungs-Fehler-Meldung
     */
    public static void showValidationError(String message) {
        showError("Ungültige Eingabe", message);
    }

    /**
     * Zeigt eine Datenbank-Fehler-Meldung
     */
    public static void showDatabaseError(String operation) {
        showError(
                "Datenbankfehler",
                "Die Operation \"" + operation + "\" konnte nicht durchgeführt werden.\n"
                + "Bitte überprüfen Sie Ihre Datenbankverbindung."
        );
    }
}
