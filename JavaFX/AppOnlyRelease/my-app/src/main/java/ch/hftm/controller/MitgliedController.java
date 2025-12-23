package ch.hftm.controller;

import java.io.File;
import java.io.IOException;

import ch.hftm.App;
import ch.hftm.model.Mitglied;
import ch.hftm.model.Rolle;
import ch.hftm.persistence.MitgliedRepository;
import ch.hftm.util.DialogUtil;
import ch.hftm.util.ExportUtil;
import ch.hftm.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Controller für die Mitgliederverwaltung
 */
public class MitgliedController {

    @FXML
    private TableView<Mitglied> mitgliedTable;
    @FXML
    private TableColumn<Mitglied, Integer> mitgliedIdColumn;
    @FXML
    private TableColumn<Mitglied, String> vornameColumn;
    @FXML
    private TableColumn<Mitglied, String> nachnameColumn;
    @FXML
    private TableColumn<Mitglied, String> emailColumn;
    @FXML
    private TableColumn<Mitglied, Rolle> rolleColumn;

    @FXML
    private TextField vornameField;
    @FXML
    private TextField nachnameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<Rolle> rolleComboBox;
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

    private MitgliedRepository mitgliedRepository;
    private ObservableList<Mitglied> mitgliedList;
    private FilteredList<Mitglied> filteredMitglieder;
    private SortedList<Mitglied> sortedMitglieder;
    private Mitglied selectedMitglied;

    @FXML
    public void initialize() {
        mitgliedRepository = new MitgliedRepository();
        mitgliedList = FXCollections.observableArrayList();

        // Tabellenspalten konfigurieren
        mitgliedIdColumn.setCellValueFactory(new PropertyValueFactory<>("mitgliedId"));
        vornameColumn.setCellValueFactory(new PropertyValueFactory<>("vorname"));
        nachnameColumn.setCellValueFactory(new PropertyValueFactory<>("nachname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolleColumn.setCellValueFactory(new PropertyValueFactory<>("rolle"));

        // Filtering + Sorting vorbereiten
        filteredMitglieder = new FilteredList<>(mitgliedList, m -> true);
        sortedMitglieder = new SortedList<>(filteredMitglieder);
        sortedMitglieder.comparatorProperty().bind(mitgliedTable.comparatorProperty());
        mitgliedTable.setItems(sortedMitglieder);

        // ComboBox mit Rollen füllen
        rolleComboBox.setItems(FXCollections.observableArrayList(Rolle.values()));
        rolleComboBox.setValue(Rolle.MITGLIED);

        // Selektions-Listener
        mitgliedTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectMitglied(newSelection);
                    }
                }
        );

        // Live-Suche beim Tippen
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
        }

        loadAllMitglieder();
    }

    /**
     * Lädt alle Mitglieder aus der Datenbank
     */
    private void loadAllMitglieder() {
        mitgliedList.clear();
        mitgliedList.addAll(mitgliedRepository.findAll());
        // Nach dem Laden Filter anwenden (z. B. wenn ein Suchtext aktiv ist)
        applyFilter(searchField != null ? searchField.getText() : null);
    }

    /**
     * Wendet einen Volltext-Filter über die wichtigsten Felder an
     */
    private void applyFilter(String query) {
        final String q = query == null ? "" : query.trim().toLowerCase();
        filteredMitglieder.setPredicate(m -> {
            if (q.isEmpty()) {
                return true;
            }
            boolean byVorname = m.getVorname() != null && m.getVorname().toLowerCase().contains(q);
            boolean byNachname = m.getNachname() != null && m.getNachname().toLowerCase().contains(q);
            boolean byEmail = m.getEmail() != null && m.getEmail().toLowerCase().contains(q);
            boolean byRolle = m.getRolle() != null && m.getRolle().name().toLowerCase().contains(q);
            boolean byId = String.valueOf(m.getMitgliedId()).contains(q);
            return byVorname || byNachname || byEmail || byRolle || byId;
        });
    }

    /**
     * Wählt ein Mitglied aus und füllt die Eingabefelder
     */
    private void selectMitglied(Mitglied mitglied) {
        selectedMitglied = mitglied;
        vornameField.setText(mitglied.getVorname());
        nachnameField.setText(mitglied.getNachname());
        emailField.setText(mitglied.getEmail());
        rolleComboBox.setValue(mitglied.getRolle());

        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    /**
     * Speichert ein neues Mitglied
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Mitglied mitglied = new Mitglied();
            mitglied.setVorname(vornameField.getText().trim());
            mitglied.setNachname(nachnameField.getText().trim());
            mitglied.setEmail(emailField.getText().trim());
            mitglied.setRolle(rolleComboBox.getValue());

            if (mitgliedRepository.save(mitglied)) {
                DialogUtil.showSuccess("Erfolg", "Mitglied wurde erfolgreich gespeichert.");
                loadAllMitglieder();
                handleClear();
            } else {
                DialogUtil.showDatabaseError("Speichern");
            }
        } catch (Exception e) {
            DialogUtil.showError("Fehler beim Speichern", e.getMessage());
        }
    }

    /**
     * Aktualisiert das ausgewählte Mitglied
     */
    @FXML
    private void handleUpdate() {
        if (selectedMitglied == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst ein Mitglied aus der Tabelle aus.");
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            selectedMitglied.setVorname(vornameField.getText().trim());
            selectedMitglied.setNachname(nachnameField.getText().trim());
            selectedMitglied.setEmail(emailField.getText().trim());
            selectedMitglied.setRolle(rolleComboBox.getValue());

            if (mitgliedRepository.update(selectedMitglied)) {
                DialogUtil.showSuccess("Erfolg", "Mitglied wurde erfolgreich aktualisiert.");
                loadAllMitglieder();
                handleClear();
            } else {
                DialogUtil.showDatabaseError("Aktualisieren");
            }
        } catch (Exception e) {
            DialogUtil.showError("Fehler beim Aktualisieren", e.getMessage());
        }
    }

    /**
     * Löscht das ausgewählte Mitglied
     */
    @FXML
    private void handleDelete() {
        if (selectedMitglied == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst ein Mitglied aus der Tabelle aus.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmation(
                "Mitglied löschen?",
                "Möchten Sie das Mitglied '" + selectedMitglied.getVorname() + " "
                + selectedMitglied.getNachname() + "' wirklich löschen?\n\n"
                + "Diese Aktion kann nicht rückgängig gemacht werden!"
        );

        if (confirmed) {
            try {
                if (mitgliedRepository.delete(selectedMitglied.getMitgliedId())) {
                    DialogUtil.showSuccess("Erfolg", "Mitglied wurde erfolgreich gelöscht.");
                    loadAllMitglieder();
                    handleClear();
                } else {
                    DialogUtil.showDatabaseError("Löschen");
                }
            } catch (Exception e) {
                DialogUtil.showError("Fehler beim Löschen", e.getMessage());
            }
        }
    }

    /**
     * Leert die Eingabefelder
     */
    @FXML
    private void handleClear() {
        vornameField.clear();
        nachnameField.clear();
        emailField.clear();
        rolleComboBox.setValue(Rolle.MITGLIED);

        // Reset validation styles
        ValidationUtil.resetStyle(vornameField, nachnameField, emailField);
        ValidationUtil.resetStyle(rolleComboBox);

        selectedMitglied = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        mitgliedTable.getSelectionModel().clearSelection();
    }

    /**
     * Sucht Mitglieder
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            // Wenn leer, alle Mitglieder laden und Filter zurücksetzen
            loadAllMitglieder();
        } else {
            // DB-gestützte Suche (case-insensitive über Vorname/Nachname/Email)
            mitgliedList.clear();
            mitgliedList.addAll(mitgliedRepository.search(searchTerm));
            // Zusätzlich lokalen Volltextfilter anwenden (z. B. wenn Rolle/ID enthalten ist)
            applyFilter(searchTerm);
        }
    }

    /**
     * Exportiert die aktuelle Mitgliederliste als CSV
     */
    @FXML
    private void handleExportCsv() {
        try {
            var headers = java.util.List.of("ID", "Vorname", "Nachname", "E-Mail", "Rolle");
            var mappers = java.util.List.of(
                    (java.util.function.Function<Mitglied, String>) (m -> String.valueOf(m.getMitgliedId())),
                    (java.util.function.Function<Mitglied, String>) Mitglied::getVorname,
                    (java.util.function.Function<Mitglied, String>) Mitglied::getNachname,
                    (java.util.function.Function<Mitglied, String>) Mitglied::getEmail,
                    (java.util.function.Function<Mitglied, String>) (m -> m.getRolle() != null ? m.getRolle().name() : "")
            );

            Window owner = (mitgliedTable != null && mitgliedTable.getScene() != null) ? mitgliedTable.getScene().getWindow() : null;
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Mitglieder als CSV exportieren");
            chooser.setInitialFileName("mitglieder.csv");
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
                    mitgliedList,
                    headers,
                    m -> mappers.stream().map(fn -> fn.apply(m)).toList()
            );

            if (success) {
                DialogUtil.showSuccess("CSV-Export", "Datei gespeichert: " + exportFile.getAbsolutePath());
            } else {
                DialogUtil.showError("CSV-Export fehlgeschlagen", "Die CSV-Datei konnte nicht erstellt werden.");
            }
        } catch (Exception e) {
            DialogUtil.showError("CSV-Export fehlgeschlagen", e.getMessage());
        }
    }

    /**
     * Validiert die Eingaben
     */
    private boolean validateInput() {
        boolean valid = true;

        if (!ValidationUtil.isNotEmpty(vornameField)) {
            valid = false;
        }
        if (!ValidationUtil.isNotEmpty(nachnameField)) {
            valid = false;
        }
        if (!ValidationUtil.isValidEmail(emailField)) {
            valid = false;
        }
        if (!ValidationUtil.hasSelection(rolleComboBox)) {
            valid = false;
        }

        if (!valid) {
            DialogUtil.showValidationError("Bitte füllen Sie alle Pflichtfelder korrekt aus.\n"
                    + "- Vorname und Nachname dürfen nicht leer sein\n"
                    + "- E-Mail muss gültig sein (z.B. name@example.com)\n"
                    + "- Rolle muss ausgewählt sein");
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
}
