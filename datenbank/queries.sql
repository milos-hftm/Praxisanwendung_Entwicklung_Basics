-- ============================================
-- queries.sql – Beispielabfragen (Read-Only)
-- Dialektneutral (PostgreSQL & MySQL/MariaDB)
-- ============================================

-- 1) Teilnehmerzahl pro Termin (inkl. Termine ohne Anmeldungen)
SELECT
  t.termin_id,
  t.datum,
  t.beschreibung,
  COALESCE(COUNT(x.mitglied_id), 0) AS teilnehmerzahl
FROM Termin t
LEFT JOIN Teilnahme x ON x.termin_id = t.termin_id
GROUP BY t.termin_id, t.datum, t.beschreibung
ORDER BY t.datum DESC;

-- 2) Alle Teilnehmer eines bestimmten Termins (ID anpassen)
-- Tipp: Ersetze <TERMIN_ID> durch eine echte ID, z. B. 1
SELECT
  t.termin_id,
  t.datum,
  t.beschreibung,
  m.mitglied_id,
  m.vorname,
  m.nachname,
  m.email
FROM Termin t
JOIN Teilnahme x ON x.termin_id = t.termin_id
JOIN Mitglied  m ON m.mitglied_id = x.mitglied_id
WHERE t.termin_id = <TERMIN_ID>
ORDER BY m.nachname, m.vorname;

-- 3) Kommende Termine ab heute
SELECT
  termin_id, datum, beschreibung, ort
FROM Termin
WHERE datum >= CURRENT_DATE
ORDER BY datum ASC;

-- 4) Mitglieder-Suche (einfach, LIKE – Suchbegriff anpassen)
-- Ersetze <SUCH> durch z. B. milos oder @example.com
SELECT
  mitglied_id, vorname, nachname, email, geburtsdatum
FROM Mitglied
WHERE vorname  LIKE CONCAT('%', '<SUCH>', '%')
   OR nachname LIKE CONCAT('%', '<SUCH>', '%')
   OR email    LIKE CONCAT('%', '<SUCH>', '%')
ORDER BY nachname, vorname;
-- Hinweis: In PostgreSQL funktioniert CONCAT ebenfalls; alternativ:
-- WHERE vorname ILIKE '%' || '<SUCH>' || '%' ... (falls Case-Insensitive erwuenscht, dann alle drei Zeilen entsprechend anpassen)

-- 5) Dubletten in Teilnahme (gleicher Member mehrfach am gleichen Termin)
SELECT
  mitglied_id,
  termin_id,
  COUNT(*) AS anzahl
FROM Teilnahme
GROUP BY mitglied_id, termin_id
HAVING COUNT(*) > 1
ORDER BY anzahl DESC;

-- 6) Fremdschluessel-Waisen in Teilnahme (sollte leer sein)
SELECT x.*
FROM Teilnahme x
LEFT JOIN Mitglied m ON m.mitglied_id = x.mitglied_id
LEFT JOIN Termin  t ON t.termin_id   = x.termin_id
WHERE m.mitglied_id IS NULL
   OR t.termin_id   IS NULL;

-- 7) Termine mit detaillierter Teilnehmerliste (kompakt)
SELECT
  t.termin_id,
  t.datum,
  t.beschreibung,
  CONCAT(m.vorname, ' ', m.nachname) AS teilnehmer_name
FROM Termin t
LEFT JOIN Teilnahme x ON x.termin_id = t.termin_id
LEFT JOIN Mitglied  m ON m.mitglied_id = x.mitglied_id
ORDER BY t.datum DESC, teilnehmer_name ASC;

