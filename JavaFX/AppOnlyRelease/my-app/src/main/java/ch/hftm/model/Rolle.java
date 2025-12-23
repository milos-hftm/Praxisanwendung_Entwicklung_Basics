package ch.hftm.model;

/**
 * Enum für die Rolle eines Mitglieds im Verein
 */
public enum Rolle {
    MITGLIED("Mitglied"),
    TRAINER("Trainer"),
    ADMIN("Admin");

    private final String displayName;

    Rolle(String displayName) {
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
