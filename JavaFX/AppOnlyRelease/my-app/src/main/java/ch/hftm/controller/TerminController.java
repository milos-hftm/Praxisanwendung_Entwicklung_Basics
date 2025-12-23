package ch.hftm.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;

import ch.hftm.App;
import ch.hftm.model.Termin;
import ch.hftm.persistence.TerminRepository;
import ch.hftm.util.DialogUtil;
import ch.hftm.util.ExportUtil;
import ch.hftm.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 * Controller für die Terminverwaltung
 */
@SuppressWarnings("unused") // FXML bindet Felder und Handler methoden zur Laufzeit
public class TerminController {

    private static final PseudoClass UPCOMING_PSEUDO_CLASS = PseudoClass.getPseudoClass("upcoming");

    private static final DateTimeFormatter DATE_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_INPUT_DOT_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");
    private static final DateTimeFormatter DATE_INPUT_DOT_2Y_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("d.M.")
            // interpretiere 2-stelliges Jahr als 20xx
            .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
            .toFormatter();

    @FXML
    private TableView<Termin> terminTable;
    @FXML
    private TableColumn<Termin, Integer> terminIdColumn;
    @FXML
    private TableColumn<Termin, LocalDate> datumColumn;
    @FXML
    private TableColumn<Termin, LocalTime> uhrzeitColumn;
    @FXML
    private TableColumn<Termin, String> ortColumn;
    @FXML
    private TableColumn<Termin, Boolean> ferienFlagColumn;

    @FXML
    private DatePicker datumPicker;
    @FXML
    private TextField uhrzeitField;
    @FXML
    private TextField ortField;
    @FXML
    private CheckBox ferienCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    @FXML
    private TextField searchField;
    @FXML
    private CheckBox nurKommendeCheckBox;
    @FXML
    private DatePicker vonDatumPicker;
    @FXML
    private DatePicker bisDatumPicker;

    private TerminRepository terminRepository;
    private ObservableList<Termin> terminList;
    private FilteredList<Termin> filteredTermine;
    private SortedList<Termin> sortedTermine;
    private Termin selectedTermin;

    @FXML
    public void initialize() {
        terminRepository = new TerminRepository();
        terminList = FXCollections.observableArrayList();

        // DatePicker konfigurieren (Kalender + Tippen; robustes Parsing)
        configureDatePicker(vonDatumPicker, "Von-Datum (TT.MM.JJJJ)");
        configureDatePicker(bisDatumPicker, "Bis-Datum (TT.MM.JJJJ)");
        configureDatePicker(datumPicker, "Datum wählen (TT.MM.JJJJ)");

        // Tabellenspalten konfigurieren
        terminIdColumn.setCellValueFactory(new PropertyValueFactory<>("terminId"));
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datum"));
        uhrzeitColumn.setCellValueFactory(new PropertyValueFactory<>("uhrzeit"));
        ortColumn.setCellValueFactory(new PropertyValueFactory<>("ort"));
        ferienFlagColumn.setCellValueFactory(new PropertyValueFactory<>("ferienFlag"));

        // Filter + Sort vorbereiten
        filteredTermine = new FilteredList<>(terminList, t -> true);
        sortedTermine = new SortedList<>(filteredTermine);
        sortedTermine.comparatorProperty().bind(terminTable.comparatorProperty());
        terminTable.setItems(sortedTermine);

        // Zeilen hervorheben: kommende Termine
        installUpcomingRowHighlight();

        // Selektions-Listener
        terminTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectTermin(newSelection);
                    }
                }
        );

        // Live-Suche/Filter
        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        }
        if (vonDatumPicker != null) {
            vonDatumPicker.valueProperty().addListener((obs, o, n) -> applyFilter());
        }
        if (bisDatumPicker != null) {
            bisDatumPicker.valueProperty().addListener((obs, o, n) -> applyFilter());
        }
        if (nurKommendeCheckBox != null) {
            nurKommendeCheckBox.selectedProperty().addListener((obs, o, n) -> loadAllTermine());
        }

        loadAllTermine();
    }

    private void installUpcomingRowHighlight() {
        if (terminTable == null) {
            return;
        }

        terminTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Termin item, boolean empty) {
                super.updateItem(item, empty);

                boolean upcoming = !empty
                        && item != null
                        && item.getDatum() != null
                        && !item.getDatum().isBefore(LocalDate.now());

                pseudoClassStateChanged(UPCOMING_PSEUDO_CLASS, upcoming);
            }
        });
    }

    private void configureDatePicker(DatePicker picker, String promptText) {
        if (picker == null) {
            return;
        }

        picker.setEditable(true);
        picker.setPromptText(promptText);

        // Wichtig: Wir formatieren immer gleich (dd.MM.yyyy), akzeptieren beim Tippen aber auch d.M.yyyy und ISO.
        picker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? DATE_OUTPUT_FORMAT.format(date) : "";
            }

            @Override
            public LocalDate fromString(String text) {
                LocalDate parsed = parseDateLenient(text);
                if (parsed != null) {
                    return parsed;
                }
                // Bei ungültiger Eingabe nicht "leer machen", sondern aktuellen Wert behalten.
                return picker.getValue();
            }
        });

        // JavaFX committed getippte Werte nicht immer automatisch (z.B. bei Fokusverlust).
        // Daher explizit committen bei Enter und beim Fokusverlust.
        installDatePickerCommitHandlers(picker);
    }

    private void installDatePickerCommitHandlers(DatePicker picker) {
        if (picker.getEditor() == null) {
            return;
        }

        picker.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode() == KeyCode.ENTER) {
                commitDatePickerEditorText(picker);
                evt.consume();
            }
        });

        picker.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (Boolean.TRUE.equals(wasFocused) && Boolean.FALSE.equals(isFocused)) {
                commitDatePickerEditorText(picker);
            }
        });
    }

    private void commitDatePickerEditorText(DatePicker picker) {
        String text = picker.getEditor().getText();
        if (text == null || text.trim().isEmpty()) {
            // User hat bewusst geleert -> Filter/Datum zurücksetzen
            picker.setValue(null);
            picker.getEditor().setText("");
            return;
        }

        LocalDate parsed = parseDateLenient(text);
        if (parsed != null) {
            picker.setValue(parsed);
            // Normalisieren auf dd.MM.yyyy
            picker.getEditor().setText(picker.getConverter().toString(parsed));
        } else {
            // Ungültige Eingabe -> Text stehen lassen, damit User korrigieren kann.
            // (Value bleibt unverändert.)
        }
    }

    private LocalDate parseDateLenient(String text) {
        if (text == null) {
            return null;
        }
        String s = text.trim();
        if (s.isEmpty()) {
            return null;
        }

        // Vereinheitlichen: 01/01/2025 -> 01.01.2025
        s = s.replace('/', '.');

        // 1) Schweizer Punktformat: d.M.yyyy (akzeptiert 1.1.2025 und 01.01.2025)
        try {
            return LocalDate.parse(s, DATE_INPUT_DOT_FORMAT);
        } catch (DateTimeParseException ignored) {
            // weiter versuchen
        }

        // 1b) Schweizer Punktformat mit 2-stelligem Jahr: d.M.yy (z.B. 1.1.25 -> 2025-01-01)
        try {
            return LocalDate.parse(s, DATE_INPUT_DOT_2Y_FORMAT);
        } catch (DateTimeParseException ignored) {
            // weiter versuchen
        }

        // 2) ISO-Format: yyyy-MM-dd (Default vieler DatePicker/Copy-Paste)
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    /**
     * Lädt alle Termine aus der Datenbank
     */
    private void loadAllTermine() {
        // Basisdaten laden
        terminList.clear();
        if (nurKommendeCheckBox != null && nurKommendeCheckBox.isSelected()) {
            terminList.addAll(terminRepository.findUpcoming());
        } else {
            terminList.addAll(terminRepository.findAll());
        }
        // Filter anwenden
        applyFilter();
    }

    /**
     * Wendet Datumsbereich und Volltextsuche über Ort/Datum/Uhrzeit an
     */
    private void applyFilter() {
        final LocalDate von = vonDatumPicker != null ? vonDatumPicker.getValue() : null;
        final LocalDate bis = bisDatumPicker != null ? bisDatumPicker.getValue() : null;
        final String q = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";

        filteredTermine.setPredicate(t -> {
            // Datumsbereich
            if (von != null && t.getDatum() != null && t.getDatum().isBefore(von)) {
                return false;
            }
            if (bis != null && t.getDatum() != null && t.getDatum().isAfter(bis)) {
                return false;
            }

            // Volltextsuche
            if (q.isEmpty()) {
                return true;
            }
            boolean byOrt = t.getOrt() != null && t.getOrt().toLowerCase().contains(q);
            // Datum in schweizer Format (dd.MM.yyyy) prüfen
            boolean byDatum = t.getDatum() != null && t.getDatum().format(DATE_OUTPUT_FORMAT).contains(q);
            boolean byUhrzeit = t.getUhrzeit() != null && t.getUhrzeit().toString().contains(q);
            boolean byId = String.valueOf(t.getTerminId()).contains(q);
            return byOrt || byDatum || byUhrzeit || byId;
        });
    }

    /**
     * Wählt einen Termin aus und füllt die Eingabefelder
     */
    private void selectTermin(Termin termin) {
        selectedTermin = termin;
        datumPicker.setValue(termin.getDatum());
        uhrzeitField.setText(termin.getUhrzeit().toString());
        ortField.setText(termin.getOrt());
        ferienCheckBox.setSelected(termin.isFerienFlag());

        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    /**
     * Speichert einen neuen Termin
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            if (datumPicker.getValue() == null) {
                DialogUtil.showWarning("Eingabe erforderlich", "Bitte wählen Sie ein Datum aus.");
                return;
            }
            Termin termin = new Termin();
            termin.setDatum(datumPicker.getValue());
            termin.setUhrzeit(LocalTime.parse(uhrzeitField.getText().trim()));
            termin.setOrt(ortField.getText().trim());
            termin.setFerienFlag(ferienCheckBox.isSelected());

            if (terminRepository.save(termin)) {
                DialogUtil.showSuccess("Erfolg", "Termin wurde erfolgreich gespeichert.");
                loadAllTermine();
                handleClear();
            } else {
                DialogUtil.showDatabaseError("Speichern");
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            DialogUtil.showError("Fehler beim Speichern", e.getMessage());
        }
    }

    /**
     * Aktualisiert den ausgewählten Termin
     */
    @FXML
    private void handleUpdate() {
        if (selectedTermin == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Termin aus der Tabelle aus.");
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            if (datumPicker.getValue() == null) {
                DialogUtil.showWarning("Eingabe erforderlich", "Bitte wählen Sie ein Datum aus.");
                return;
            }
            selectedTermin.setDatum(datumPicker.getValue());
            selectedTermin.setUhrzeit(LocalTime.parse(uhrzeitField.getText().trim()));
            selectedTermin.setOrt(ortField.getText().trim());
            selectedTermin.setFerienFlag(ferienCheckBox.isSelected());

            if (terminRepository.update(selectedTermin)) {
                DialogUtil.showSuccess("Erfolg", "Termin wurde erfolgreich aktualisiert.");
                loadAllTermine();
                handleClear();
            } else {
                DialogUtil.showDatabaseError("Aktualisieren");
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            DialogUtil.showError("Fehler beim Aktualisieren", e.getMessage());
        }
    }

    /**
     * Löscht den ausgewählten Termin
     */
    @FXML
    private void handleDelete() {
        if (selectedTermin == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Termin aus der Tabelle aus.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmation(
                "Termin löschen?",
                "Möchten Sie den Termin am " + selectedTermin.getDatum() + " um "
                + selectedTermin.getUhrzeit() + " wirklich löschen?\n\n"
                + "Diese Aktion kann nicht rückgängig gemacht werden!"
        );

        if (confirmed) {
            try {
                if (terminRepository.delete(selectedTermin.getTerminId())) {
                    DialogUtil.showSuccess("Erfolg", "Termin wurde erfolgreich gelöscht.");
                    loadAllTermine();
                    handleClear();
                } else {
                    DialogUtil.showDatabaseError("Löschen");
                }
            } catch (RuntimeException e) {
                DialogUtil.showError("Fehler beim Löschen", e.getMessage());
            }
        }
    }

    /**
     * Leert die Eingabefelder
     */
    @FXML
    private void handleClear() {
        datumPicker.setValue(null);
        uhrzeitField.clear();
        ortField.clear();
        ferienCheckBox.setSelected(false);

        // Reset validation styles
        ValidationUtil.resetStyle(uhrzeitField, ortField);

        selectedTermin = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        terminTable.getSelectionModel().clearSelection();
    }

    /**
     * Filtert Termine nach kommenden Terminen
     */
    @FXML
    private void handleFilterToggle() {
        loadAllTermine();
    }

    /**
     * Datumsbereich geändert
     */
    @FXML
    private void handleDateRangeChanged() {
        loadAllTermine();
    }

    /**
     * Sucht Termine (Platzhalter für erweiterte Suche)
     */
    @FXML
    private void handleSearch() {
        loadAllTermine();
    }

    /**
     * Löscht alle Filter (Suche + Datum)
     */
    @FXML
    private void handleClearFilters() {
        if (searchField != null) {
            searchField.clear();
        }
        if (vonDatumPicker != null) {
            vonDatumPicker.setValue(null);
            if (vonDatumPicker.getEditor() != null) {
                vonDatumPicker.getEditor().clear();
            }
        }
        if (bisDatumPicker != null) {
            bisDatumPicker.setValue(null);
            if (bisDatumPicker.getEditor() != null) {
                bisDatumPicker.getEditor().clear();
            }
        }
        loadAllTermine();
    }

    /**
     * Validiert die Eingaben
     */
    private boolean validateInput() {
        boolean valid = true;

        if (datumPicker.getValue() == null) {
            valid = false;
        }
        if (!ValidationUtil.isValidTimeText(uhrzeitField)) {
            valid = false;
        }
        if (!ValidationUtil.isNotEmpty(ortField)) {
            valid = false;
        }

        if (!valid) {
            DialogUtil.showValidationError("""
                    Bitte füllen Sie alle Pflichtfelder korrekt aus.
                    - Datum darf nicht leer sein
                    - Uhrzeit darf nicht leer sein (Format: HH:MM)
                    - Ort darf nicht leer sein
                    """);
        }

        return valid;
    }

    /**
     * Zurück zum Hauptmenü
     */
    @FXML
    private void handleBack() {
        try {
            App.setSceneRoot("main");
        } catch (IOException e) {
            DialogUtil.showError("Navigation fehlgeschlagen", "Konnte Hauptmenü nicht laden: " + e.getMessage());
        }
    }

    /**
     * Exportiert die aktuell angezeigten Termine als CSV
     */
    @FXML
    private void handleExportCsv() {
        if (terminList.isEmpty()) {
            DialogUtil.showWarning("Keine Daten", "Es gibt keine Termine zum Exportieren.");
            return;
        }

        try {
            Window owner = (terminTable != null && terminTable.getScene() != null) ? terminTable.getScene().getWindow() : null;

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Termine als CSV exportieren");
            chooser.setInitialFileName("termine_export.csv");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien (*.csv)", "*.csv"));
            File exportFile = chooser.showSaveDialog(owner);
            if (exportFile == null) {
                return;
            }

            // Falls Benutzer keinen Suffix angibt, .csv anhängen
            String name = exportFile.getName() != null ? exportFile.getName() : "";
            if (!name.toLowerCase(java.util.Locale.ROOT).endsWith(".csv")) {
                exportFile = new File(exportFile.getParentFile(), name + ".csv");
            }

            if (exportFile.exists()) {
                boolean overwrite = DialogUtil.showConfirmation(
                        "Datei überschreiben?",
                        "Die Datei existiert bereits:\n" + exportFile.getAbsolutePath() + "\n\nMöchten Sie sie überschreiben?"
                );
                if (!overwrite) {
                    return;
                }
            }

            boolean success = ExportUtil.exportCsv(
                    exportFile,
                    terminList,
                    Arrays.asList("Termin-ID", "Datum", "Uhrzeit", "Ort", "Ferien"),
                    termin -> Arrays.asList(
                            String.valueOf(termin.getTerminId()),
                            termin.getDatum() != null ? termin.getDatum().toString() : "",
                            termin.getUhrzeit() != null ? termin.getUhrzeit().toString() : "",
                            termin.getOrt() != null ? termin.getOrt() : "",
                            termin.isFerienFlag() ? "Ja" : "Nein"
                    )
            );

            if (success) {
                DialogUtil.showSuccess("Export erfolgreich",
                        "Termine wurden erfolgreich exportiert nach:\n" + exportFile.getAbsolutePath());
            } else {
                DialogUtil.showError("Export fehlgeschlagen", "Die CSV-Datei konnte nicht erstellt werden.");
            }
        } catch (RuntimeException e) {
            DialogUtil.showError("Export-Fehler", "Fehler beim Exportieren: " + e.getMessage());
        }
    }
}
