package ch.hftm.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model-Klasse für einen Termin (Training oder Veranstaltung)
 */
public class Termin {

    private final IntegerProperty terminId = new SimpleIntegerProperty(this, "terminId", 0);
    private final ObjectProperty<LocalDate> datum = new SimpleObjectProperty<>(this, "datum");
    private final ObjectProperty<LocalTime> uhrzeit = new SimpleObjectProperty<>(this, "uhrzeit");
    private final StringProperty ort = new SimpleStringProperty(this, "ort", "");
    private final BooleanProperty ferienFlag = new SimpleBooleanProperty(this, "ferienFlag", false);

    // Konstruktoren
    public Termin() {
    }

    public Termin(int terminId, LocalDate datum, LocalTime uhrzeit, String ort, boolean ferienFlag) {
        this.terminId.set(terminId);
        this.datum.set(datum);
        this.uhrzeit.set(uhrzeit);
        this.ort.set(ort);
        this.ferienFlag.set(ferienFlag);
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

    // datum
    public LocalDate getDatum() {
        return datum.get();
    }

    public void setDatum(LocalDate value) {
        datum.set(value);
    }

    public ObjectProperty<LocalDate> datumProperty() {
        return datum;
    }

    // uhrzeit
    public LocalTime getUhrzeit() {
        return uhrzeit.get();
    }

    public void setUhrzeit(LocalTime value) {
        uhrzeit.set(value);
    }

    public ObjectProperty<LocalTime> uhrzeitProperty() {
        return uhrzeit;
    }

    // ort
    public String getOrt() {
        return ort.get();
    }

    public void setOrt(String value) {
        ort.set(value);
    }

    public StringProperty ortProperty() {
        return ort;
    }

    // ferienFlag
    public boolean isFerienFlag() {
        return ferienFlag.get();
    }

    public void setFerienFlag(boolean value) {
        ferienFlag.set(value);
    }

    public BooleanProperty ferienFlagProperty() {
        return ferienFlag;
    }

    @Override
    public String toString() {
        return datum.get() + " " + uhrzeit.get() + " - " + ort.get();
    }
}
