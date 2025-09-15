# KUD Karadjordje Bern – Termin- & Formularverwaltung (Praxisanwendung Entwicklung Basics)

Diese Arbeit dokumentiert die Konzeption und Datenbank-Basis für eine Web-App zur zentralen Verwaltung von Tanztrainingsterminen sowie digitalen Formularen des Vereins **KUD Karadjordje Bern** (HFTM | HF Informatik).

> Fokus: System Modelling · Requirements Engineering · Relationale Datenbanken · Java-Grundlagen

---

## Inhaltsverzeichnis

- [Projektziele](#projektziele)
- [Scope & Status](#scope--status)
- [Systemvoraussetzungen](#systemvoraussetzungen)
- [Ordnerstruktur](#ordnerstruktur)
- [Quickstart: Datenbank](#quickstart-datenbank)
- [Datenmodell (Kurzüberblick)](#datenmodell-kurzüberblick)
- [Artefakte & Dokumente](#artefakte--dokumente)
- [Geplante Erweiterungen](#geplante-erweiterungen)
- [Mitwirken](#mitwirken)
- [Lizenz](#lizenz)

---

## Projektziele

- Trainings- und Vereins-Termine zentral verwalten  
- Digitale Formulare (z. B. Anmeldungen/Teilnahmen) abbilden  
- Konsistentes Datenmodell entwerfen und als SQL implementieren  
- Grundbausteine für eine spätere Applikation schaffen

---

## Scope & Status

- ✅ **Erarbeitet**: Anforderungen, UML/Use-Cases, relationales Schema, SQL-Skript  
- 🔜 **In Planung**: Applikations-Prototyp (UI/Backend), Import/Export, Rollenkonzept

---

## Systemvoraussetzungen

- PostgreSQL **oder** MySQL/MariaDB
- Optional: psql / mysql CLI

---

## Ordnerstruktur

.
├── bericht/ # Berichte & Dokumentation (PDF)
│ ├── Technischer_Bericht_EntwicklungBasics.pdf
│ └── Fachuebergreifendes Transfer-Projekt.pdf
└── datenbank/ # Datenbankartefakte
├── Datenbankschema-Dokumentation.pdf
├── tanzverein_datenbank.sql
└── Transfer-Projekt_Implementierung.zip

yaml
Code kopieren

---

## Quickstart: Datenbank

> Das SQL-Skript erstellt die Kernobjekte: **Mitglied**, **Termin**, **Formular**, **Teilnahme** (inkl. Keys/Constraints).

### Variante A: PostgreSQL

```bash
# 1) Neue DB erstellen (optional)
createdb tanzverein

# 2) Schema einspielen
psql -d tanzverein -f datenbank/tanzverein_datenbank.sql
Variante B: MySQL/MariaDB
bash
Code kopieren
# 1) Neue DB erstellen (optional)
mysql -u <user> -p -e "CREATE DATABASE IF NOT EXISTS tanzverein CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2) Schema einspielen
mysql -u <user> -p tanzverein < datenbank/tanzverein_datenbank.sql
Hinweis: Falls das Skript Dialekt-spezifische Syntax enthält, kurz prüfen und ggf. anpassen.

Smoke-Test

sql
Code kopieren
-- Anzahl Tabellen prüfen (Beispiel; ggf. Schema/DB-Namen anpassen)
SELECT table_name
FROM information_schema.tables
WHERE table_schema NOT IN ('information_schema','pg_catalog');

-- Für MySQL/MariaDB:
-- SELECT table_name FROM information_schema.tables WHERE table_schema='tanzverein';
Datenmodell (Kurzüberblick)
Mitglied ↔ Teilnahme ↔ Termin
Mitglieder melden sich zu Terminen an; Teilnahme bildet die N:M-Beziehung ab.

Formular ↔ (optional) Verknüpfung zu Termin/Workflow
Formulare dienen der strukturierten Datenerfassung (Anmeldung, Feedback usw.).

Ziele: Referenzielle Integrität, klare Kardinalitäten, Normalisierung bis mind. 3NF, sinnvolle Datentypen und Constraints.

Artefakte & Dokumente
Technischer Bericht: Analyse, Datenmodell, UML, Use-Cases, Umsetzungsidee
bericht/Technischer_Bericht_EntwicklungBasics.pdf

Transfer-Projekt: Funktionale Anforderungen, Entitäten, Geschäftsregeln
bericht/Fachuebergreifendes Transfer-Projekt.pdf

DB-Schema-Doku: Tabellen & Constraints erklärt
datenbank/Datenbankschema-Dokumentation.pdf

SQL-Skript: Tabellenanlage
datenbank/tanzverein_datenbank.sql

Implementierungs-ZIP (Begleitmaterial)
datenbank/Transfer-Projekt_Implementierung.zip

Geplante Erweiterungen
Minimal-Viable-App (CRUD für Termine/Mitglieder, Formular-Workflow)

Rollen & Berechtigungen (Trainer:in, Mitglied, Admin)

Validierungen (z. B. Kollisionsprüfung von Terminen)

Exporte (CSV/PDF), E-Mail-Benachrichtigungen

CI-Checks (Linting/SQL-Validierung), Example-Dataset/Seeds

Mitwirken
Vorschläge oder Issues gerne eröffnen (Fehler in der Doku, SQL-Dialekt-Hinweise, Erweiterungswünsche).

Lizenz
Noch nicht festgelegt. Bis dahin: Nutzung zu Lern-/Review-Zwecken im Rahmen der HFTM.

makefile
Code kopieren
::contentReference[oaicite:0]{index=0}
