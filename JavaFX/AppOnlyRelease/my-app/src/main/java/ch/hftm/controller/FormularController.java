package ch.hftm.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Locale;

import ch.hftm.App;
import ch.hftm.model.Formular;
import ch.hftm.model.FormularStatus;
import ch.hftm.persistence.FormularRepository;
import ch.hftm.util.DialogUtil;
import ch.hftm.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Controller für die Formularverwaltung
 */
@SuppressWarnings("unused") // FXML bindet Felder und Handler methoden zur Laufzeit
public class FormularController {

    @FXML
    private TableView<Formular> formularTable;
    @FXML
    private TableColumn<Formular, Integer> formularIdColumn;
    @FXML
    private TableColumn<Formular, String> typColumn;
    @FXML
    private TableColumn<Formular, LocalDate> ausgabedatumColumn;
    @FXML
    private TableColumn<Formular, LocalDate> rueckgabedatumColumn;
    @FXML
    private TableColumn<Formular, FormularStatus> statusColumn;
    @FXML
    private TableColumn<Formular, Integer> mitgliedIdColumn;

    @FXML
    private TextField typField;
    @FXML
    private DatePicker ausgabedatumPicker;
    @FXML
    private DatePicker rueckgabedatumPicker;
    @FXML
    private ComboBox<FormularStatus> statusCombo;
    @FXML
    private TextField mitgliedIdField;

    @FXML
    private Button saveButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    @FXML
    private Button pdfOpenButton;
    @FXML
    private Button pdfAttachButton;
    @FXML
    private Label pdfStatusLabel;

    @FXML
    private TextField searchField;
    @FXML
    private CheckBox nurOffeneCheckBox;

    private final FormularRepository repo = new FormularRepository();
    private ObservableList<Formular> formularList;
    private FilteredList<Formular> filteredFormulare;
    private SortedList<Formular> sortedFormulare;
    private Formular selected;

    /**
     * Lokaler Ablage-Ordner für Formular-PDFs (pro Benutzerprofil).
     */
    private static final String DOC_DIR_NAME = ".kud-karadjordje";
    private static final String FORM_DOC_SUBDIR = "formulare";

    @FXML
    public void initialize() {
        formularList = FXCollections.observableArrayList();
        statusCombo.setItems(FXCollections.observableArrayList(FormularStatus.values()));

        formularIdColumn.setCellValueFactory(new PropertyValueFactory<>("formularId"));
        typColumn.setCellValueFactory(new PropertyValueFactory<>("typ"));
        ausgabedatumColumn.setCellValueFactory(new PropertyValueFactory<>("ausgabedatum"));
        rueckgabedatumColumn.setCellValueFactory(new PropertyValueFactory<>("rueckgabedatum"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        mitgliedIdColumn.setCellValueFactory(new PropertyValueFactory<>("mitgliedId"));

        // Filter + Sort
        filteredFormulare = new FilteredList<>(formularList, f -> true);
        sortedFormulare = new SortedList<>(filteredFormulare);
        sortedFormulare.comparatorProperty().bind(formularTable.comparatorProperty());
        formularTable.setItems(sortedFormulare);
        formularTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                select(n);
            }
        });

        // Live-Suche
        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        }
        if (nurOffeneCheckBox != null) {
            nurOffeneCheckBox.selectedProperty().addListener((obs, o, n) -> loadAll());
        }

        loadAll();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // PDF-Bedienung initial deaktivieren (erst bei Auswahl aktiv)
        setPdfUiState(false, false, null);
    }

    private void loadAll() {
        formularList.clear();
        if (nurOffeneCheckBox != null && nurOffeneCheckBox.isSelected()) {
            formularList.addAll(repo.findAll().stream().filter(f -> f.getStatus() == FormularStatus.AUSSTEHEND).toList());
        } else {
            formularList.addAll(repo.findAll());
        }
        applyFilter();
    }

    /**
     * Volltext-Filter über Typ/Status/Datum/Mitglied-ID
     */
    private void applyFilter() {
        final String q = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";
        filteredFormulare.setPredicate(f -> {
            if (q.isEmpty()) {
                return true;
            }
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
            boolean byTyp = f.getTyp() != null && f.getTyp().toLowerCase().contains(q);
            boolean byStatus = f.getStatus() != null && f.getStatus().name().toLowerCase().contains(q);
            boolean byAusgabe = f.getAusgabedatum() != null && f.getAusgabedatum().format(formatter).contains(q);
            boolean byRueckgabe = f.getRueckgabedatum() != null && f.getRueckgabedatum().format(formatter).contains(q);
            boolean byMitglied = String.valueOf(f.getMitgliedId()).contains(q);
            boolean byId = String.valueOf(f.getFormularId()).contains(q);
            return byTyp || byStatus || byAusgabe || byRueckgabe || byMitglied || byId;
        });
    }

    private void select(Formular f) {
        selected = f;
        typField.setText(f.getTyp());
        ausgabedatumPicker.setValue(f.getAusgabedatum());
        rueckgabedatumPicker.setValue(f.getRueckgabedatum());
        statusCombo.setValue(f.getStatus());
        mitgliedIdField.setText(String.valueOf(f.getMitgliedId()));
        updateButton.setDisable(false);
        deleteButton.setDisable(false);

        updatePdfStatusForSelection();
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Formular f = new Formular();
            f.setTyp(typField.getText().trim());
            f.setAusgabedatum(ausgabedatumPicker.getValue());
            f.setRueckgabedatum(rueckgabedatumPicker.getValue());
            f.setStatus(statusCombo.getValue());
            f.setMitgliedId(Integer.parseInt(mitgliedIdField.getText().trim()));
            if (repo.save(f)) {
                DialogUtil.showSuccess("Erfolg", "Formular wurde erfolgreich gespeichert.");
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
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst ein Formular aus der Tabelle aus.");
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            selected.setTyp(typField.getText().trim());
            selected.setAusgabedatum(ausgabedatumPicker.getValue());
            selected.setRueckgabedatum(rueckgabedatumPicker.getValue());
            selected.setStatus(statusCombo.getValue());
            selected.setMitgliedId(Integer.parseInt(mitgliedIdField.getText().trim()));
            if (repo.update(selected)) {
                DialogUtil.showSuccess("Erfolg", "Formular wurde erfolgreich aktualisiert.");
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
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst ein Formular aus der Tabelle aus.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmation(
                "Formular löschen?",
                "Möchten Sie das Formular '" + selected.getTyp() + "' wirklich löschen?\n\n"
                + "Diese Aktion kann nicht rückgängig gemacht werden!"
        );

        if (confirmed) {
            try {
                if (repo.delete(selected.getFormularId())) {
                    DialogUtil.showSuccess("Erfolg", "Formular wurde erfolgreich gelöscht.");
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
        typField.clear();
        ausgabedatumPicker.setValue(null);
        rueckgabedatumPicker.setValue(null);
        statusCombo.setValue(null);
        mitgliedIdField.clear();

        // Reset validation styles
        ValidationUtil.resetStyle(typField, mitgliedIdField);
        ValidationUtil.resetStyle(ausgabedatumPicker, rueckgabedatumPicker);
        ValidationUtil.resetStyle(statusCombo);

        selected = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        formularTable.getSelectionModel().clearSelection();

        setPdfUiState(false, false, null);
    }

    @FXML
    private void handlePdfOpen() {
        if (selected == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst ein Formular aus der Tabelle aus.");
            return;
        }

        Path pdf = getPdfPathFor(selected);
        if (pdf == null) {
            DialogUtil.showError("PDF öffnen", "Konnte den Ablagepfad für Dokumente nicht bestimmen.");
            return;
        }

        if (!Files.exists(pdf)) {
            DialogUtil.showWarning("Kein PDF hinterlegt", "Für dieses Formular ist noch kein PDF gespeichert.\n\n"
                    + "Tipp: Klicke auf 'PDF hinzufügen' und wähle eine Datei aus.");
            return;
        }

        try {
            if (!Desktop.isDesktopSupported()) {
                DialogUtil.showError("PDF öffnen", "Desktop-Integration ist auf diesem System nicht verfügbar.\nDatei: " + pdf);
                return;
            }
            Desktop.getDesktop().open(pdf.toFile());
        } catch (Exception e) {
            DialogUtil.showError("PDF öffnen", "Konnte PDF nicht öffnen: " + e.getMessage());
        }
    }

    @FXML
    private void handlePdfAttach() {
        if (selected == null) {
            DialogUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst ein Formular aus der Tabelle aus.");
            return;
        }
        if (selected.getFormularId() <= 0) {
            DialogUtil.showWarning("Ungültige ID", "Dieses Formular hat keine gültige ID. Bitte speichern Sie es zuerst.");
            return;
        }

        Window owner = getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("PDF für Formular auswählen");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)", "*.pdf"));
        var source = chooser.showOpenDialog(owner);
        if (source == null) {
            return;
        }

        String name = source.getName() != null ? source.getName() : "";
        if (!name.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            DialogUtil.showValidationError("Bitte wählen Sie eine PDF-Datei (.pdf) aus.");
            return;
        }

        Path target = getPdfPathFor(selected);
        if (target == null) {
            DialogUtil.showError("PDF hinzufügen", "Konnte den Ablagepfad für Dokumente nicht bestimmen.");
            return;
        }

        try {
            Files.createDirectories(target.getParent());

            if (Files.exists(target)) {
                boolean overwrite = DialogUtil.showConfirmation(
                        "Vorhandenes PDF überschreiben?",
                        "Für dieses Formular existiert bereits ein PDF.\n\nMöchten Sie es überschreiben?"
                );
                if (!overwrite) {
                    return;
                }
            }

            Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            DialogUtil.showSuccess("Erfolg", "PDF wurde gespeichert.\n\nAblage: " + target);
            updatePdfStatusForSelection();
        } catch (Exception e) {
            DialogUtil.showError("PDF hinzufügen", "Konnte PDF nicht speichern: " + e.getMessage());
        }
    }

    private void updatePdfStatusForSelection() {
        if (selected == null) {
            setPdfUiState(false, false, null);
            return;
        }

        Path pdf = getPdfPathFor(selected);
        boolean canAttach = selected.getFormularId() > 0;
        boolean hasPdf = pdf != null && Files.exists(pdf);
        String label = (pdf == null)
                ? "PDF: (Pfad nicht verfügbar)"
                : (hasPdf ? "PDF: vorhanden" : "PDF: nicht vorhanden");

        setPdfUiState(hasPdf, canAttach, label);
    }

    private void setPdfUiState(boolean canOpen, boolean canAttach, String statusText) {
        if (pdfOpenButton != null) {
            pdfOpenButton.setDisable(!canOpen);
        }
        if (pdfAttachButton != null) {
            pdfAttachButton.setDisable(!canAttach);
        }
        if (pdfStatusLabel != null) {
            pdfStatusLabel.setText(statusText != null ? statusText : "PDF: -");
        }
    }

    private Window getWindow() {
        if (formularTable != null && formularTable.getScene() != null) {
            return formularTable.getScene().getWindow();
        }
        return null;
    }

    /**
     * Liefert den Zielpfad, unter dem das PDF zu einem Formular gespeichert
     * wird. Es wird bewusst keine DB-Spalte benötigt; die Zuordnung erfolgt
     * über die Formular-ID.
     */
    private Path getPdfPathFor(Formular f) {
        if (f == null) {
            return null;
        }

        String home = System.getProperty("user.home");
        if (home == null || home.isBlank()) {
            return null;
        }

        Path dir = Paths.get(home, DOC_DIR_NAME, FORM_DOC_SUBDIR);
        return dir.resolve("formular_" + f.getFormularId() + ".pdf");
    }

    @FXML
    private void handleSearch() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            loadAll();
        } else {
            // Repo-Suche nach Typ laden und danach lokalen Volltextfilter anwenden
            formularList.clear();
            formularList.addAll(repo.searchByTyp(q.trim()));
            applyFilter();
        }
    }

    @FXML
    private void handleFilterToggle() {
        loadAll();
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

        if (!ValidationUtil.isNotEmpty(typField)) {
            valid = false;
        }
        if (!ValidationUtil.hasDate(ausgabedatumPicker)) {
            valid = false;
        }
        // Rückgabedatum ist optional: keine Pflichtvalidierung
        if (!ValidationUtil.hasSelection(statusCombo)) {
            valid = false;
        }
        if (!ValidationUtil.isNotEmpty(mitgliedIdField)) {
            valid = false;
        }

        if (!valid) {
            DialogUtil.showValidationError("""
                    Bitte füllen Sie alle Pflichtfelder korrekt aus.
                    - Typ darf nicht leer sein
                    - Ausgabedatum muss gesetzt sein
                    - Status muss ausgewählt sein
                    - Mitglied-ID muss angegeben sein
                    (Hinweis: Rückgabedatum ist optional)
                    """);
        }

        return valid;
    }
}
