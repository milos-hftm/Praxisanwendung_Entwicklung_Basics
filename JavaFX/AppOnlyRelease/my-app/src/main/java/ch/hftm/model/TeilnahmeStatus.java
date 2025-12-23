package ch.hftm.model;

/**
 * Enum für den Status einer Teilnahme an einem Termin
 */
public enum TeilnahmeStatus {
    ZUGESAGT("Zugesagt"),
    ABGESAGT("Abgesagt"),
    ABWESEND("Abwesend");

    private final String displayName;

    TeilnahmeStatus(String displayName) {
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
