package ch.hftm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Model-Klasse für eine Teilnahme (Verknüpfung Mitglied-Termin)
 */
public class Teilnahme {

    private final IntegerProperty teilnahmeId = new SimpleIntegerProperty(this, "teilnahmeId", 0);
    private final IntegerProperty mitgliedId = new SimpleIntegerProperty(this, "mitgliedId", 0);
    private final IntegerProperty terminId = new SimpleIntegerProperty(this, "terminId", 0);
    private final IntegerProperty formularId = new SimpleIntegerProperty(this, "formularId", 0);
    private final ObjectProperty<TeilnahmeStatus> status = new SimpleObjectProperty<>(this, "status", TeilnahmeStatus.ZUGESAGT);

    // Konstruktoren
    public Teilnahme() {
    }

    public Teilnahme(int teilnahmeId, int mitgliedId, int terminId, Integer formularId, TeilnahmeStatus status) {
        this.teilnahmeId.set(teilnahmeId);
        this.mitgliedId.set(mitgliedId);
        this.terminId.set(terminId);
        if (formularId != null) {
            this.formularId.set(formularId);
        }
        this.status.set(status);
    }

    // teilnahmeId
    public int getTeilnahmeId() {
        return teilnahmeId.get();
    }

    public void setTeilnahmeId(int value) {
        teilnahmeId.set(value);
    }

    public IntegerProperty teilnahmeIdProperty() {
        return teilnahmeId;
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

    // terminId
    public int getTerminId() {
        return terminId.get();
    }

    public void setTerminId(int value) {
        terminId.set(value);
    }

    public IntegerProperty terminIdProperty() {
        return terminId;
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

    // status
    public TeilnahmeStatus getStatus() {
        return status.get();
    }

    public void setStatus(TeilnahmeStatus value) {
        status.set(value);
    }

    public ObjectProperty<TeilnahmeStatus> statusProperty() {
        return status;
    }

    @Override
    public String toString() {
        return "Teilnahme #" + teilnahmeId.get() + " - " + status.get();
    }
}
