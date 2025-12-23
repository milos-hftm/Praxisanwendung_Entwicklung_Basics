package ch.hftm.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Utility-Klasse für Eingabe-Validierung
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final String ERROR_STYLE = "-fx-border-color: #cc0000; -fx-border-width: 2px;";
    private static final String NORMAL_STYLE = "-fx-border-color: #000000; -fx-border-width: 1.5px;";

    /**
     * Prüft, ob ein TextField einen nicht-leeren Text enthält.
     *
     * @param field das zu prüfende Textfeld
     * @return true, wenn Text vorhanden ist (nach Trimmen nicht leer), sonst
     * false
     */
    public static boolean isNotEmpty(TextField field) {
        if (field == null) {
            return false;
        }
        String text = field.getText();
        boolean valid = text != null && !text.trim().isEmpty();

        if (!valid) {
            field.setStyle(ERROR_STYLE);
        } else {
            field.setStyle(NORMAL_STYLE);
        }

        return valid;
    }

    /**
     * Prüft, ob ein TextField ein gültiges Datum im ISO-Format (YYYY-MM-DD)
     * enthält.
     *
     * @param field das zu prüfende Textfeld
     * @return true, wenn ein gültiges Datum geparst werden kann, sonst false
     */
    public static boolean isValidDateText(TextField field) {
        if (field == null) {
            return false;
        }
        String text = field.getText();
        boolean valid = false;
        if (text != null && !text.trim().isEmpty()) {
            try {
                LocalDate.parse(text.trim());
                valid = true;
            } catch (DateTimeParseException ex) {
                valid = false;
            }
        }

        field.setStyle(valid ? NORMAL_STYLE : ERROR_STYLE);
        return valid;
    }

    /**
     * Prüft, ob ein TextField eine gültige Uhrzeit im ISO-Format (HH:MM)
     * enthält.
     *
     * @param field das zu prüfende Textfeld
     * @return true, wenn eine gültige Uhrzeit geparst werden kann, sonst false
     */
    public static boolean isValidTimeText(TextField field) {
        if (field == null) {
            return false;
        }
        String text = field.getText();
        boolean valid = false;
        if (text != null && !text.trim().isEmpty()) {
            try {
                LocalTime.parse(text.trim());
                valid = true;
            } catch (DateTimeParseException ex) {
                valid = false;
            }
        }

        field.setStyle(valid ? NORMAL_STYLE : ERROR_STYLE);
        return valid;
    }

    /**
     * Prüft, ob der Inhalt eines TextFields einer einfachen E-Mail-Regex
     * entspricht.
     *
     * @param field das zu prüfende Textfeld
     * @return true, wenn das Muster passt, sonst false
     */
    public static boolean isValidEmail(TextField field) {
        if (field == null) {
            return false;
        }
        String email = field.getText();
        boolean valid = email != null && EMAIL_PATTERN.matcher(email.trim()).matches();

        if (!valid) {
            field.setStyle(ERROR_STYLE);
        } else {
            field.setStyle(NORMAL_STYLE);
        }

        return valid;
    }

    /**
     * Prüft, ob ein DatePicker ein Datum gesetzt hat.
     *
     * @param picker der zu prüfende DatePicker
     * @return true, wenn ein Datum vorhanden ist, sonst false
     */
    public static boolean hasDate(DatePicker picker) {
        if (picker == null) {
            return false;
        }
        LocalDate date = picker.getValue();
        boolean valid = date != null;

        if (!valid) {
            picker.setStyle(ERROR_STYLE);
        } else {
            picker.setStyle(NORMAL_STYLE);
        }

        return valid;
    }

    /**
     * Prüft, ob ein DatePicker ein zukünftiges oder heutiges Datum gesetzt hat.
     *
     * @param picker der zu prüfende DatePicker
     * @return true, wenn Datum heute oder in der Zukunft liegt, sonst false
     */
    public static boolean isFutureOrToday(DatePicker picker) {
        if (picker == null) {
            return false;
        }
        LocalDate date = picker.getValue();
        boolean valid = date != null && !date.isBefore(LocalDate.now());

        if (!valid) {
            picker.setStyle(ERROR_STYLE);
        } else {
            picker.setStyle(NORMAL_STYLE);
        }

        return valid;
    }

    /**
     * Prüft, ob eine ComboBox eine Auswahl hat.
     *
     * @param comboBox die zu prüfende ComboBox
     * @return true, wenn eine Auswahl vorhanden ist, sonst false
     */
    public static boolean hasSelection(ComboBox<?> comboBox) {
        if (comboBox == null) {
            return false;
        }
        boolean valid = comboBox.getValue() != null;

        if (!valid) {
            comboBox.setStyle(ERROR_STYLE);
        } else {
            comboBox.setStyle(NORMAL_STYLE);
        }

        return valid;
    }

    /**
     * Setzt alle übergebenen TextField-Instanzen auf den normalen Style zurück.
     *
     * @param fields die zurückzusetzenden Textfelder
     */
    public static void resetStyle(TextField... fields) {
        if (fields == null) {
            return;
        }
        for (TextField field : fields) {
            if (field != null) {
                field.setStyle(NORMAL_STYLE);
            }
        }
    }

    /**
     * Setzt alle übergebenen DatePicker-Instanzen auf den normalen Style
     * zurück.
     *
     * @param pickers die zurückzusetzenden DatePicker
     */
    public static void resetStyle(DatePicker... pickers) {
        if (pickers == null) {
            return;
        }
        for (DatePicker picker : pickers) {
            if (picker != null) {
                picker.setStyle(NORMAL_STYLE);
            }
        }
    }

    /**
     * Setzt alle übergebenen ComboBox-Instanzen auf den normalen Style zurück.
     *
     * @param comboBoxes die zurückzusetzenden ComboBoxen
     */
    public static void resetStyle(ComboBox<?>... comboBoxes) {
        if (comboBoxes == null) {
            return;
        }
        for (ComboBox<?> cb : comboBoxes) {
            if (cb != null) {
                cb.setStyle(NORMAL_STYLE);
            }
        }
    }
}
