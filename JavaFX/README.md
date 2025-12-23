# KUD Karadjordje – Verwaltungssoftware (JavaFX)

Desktop-Anwendung zur Verwaltung von Terminen, Mitgliedern, Formularen und Teilnahmen für den Tanzverein KUD Karadjordje Bern. Entwickelt als HFTM-Praxisanwendung mit JavaFX und PostgreSQL.

## 🚀 Quickstart

### Voraussetzungen
- **JDK 21**
- **Maven 3.9+**
- **Docker Desktop** (für PostgreSQL)

### Installation & Start

1. **Datenbank starten:**
   ```bash
   cd datenbank/Transfer-Projekt_Implementierung/relational-databases-orm-java-main
   docker compose -f docker-compose.db.yaml up -d
   ```
   Datenbank läuft dann unter `localhost:5432`, Benutzerdaten: `transferdemo` / `transferdemo`

2. **Anwendung starten:**
   ```bash
   cd JavaFX/AppOnlyRelease/my-app
   mvn clean javafx:run
   ```

### Bedienung (Shortcuts)

- **F11**: Vollbild umschalten (die App startet standardmässig im Vollbild)
- **F12**: Screenshot speichern (Standard: `bericht/screenshots`, Fallback: `~/.kud-karadjordje/screenshots`)

## 📁 Projektstruktur

```
JavaFX/AppOnlyRelease/my-app/       → Hauptanwendung (JavaFX + JDBC)
├── src/main/java/ch/hftm/          → Source Code (Model, View, Controller)
├── src/main/resources/             → FXML-Dateien, CSS, Properties
└── pom.xml                         → Maven-Konfiguration

datenbank/Transfer-Projekt_...      → Referenz-Backend mit DB-Migrationen
├── docker-compose.db.yaml          → PostgreSQL Setup
└── src/main/resources/db/migration → Flyway-Migrationen (Schema + Daten)

bericht/                            → Dokumentation & Screenshots
├── Projekt_Dokumentation.md
└── screenshots/
```

## 🗄️ Datenbank

**Schema:**
- `mitglied` – Vereinsmitglieder (Vorname, Nachname, E-Mail, Rolle)
- `termin` – Trainingstermine & Veranstaltungen (Datum, Uhrzeit, Ort)
- `formular` – Anmeldungen und Fragebögen (Status-Tracking)
- `teilnahme` – Zusage/Absage-Verwaltung pro Mitglied & Termin

Migrationen werden automatisch beim Compose-Start ausgeführt (Flyway).

## 💡 Features

- **Termine verwalten:** Erstellen, bearbeiten, löschen
- **Mitglieder erfassen:** Stammdaten, Rollen (Mitglied, Trainer, Admin)
- **Teilnahmen erfassen:** Zusagen/Absagen mit Formular-Tracking
- **Suchen & Filtern:** Übersichtliche Tabellenansichten mit Such- und Sortierfunktion
- **Validierung:** Eingabeprüfung (Pflichtfelder, Datenformate)

## ⚙️ Konfiguration

**DB-Verbindung (JavaFX):**

Standardwerte liegen in `JavaFX/AppOnlyRelease/my-app/src/main/resources/db.properties`.

Optional kannst du sie überschreiben über:
- **Java System Properties:** `-Ddb.url=... -Ddb.user=... -Ddb.password=...`
- **Environment-Variablen:** `KUD_DB_URL`, `KUD_DB_USER`, `KUD_DB_PASSWORD`

## 🐛 Troubleshooting

| Problem | Lösung |
|---------|--------|
| „no POM in this directory" | Ins Verzeichnis `JavaFX/AppOnlyRelease/my-app` wechseln |
| Datenbank nicht erreichbar | `docker compose up -d` aus dem korrekten Verzeichnis ausführen |


## 📚 Dokumentation

- **Detaillierte Projekt-Doku:** `bericht/Projekt_Dokumentation.md`
- **Datenbankschema-PDF:** `datenbank/Datenbankschema-Dokumentation.pdf`

## 📝 Lizenz

HFTM-Praxisanwendung. Nutzung im Rahmen der Informatiker-HF-Ausbildung.
