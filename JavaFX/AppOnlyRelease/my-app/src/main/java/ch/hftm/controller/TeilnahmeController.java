package ch.hftm.controller;

import java.io.IOException;

import ch.hftm.App;
import ch.hftm.model.Teilnahme;
import ch.hftm.model.TeilnahmeStatus;
import ch.hftm.persistence.TeilnahmeRepository;
import ch.hftm.util.DialogUtil;
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

/**
 * Controller für die Teilnahmeverwaltung
 */
@SuppressWarnings("unused") // FXML bindet Felder und Handler methoden zur Laufzeit
public class TeilnahmeController {

    @FXML
    private TableView<Teilnahme> teilnahmeTable;
    @FXML
    private TableColumn<Teilnahme, Integer> teilnahmeIdColumn;
    @FXML
    private TableColumn<Teilnahme, Integer> mitgliedIdColumn;
    @FXML
    private TableColumn<Teilnahme, Integer> terminIdColumn;
    @FXML
    private TableColumn<Teilnahme, Integer> formularIdColumn;
    @FXML
    private TableColumn<Teilnahme, TeilnahmeStatus> statusColumn;

    @FXML
    private TextField mitgliedIdField;
    @FXML
    private TextField terminIdField;
    @FXML
    private TextField formularIdField;
    @FXML
    private ComboBox<TeilnahmeStatus> statusCombo;

    @FXML
    private Button saveButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    @FXML
    private TextField searchMitgliedField;
    @FXML
    private TextField searchTerminField;
    @FXML
    private TextField searchField;

    private final TeilnahmeRepository repo = new TeilnahmeRepository();
    private ObservableList<Teilnahme> teilnahmeList;
    private FilteredList<Teilnahme> filteredTeilnahmen;
    private SortedList<Teilnahme> sortedTeilnahmen;
    private Teilnahme selected;

    @FXML
    public void initialize() {
        teilnahmeList = FXCollections.observableArrayList();
        statusCombo.setItems(FXCollections.observableArrayList(TeilnahmeStatus.values()));

        teilnahmeIdColumn.setCellValueFactory(new PropertyValueFactory<>("teilnahmeId"));
        mitgliedIdColumn.setCellValueFactory(new PropertyValueFactory<>("mitgliedId"));
        terminIdColumn.setCellValueFactory(new PropertyValueFactory<>("terminId"));
        formularIdColumn.setCellValueFactory(new PropertyValueFactory<>("formularId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        filteredTeilnahmen = new FilteredList<>(teilnahmeList, t -> true);
        sortedTeilnahmen = new SortedList<>(filteredTeilnahmen);
        sortedTeilnahmen.comparatorProperty().bind(teilnahmeTable.comparatorProperty());
        teilnahmeTable.setItems(sortedTeilnahmen);
        teilnahmeTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                select(n);
            }
        });

        // Live Filter
        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        }
        if (searchMitgliedField != null) {
            searchMitgliedField.textProperty().addListener((obs, o, n) -> applyFilter());
        }
        if (searchTerminField != null) {
            searchTerminField.textProperty().addListener((obs, o, n) -> applyFilter());
        }

        loadAll();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void loadAll() {
        teilnahmeList.clear();
        teilnahmeList.addAll(repo.findAll());
        applyFilter();
    }

    /**
     * Wendet einen kombinierten Volltext- und Feldfilter an
     */
    @FXML
    public void applyFilter() {
        final String qAll = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";
        final String qMit = (searchMitgliedField != null && searchMitgliedField.getText() != null) ? searchMitgliedField.getText().trim().toLowerCase() : "";
        final String qTer = (searchTerminField != null && searchTerminField.getText() != null) ? searchTerminField.getText().trim().toLowerCase() : "";

        filteredTeilnahmen.setPredicate(t -> {
            // Feldbasierte Filter (Mitglied/Termin) vorrangig
            if (!qMit.isEmpty() && !String.valueOf(t.getMitgliedId()).contains(qMit)) {
                return false;
            }
            if (!qTer.isEmpty() && !String.valueOf(t.getTerminId()).contains(qTer)) {
                return false;
            }

            if (qAll.isEmpty()) {
                return true;
            }
            boolean byMitglied = String.valueOf(t.getMitgliedId()).contains(qAll);
            boolean byTermin = String.valueOf(t.getTerminId()).contains(qAll);
            boolean byFormular = String.valueOf(t.getFormularId()).contains(qAll);
            boolean byStatus = t.getStatus() != null && t.getStatus().name().toLowerCase().contains(qAll);
            boolean byId = String.valueOf(t.getTeilnahmeId()).contains(qAll);
            return byMitglied || byTermin || byFormular || byStatus || byId;
        });
    }

    private void select(Teilnahme t) {
        selected = t;
        mitgliedIdField.setText(String.valueOf(t.getMitgliedId()));
        terminIdField.setText(String.valueOf(t.getTerminId()));
        formularIdField.setText(t.getFormularId() > 0 ? String.valueOf(t.getFormularId()) : "");
        statusCombo.setValue(t.getStatus());
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Teilnahme t = new Teilnahme();
            t.setMitgliedId(Integer.parseInt(mitgliedIdField.getText().trim()));
            t.setTerminId(Integer.parseInt(terminIdField.getText().trim()));
            String fid = formularIdField.getText().trim();
            if (!fid.isEmpty()) {
                t.setFormularId(Integer.parseInt(fid));
            }
            t.setStatus(statusCombo.getValue());
            if (repo.save(t)) {
                DialogUtil.showSuccess("Erfolg", "Teilnahme wurde erfolgreich gespeichert.");
                loadAll();
                handleClear();
            } else {
                DialogUtil.showDatabaseError("Speichern");
            }
        } catch (NumberFormatException e) {
            DialogUtil.showError("Fehler beim Speichern", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selected == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst eine Teilnahme aus der Tabelle aus.");
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            selected.setMitgliedId(Integer.parseInt(mitgliedIdField.getText().trim()));
            selected.setTerminId(Integer.parseInt(terminIdField.getText().trim()));
            String fid = formularIdField.getText().trim();
            if (!fid.isEmpty()) {
                selected.setFormularId(Integer.parseInt(fid));
            } else {
                selected.setFormularId(0);
            }
            selected.setStatus(statusCombo.getValue());
            if (repo.update(selected)) {
                DialogUtil.showSuccess("Erfolg", "Teilnahme wurde erfolgreich aktualisiert.");
                loadAll();
                handleClear();
            } else {
                DialogUtil.showDatabaseError("Aktualisieren");
            }
        } catch (NumberFormatException e) {
            DialogUtil.showError("Fehler beim Aktualisieren", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selected == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst eine Teilnahme aus der Tabelle aus.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmation(
                "Teilnahme löschen?",
                "Möchten Sie die Teilnahme (Mitglied-ID: " + selected.getMitgliedId()
                + ", Termin-ID: " + selected.getTerminId() + ") wirklich löschen?\n\n"
                + "Diese Aktion kann nicht rückgängig gemacht werden!"
        );

        if (confirmed) {
            try {
                if (repo.delete(selected.getTeilnahmeId())) {
                    DialogUtil.showSuccess("Erfolg", "Teilnahme wurde erfolgreich gelöscht.");
                    loadAll();
                    handleClear();
                } else {
                    DialogUtil.showDatabaseError("Löschen");
                }
            } catch (RuntimeException e) {
                DialogUtil.showError("Fehler beim Löschen", e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        mitgliedIdField.clear();
        terminIdField.clear();
        formularIdField.clear();
        statusCombo.setValue(null);

        // Reset validation styles
        ValidationUtil.resetStyle(mitgliedIdField, terminIdField, formularIdField);
        ValidationUtil.resetStyle(statusCombo);

        selected = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        teilnahmeTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSearchMitglied() {
        String q = searchMitgliedField.getText();
        if (q == null || q.isBlank()) {
            loadAll();
        } else {
            try {
                teilnahmeList.clear();
                teilnahmeList.addAll(repo.findByMitglied(Integer.parseInt(q.trim())));
                applyFilter();
            } catch (NumberFormatException ex) {
                DialogUtil.showWarning("Ungültige Eingabe", "Mitglied-ID muss eine Zahl sein.");
            }
        }
    }

    @FXML
    private void handleSearchTermin() {
        String q = searchTerminField.getText();
        if (q == null || q.isBlank()) {
            loadAll();
        } else {
            try {
                teilnahmeList.clear();
                teilnahmeList.addAll(repo.findByTermin(Integer.parseInt(q.trim())));
                applyFilter();
            } catch (NumberFormatException ex) {
                DialogUtil.showWarning("Ungültige Eingabe", "Termin-ID muss eine Zahl sein.");
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.setSceneRoot("main");
        } catch (IOException e) {
            DialogUtil.showError("Navigation fehlgeschlagen", "Konnte Hauptmenü nicht laden: " + e.getMessage());
        }
    }

    /**
     * Validiert die Eingaben
     */
    private boolean validateInput() {
        boolean valid = true;

        if (!ValidationUtil.isNotEmpty(mitgliedIdField)) {
            valid = false;
        }
        if (!ValidationUtil.isNotEmpty(terminIdField)) {
            valid = false;
        }
        if (!ValidationUtil.hasSelection(statusCombo)) {
            valid = false;
        }

        if (!valid) {
            DialogUtil.showValidationError("""
                    Bitte füllen Sie alle Pflichtfelder korrekt aus.
                    - Mitglied-ID muss angegeben sein
                    - Termin-ID muss angegeben sein
                    - Status muss ausgewählt sein
                    """);
        }

        return valid;
    }
}
