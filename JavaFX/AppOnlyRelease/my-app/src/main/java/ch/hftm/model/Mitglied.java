package ch.hftm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model-Klasse für ein Mitglied des Tanzvereins
 */
public class Mitglied {

    private final IntegerProperty mitgliedId = new SimpleIntegerProperty(this, "mitgliedId", 0);
    private final StringProperty vorname = new SimpleStringProperty(this, "vorname", "");
    private final StringProperty nachname = new SimpleStringProperty(this, "nachname", "");
    private final StringProperty email = new SimpleStringProperty(this, "email", "");
    private final ObjectProperty<Rolle> rolle = new SimpleObjectProperty<>(this, "rolle", Rolle.MITGLIED);

    // Konstruktoren
    public Mitglied() {
    }

    public Mitglied(int mitgliedId, String vorname, String nachname, String email, Rolle rolle) {
        this.mitgliedId.set(mitgliedId);
        this.vorname.set(vorname);
        this.nachname.set(nachname);
        this.email.set(email);
        this.rolle.set(rolle);
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

    // vorname
    public String getVorname() {
        return vorname.get();
    }

    public void setVorname(String value) {
        vorname.set(value);
    }

    public StringProperty vornameProperty() {
        return vorname;
    }

    // nachname
    public String getNachname() {
        return nachname.get();
    }

    public void setNachname(String value) {
        nachname.set(value);
    }

    public StringProperty nachnameProperty() {
        return nachname;
    }

    // email
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // rolle
    public Rolle getRolle() {
        return rolle.get();
    }

    public void setRolle(Rolle value) {
        rolle.set(value);
    }

    public ObjectProperty<Rolle> rolleProperty() {
        return rolle;
    }

    @Override
    public String toString() {
        return vorname.get() + " " + nachname.get() + " (" + email.get() + ")";
    }
}
