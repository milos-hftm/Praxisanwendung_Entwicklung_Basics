package ch.hftm.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model-Klasse für ein Formular
 */
public class Formular {

    private final IntegerProperty formularId = new SimpleIntegerProperty(this, "formularId", 0);
    private final StringProperty typ = new SimpleStringProperty(this, "typ", "");
    private final ObjectProperty<LocalDate> ausgabedatum = new SimpleObjectProperty<>(this, "ausgabedatum");
    private final ObjectProperty<LocalDate> rueckgabedatum = new SimpleObjectProperty<>(this, "rueckgabedatum");
    private final ObjectProperty<FormularStatus> status = new SimpleObjectProperty<>(this, "status", FormularStatus.AUSSTEHEND);
    private final IntegerProperty mitgliedId = new SimpleIntegerProperty(this, "mitgliedId", 0);

    // Konstruktoren
    public Formular() {
    }

    public Formular(int formularId, String typ, LocalDate ausgabedatum, LocalDate rueckgabedatum,
            FormularStatus status, int mitgliedId) {
        this.formularId.set(formularId);
        this.typ.set(typ);
        this.ausgabedatum.set(ausgabedatum);
        this.rueckgabedatum.set(rueckgabedatum);
        this.status.set(status);
        this.mitgliedId.set(mitgliedId);
    }

    // formularId
    public int getFormularId() {
        return formularId.get();
    }

    public void setFormularId(int value) {
        formularId.set(value);
    }

    public IntegerProperty formularIdProperty() {
        return formularId;
    }

    // typ
    public String getTyp() {
        return typ.get();
    }

    public void setTyp(String value) {
        typ.set(value);
    }

    public StringProperty typProperty() {
        return typ;
    }

    // ausgabedatum
    public LocalDate getAusgabedatum() {
        return ausgabedatum.get();
    }

    public void setAusgabedatum(LocalDate value) {
        ausgabedatum.set(value);
    }

    public ObjectProperty<LocalDate> ausgabedatumProperty() {
        return ausgabedatum;
    }

    // rueckgabedatum
    public LocalDate getRueckgabedatum() {
        return rueckgabedatum.get();
    }

    public void setRueckgabedatum(LocalDate value) {
        rueckgabedatum.set(value);
    }

    public ObjectProperty<LocalDate> rueckgabedatumProperty() {
        return rueckgabedatum;
    }

    // status
    public FormularStatus getStatus() {
        return status.get();
    }

    public void setStatus(FormularStatus value) {
        status.set(value);
    }

    public ObjectProperty<FormularStatus> statusProperty() {
        return status;
    }

    // mitgliedId
    public int getMitgliedId() {
        return mitgliedId.get();
    }

    public void setMitgliedId(int value) {
        mitgliedId.set(value);
    }

    public IntegerProperty mitgliedIdProperty() {
        return mitgliedId;
    }

    @Override
    public String toString() {
        return typ.get() + " (" + status.get() + ")";
    }
}
