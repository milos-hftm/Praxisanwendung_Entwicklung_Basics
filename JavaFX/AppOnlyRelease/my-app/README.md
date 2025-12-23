# KUD Karadjordje Bern – JavaFX Desktop-App

JavaFX-Desktop-Anwendung zur Verwaltung von Trainingsterminen, Mitgliedern, Formularen und Teilnahmen des Tanzvereins KUD Karadjordje Bern.

## Features

- Module: Termin-, Mitglieder-, Formular- und Teilnahmeverwaltung
- TableView mit Sortierung und Live-Filterung (Volltext)
- CRUD-Funktionen mit Validierung und Löschbestätigung
- CSV-Export (Termine, Mitglieder)
- JDBC-Anbindung an PostgreSQL (Schema gemäss Transfer-Projekt)

## Technologie-Stack

- Java 21
- JavaFX 21 (controls, fxml)
- Maven
- PostgreSQL JDBC (42.7.x)

## Architektur

- MVC
  - Model: `ch.hftm.model` (JavaFX Properties, Enums)
  - Persistence: `ch.hftm.persistence` (JDBC-Repositories, `DatabaseConnection`)
  - Controller: `ch.hftm.controller` (FXML-gebundene UI-Logik)
  - Utilities: `ch.hftm.util` (Validierung, Dialoge, Export)

## Voraussetzungen

- JDK 21 installiert und konfiguriert
- PostgreSQL erreichbar (lokal via Docker aus dem Transfer-Projekt)

### DB-Konfiguration

Default-Werte liegen in `src/main/resources/db.properties`.

Überschreiben (Priorität: System Properties → Environment → db.properties):
- **Java System Properties:** `-Ddb.url=... -Ddb.user=... -Ddb.password=...`
- **Environment-Variablen:** `KUD_DB_URL`, `KUD_DB_USER`, `KUD_DB_PASSWORD`

## Starten der Anwendung

```bash
mvn clean javafx:run
```
- Modulpfad: `JavaFX/AppOnlyRelease/my-app`
- Startklasse: `ch.hftm.App`

## Build & Packaging

- Jar bauen:

```bash
mvn -DskipTests package
```

- Laufzeit-Image mit jlink (JavaFX-Plugin):

```bash
mvn clean javafx:jlink
```

Ergebnis: Image und Launcher unter `target/` (Namen konfiguriert im Plugin)

## Datenbank-Setup

- Nutzt das Schema aus `datenbank/tanzverein_datenbank.sql`
- Alternativ: Spring Boot Backend aus `Transfer-Projekt_Implementierung/relational-databases-orm-java-main` nutzen, dort Docker-Compose starten

## Bedienhinweise

- Sortierung: Klick auf Spaltenkopf in jeder Tabelle
- Filterung/Volltextsuche: Suchfelder in der Top-Leiste nutzen, Filter wirkt live
- Löschaktionen: Sicherheitsabfrage bestätigt jeden Löschvorgang
- Validierung: Pflichtfelder, E-Mail-Format und Datum/Uhrzeit werden geprüft
- CSV-Export: In Termin- und Mitglieder-Views verfügbar
- Navigation: Zurück-Button immer oben sichtbar

## Projektstruktur (Auszug)

```
src/main/java/ch/hftm/
  App.java
  controller/
  model/
  persistence/
  service/
  util/
src/main/resources/
  main.fxml
  termin.fxml
  mitglied.fxml
  formular.fxml
  teilnahme.fxml
  main.css
```

## Fehlerbehandlung

- Benutzerfreundliche Fehlermeldungen via `DialogUtil`
- Technische Details geloggt mit `java.util.logging`

## Tests

- Unit- und Integrationstests können auf Repository-Ebene ergänzt werden (z. B. mittels Testcontainers für PostgreSQL)

## Lizenz

Dieses Projekt ist Teil der HFTM-Praxisanwendung. Die Nutzung ist im Rahmen der Ausbildung vorgesehen.
