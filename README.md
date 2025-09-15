# Praxisanwendung Entwicklung Basics – KUD Karadjordje Bern (Termin- & Formularverwaltung)

Diese Arbeit dokumentiert die Einzelarbeit im Rahmen der Praxisanwendung „Entwicklung Basics“ an der HF Informatik (HFTM). Ziel ist die Entwicklung einer Applikation zur zentralen Verwaltung von Tanztrainingsterminen sowie digitalen Formularen für den Verein **KUD Karadjordje Bern**.

## Inhaltsverzeichnis
- [Projektbeschreibung](#projektbeschreibung)
- [Beteiligte Kurse](#beteiligte-kurse)
- [Scope & Status](#scope--status)
- [Systemvoraussetzungen](#systemvoraussetzungen)
- [Ordnerstruktur](#ordnerstruktur)
- [Quickstart: Datenbank](#quickstart-datenbank)
- [Datenmodell (Kurzüberblick)](#datenmodell-kurzüberblick)
- [Artefakte & Dokumente](#artefakte--dokumente)
- [Geplante Erweiterungen](#geplante-erweiterungen)
- [Mitwirken](#mitwirken)
- [Lizenz](#lizenz)

## Projektbeschreibung
Dieses Repository dokumentiert die Einzelarbeit im Rahmen der Praxisanwendung „Entwicklung Basics“ an der HF Informatik (HFTM). Ziel ist die Entwicklung einer Applikation zur zentralen Verwaltung von Tanztrainingsterminen sowie digitalen Formularen für den Verein KUD Karadjordje Bern.

## Beteiligte Kurse
- System Modelling  
- Requirements Engineering  
- Relational Databases  
- Java Programming

## Scope & Status
- ✅ **Erarbeitet**: Anforderungen, UML/Use-Cases, relationales Schema, SQL-Skript  
- 🔜 **In Planung**: Applikations-Prototyp (UI/Backend), Import/Export, Rollenkonzept

## Systemvoraussetzungen
- PostgreSQL **oder** MySQL/MariaDB  
- Optional: psql / mysql CLI

## Ordnerstruktur
```
.
├── bericht/
│   ├── Technischer_Bericht_EntwicklungBasics.pdf
│   └── Fachuebergreifendes Transfer-Projekt.pdf
└── datenbank/
    ├── Datenbankschema-Dokumentation.pdf
    ├── tanzverein_datenbank.sql
    ├── seed.sql
    └── Transfer-Projekt_Implementierung.zip
```

## Quickstart: Datenbank
> Das SQL-Skript erstellt die Kernobjekte: **Mitglied**, **Termin**, **Formular**, **Teilnahme** (inkl. Keys/Constraints).

### Variante A: PostgreSQL
```bash
createdb tanzverein
psql -d tanzverein -f datenbank/tanzverein_datenbank.sql
psql -d tanzverein -f datenbank/seed.sql
```

### Variante B: MySQL/MariaDB
```bash
mysql -u <user> -p -e "CREATE DATABASE IF NOT EXISTS tanzverein CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u <user> -p tanzverein < datenbank/tanzverein_datenbank.sql
mysql -u <user> -p tanzverein < datenbank/seed.sql
```

## Datenmodell (Kurzüberblick)
- **Mitglied** ↔ **Teilnahme** ↔ **Termin**  
  Mitglieder melden sich zu Terminen an; **Teilnahme** bildet die N:M-Beziehung ab.  
- **Formular** ↔ (optional) Verknüpfung zu Termin/Workflow  
  Formulare dienen der strukturierten Datenerfassung (z. B. Anmeldung, Feedback usw.).

Ziele: referenzielle Integrität, klare Kardinalitäten, Normalisierung bis mind. 3NF, sinnvolle Datentypen und Constraints.

## Artefakte & Dokumente
- [`bericht/Technischer_Bericht_EntwicklungBasics.pdf`](bericht/Technischer_Bericht_EntwicklungBasics.pdf)  
- [`bericht/Fachuebergreifendes Transfer-Projekt.pdf`](bericht/Fachuebergreifendes%20Transfer-Projekt.pdf)  
- [`datenbank/Datenbankschema-Dokumentation.pdf`](datenbank/Datenbankschema-Dokumentation.pdf)  
- [`datenbank/tanzverein_datenbank.sql`](datenbank/tanzverein_datenbank.sql)  
- [`datenbank/seed.sql`](datenbank/seed.sql)  
- [`datenbank/Transfer-Projekt_Implementierung.zip`](datenbank/Transfer-Projekt_Implementierung.zip)

## Geplante Erweiterungen
- Minimal-Viable-App (CRUD für Mitglieder, Termine, Formular-Workflow)  
- Rollen & Berechtigungen (Trainer:in, Mitglied, Admin)  
- Validierungen (z. B. Kollisionsprüfung von Terminen)  
- Exporte (CSV/PDF), E-Mail-Benachrichtigungen  
- CI/Checks (Linting, SQL-Validierung), Beispiel-Datensatz / Seeds

## Mitwirken
Vorschläge oder Issues gerne eröffnen (Fehler in der Doku, SQL-Dialekt-Hinweise, Erweiterungswünsche).

## Lizenz
Noch nicht festgelegt. Bis dahin: Nutzung zu Lern-/Review-Zwecken im Rahmen der HFTM.
