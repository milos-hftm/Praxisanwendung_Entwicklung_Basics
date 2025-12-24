# Praxisanwendung Entwicklung Basics – KUD Karadjordje Bern (Termin- & Formularverwaltung)

Diese Arbeit dokumentiert die **Einzelarbeit** im Rahmen der Praxisanwendung **„Entwicklung Basics“** an der HF Informatik (HFTM). Ziel ist die Entwicklung einer **JavaFX Desktop-Applikation** zur zentralen Verwaltung von **Tanztrainingsterminen**, **Mitgliedern**, **Teilnahmen** und **digitalen Formularen** für den Verein **KUD Karadjordje Bern**.

> Webseite (noch in Bearbeitung): https://kud-verein.vercel.app/

---

## Inhaltsverzeichnis
- [Projektbeschreibung](#projektbeschreibung)
- [Beteiligte Kurse](#beteiligte-kurse)
- [Scope & Status](#scope--status)
- [Systemvoraussetzungen](#systemvoraussetzungen)
- [Ordnerstruktur](#ordnerstruktur)
- [Quickstart: JavaFX-App](#quickstart-javafx-app)
- [Quickstart: Datenbank](#quickstart-datenbank)
- [Datenmodell (Kurzueberblick)](#datenmodell-kurzueberblick)
- [Features](#features)
- [Konfiguration](#konfiguration)
- [Troubleshooting](#troubleshooting)
- [Artefakte & Dokumente](#artefakte--dokumente)
- [Geplante Erweiterungen](#geplante-erweiterungen)
- [Mitwirken](#mitwirken)
- [Lizenz](#lizenz)

---

## Projektbeschreibung
Die Anwendung dient der zentralen Verwaltung von:

- **Terminen** (Training/Veranstaltungen)
- **Mitgliedern** (Stammdaten & Rollen)
- **Teilnahmen** (Zusage/Absage pro Mitglied & Termin)
- **Formularen** (z. B. Anmeldung/Feedback, Status-Tracking)

---

## Beteiligte Kurse
- System Modelling
- Requirements Engineering
- Relational Databases
- Java Programming

---

## Scope & Status
- ✅ **Erarbeitet**: Anforderungen, UML/Use-Cases, relationales Schema, SQL/Seed-Daten, Dokumentation
- ✅ **Implementiert**: JavaFX Desktop-App (Maven) inkl. DB-Anbindung (PostgreSQL) und CRUD-Grundfunktionen über GUI

---

## Systemvoraussetzungen
- **JDK 21**
- **Maven 3.9+**
- **Docker Desktop** (empfohlen für PostgreSQL via `docker compose`)
- Optional (falls du ohne Docker arbeiten willst): lokales PostgreSQL + `psql`

---

## Ordnerstruktur
```text
.
├── JavaFX/
│   └── AppOnlyRelease/
│       └── my-app/                          → JavaFX-App (Maven)
│           ├── src/main/java/ch/hftm/        → Source Code (Model/View/Controller)
│           ├── src/main/resources/           → FXML, CSS, Properties
│           └── pom.xml
├── bericht/
│   ├── Technischer_Bericht_EntwicklungBasics.pdf
│   └── Fachuebergreifendes Transfer-Projekt.pdf
│   └── screenshots
└── datenbank/
    ├── Datenbankschema-Dokumentation.pdf
    ├── tanzverein_datenbank.sql
    ├── seed.sql
    └── Transfer-Projekt_Implementierung/
        └── relational-databases-orm-java-main/
            ├── docker-compose.db.yaml        → PostgreSQL Setup + Migrationen (Flyway)
            └── src/main/resources/db/migration
```

---

## Quickstart: JavaFX-App

### 1) Datenbank vorbereiten
Siehe Kapitel **[Quickstart: Datenbank](#quickstart-datenbank)**.

### 2) Anwendung starten
```bash
cd JavaFX/AppOnlyRelease/my-app
mvn clean javafx:run
```

### Bedienung (Shortcuts)
- **F11**: Vollbild umschalten (die App startet standardmässig im Vollbild)

---

## Quickstart: Datenbank

### Variante A (empfohlen): PostgreSQL per Docker Compose + Flyway
Diese Variante startet PostgreSQL und führt Migrationen automatisch aus (Flyway).

```bash
cd datenbank/Transfer-Projekt_Implementierung/relational-databases-orm-java-main
docker compose -f docker-compose.db.yaml up -d
```

Datenbank läuft danach unter:
- Host/Port: `localhost:5432`
- Benutzer/Passwort: `transferdemo` / `transferdemo`

---

### Variante B (alternativ): Lokales PostgreSQL + SQL-Skripte
Wenn du PostgreSQL lokal installiert hast, kannst du das Schema und Seed-Daten auch direkt einspielen:

```bash
# Beispielname: tanzverein
createdb tanzverein

psql -d tanzverein -f datenbank/tanzverein_datenbank.sql
psql -d tanzverein -f datenbank/seed.sql
```

**Smoke-Test (PostgreSQL)**
```sql
SELECT schemaname, tablename
FROM pg_tables
WHERE schemaname NOT IN ('pg_catalog','information_schema');
```
---

**Ziele:**
- referenzielle Integrität (FKs)
- klare Kardinalitäten
- Normalisierung bis mind. **3NF**
- sinnvolle Datentypen & Constraints

---

## Features
- **Termine verwalten:** Erstellen, bearbeiten, löschen
- **Mitglieder erfassen:** Stammdaten, Rollen (Mitglied, Trainer, Admin)
- **Teilnahmen erfassen:** Zusagen/Absagen mit Formular-Tracking
- **Suchen & Filtern:** Tabellenansichten mit Such- und Sortierfunktion
- **Validierung:** Eingabeprüfung (Pflichtfelder, Datenformate)

---

## Konfiguration

### DB-Verbindung (JavaFX)
Standardwerte liegen in:
- `JavaFX/AppOnlyRelease/my-app/src/main/resources/db.properties`

Optional kannst du sie überschreiben über:
- **Java System Properties:** `-Ddb.url=... -Ddb.user=... -Ddb.password=...`
- **Environment-Variablen:** `KUD_DB_URL`, `KUD_DB_USER`, `KUD_DB_PASSWORD`

---

## Troubleshooting

| Problem | Lösung |
|---------|--------|
| „no POM in this directory" | Ins Verzeichnis `JavaFX/AppOnlyRelease/my-app` wechseln |
| Datenbank nicht erreichbar | `docker compose -f docker-compose.db.yaml up -d` im richtigen Verzeichnis ausführen |
| Falsche DB-Credentials | `db.properties` prüfen oder per Env/`-D...` überschreiben |

---

## Artefakte & Dokumente
- [`bericht/Technischer_Bericht_EntwicklungBasics.pdf`](bericht/Technischer_Bericht_EntwicklungBasics.pdf)
- [`bericht/Fachuebergreifendes Transfer-Projekt.pdf`](bericht/Fachuebergreifendes%20Transfer-Projekt.pdf)
- [`datenbank/Datenbankschema-Dokumentation.pdf`](datenbank/Datenbankschema-Dokumentation.pdf)
- [`datenbank/tanzverein_datenbank.sql`](datenbank/tanzverein_datenbank.sql)
- [`datenbank/seed.sql`](datenbank/seed.sql)
- `datenbank/Transfer-Projekt_Implementierung/` (Docker Compose + Flyway Migrationen)

---

## Lizenz
HFTM-Praxisanwendung. Nutzung im Rahmen der Informatiker-HF-Ausbildung.
