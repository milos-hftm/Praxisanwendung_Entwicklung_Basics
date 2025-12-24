# Praxisanwendung Entwicklung Basics – KUD Karadjordje Bern (Termin- & Formularverwaltung)

Diese Arbeit dokumentiert die Einzelarbeit im Rahmen der Praxisanwendung **„Entwicklung Basics“** an der HF Informatik (HFTM). Ziel ist die Entwicklung einer Applikation zur zentralen Verwaltung von Tanztrainingsterminen sowie digitalen Formularen für den Verein **KUD Karadjordje Bern**.

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
- [Artefakte & Dokumente](#artefakte--dokumente)
- [Geplante Erweiterungen](#geplante-erweiterungen)
- [Mitwirken](#mitwirken)
- [Lizenz](#lizenz)

---

## Projektbeschreibung
Dieses Repository dokumentiert die Einzelarbeit im Rahmen der Praxisanwendung **„Entwicklung Basics“** an der HF Informatik (HFTM).  
Ziel ist die Entwicklung einer **JavaFX Desktop-Applikation** zur Verwaltung von:
- **Tanztrainingsterminen** (Anlegen, Anzeigen, Bearbeiten, Löschen)
- **Mitgliedern** und **Teilnahmen** (Anmeldungen zu Terminen)
- **digitalen Formularen** (z. B. Anmeldung, Feedback; je nach Umsetzungsstand)

---

## Beteiligte Kurse
- System Modelling
- Requirements Engineering
- Relational Databases
- Java Programming

---

## Scope & Status
- ✅ **Erarbeitet**: Anforderungen, UML/Use-Cases, relationales Schema, SQL-Skript, Seed-Daten
- ✅ **Implementiert**: JavaFX Desktop-App (**Maven**) im Ordner `JavaFX/` inkl. DB-Anbindung (**PostgreSQL**) und CRUD-Grundfunktionen (Create/Read/Update/Delete) über GUI

> Hinweis: Je nach Entwicklungsstand sind Formularfunktionen evtl. teilweise umgesetzt (siehe Dokumente/Artefakte).

---

## Systemvoraussetzungen
- Java (JDK empfohlen: **17+**)
- Maven
- PostgreSQL

---

## Ordnerstruktur
```text
.
├── JavaFX/
│   ├── (Maven-Projekt: pom.xml, src/, resources/, JavaFX-UI + Controller + DB-Zugriff)
├── bericht/
│   ├── Technischer_Bericht_EntwicklungBasics.pdf
│   └── Fachuebergreifendes Transfer-Projekt.pdf
└── datenbank/
    ├── Datenbankschema-Dokumentation.pdf
    ├── tanzverein_datenbank.sql
    ├── seed.sql
    └── Transfer-Projekt_Implementierung.zip

## Quickstart: JavaFX-App
### App starten
```bash
cd JavaFX
mvn clean javafx:run

### Variante A: PostgreSQL
```bash
# 1) Neue DB erstellen (optional)
createdb tanzverein

# 2) Schema einspielen
psql -d tanzverein -f datenbank/tanzverein_datenbank.sql

# 3) Seed-Daten einspielen
psql -d tanzverein -f datenbank/seed.sql
```

**Smoke-Test**
```sql
-- PostgreSQL: alle User-Tabellen
SELECT schemaname, tablename
FROM pg_tables
WHERE schemaname NOT IN ('pg_catalog','information_schema');

-- MySQL/MariaDB: alle Tabellen in der DB 'tanzverein'
-- SELECT table_name FROM information_schema.tables WHERE table_schema='tanzverein';
```

## Datenmodell (Kurzueberblick)
- **Mitglied** ↔ **Teilnahme** ↔ **Termin**  
  Mitglieder melden sich zu Terminen an; **Teilnahme** bildet die N:M-Beziehung ab.  
- **Formular** (optional verknuepft)  
  Formulare dienen der strukturierten Datenerfassung (z. B. Anmeldung, Feedback usw.).

Ziele: referenzielle Integritaet, klare Kardinalitaeten, Normalisierung bis mind. 3NF, sinnvolle Datentypen und Constraints.

## Artefakte & Dokumente
- [`bericht/Technischer_Bericht_EntwicklungBasics.pdf`](bericht/Technischer_Bericht_EntwicklungBasics.pdf)  
- [`bericht/Fachübergreifendes Transfer-Projekt.pdf`](bericht/Fach%C3%BCbergreifendes%20Transfer-Projekt.pdf)
- [`datenbank/Datenbankschema-Dokumentation.pdf`](datenbank/Datenbankschema-Dokumentation.pdf)  
- [`datenbank/tanzverein_datenbank.sql`](datenbank/tanzverein_datenbank.sql)  
- [`datenbank/seed.sql`](datenbank/seed.sql)  
- [`datenbank/Transfer-Projekt_Implementierung.zip`](datenbank/Transfer-Projekt_Implementierung.zip)

## Geplante Erweiterungen
- Minimal-Viable-App (CRUD fuer Mitglieder, Termine, Formular-Workflow)  
- Rollen & Berechtigungen (Trainer:in, Mitglied, Admin)  
- Validierungen (z. B. Kollisionspruefung von Terminen)  
- Exporte (CSV/PDF), E-Mail-Benachrichtigungen  
- CI/Checks (Linting, SQL-Validierung), Beispiel-Datensatz / Seeds

## Mitwirken
Vorschlaege oder Issues gerne eroeffnen (Fehler in der Doku, SQL-Dialekt-Hinweise, Erweiterungswuensche).

## Lizenz
Noch nicht festgelegt. Bis dahin: Nutzung zu Lern-/Review-Zwecken im Rahmen der HFTM.
