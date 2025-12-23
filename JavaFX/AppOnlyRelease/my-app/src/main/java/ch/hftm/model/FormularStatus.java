package ch.hftm.model;

/**
 * Enum für den Status eines Formulars
 */
public enum FormularStatus {
    AUSSTEHEND("Ausstehend"),
    EINGEREICHT("Eingereicht"),
    GEPRUEFT("Geprüft");

    private final String displayName;

    FormularStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
