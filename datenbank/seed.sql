-- =====================================
-- SEED DATA für KUD Karadjordje Bern DB
-- =====================================

-- Vorhandene Daten löschen (sicherstellen, dass Fremdschlüssel sauber sind)
DELETE FROM Teilnahme;
DELETE FROM Formular;
DELETE FROM Termin;
DELETE FROM Mitglied;

-- ======================
-- Mitglieder einfügen
-- ======================
INSERT INTO Mitglied (mitglied_id, vorname, nachname, geburtsdatum, email) VALUES
  (1, 'Milos', 'Petrovic', '1995-04-12', 'milos@example.com'),
  (2, 'Ana', 'Markovic', '1998-11-03', 'ana@example.com'),
  (3, 'Luka', 'Jovanovic', '2000-06-21', 'luka@example.com'),
  (4, 'Sofia', 'Ilic', '1997-02-14', 'sofia@example.com');

-- ======================
-- Termine einfügen
-- ======================
INSERT INTO Termin (termin_id, datum, beschreibung, ort) VALUES
  (1, '2025-09-20 18:00:00', 'Training Volkstanz – Gruppe A', 'Turnhalle Bern'),
  (2, '2025-09-22 19:00:00', 'Training Gesangschor', 'Musikraum Zentrum'),
  (3, '2025-09-27 18:00:00', 'Generalprobe für Auftritt', 'Kulturhaus Wabern');

-- ======================
-- Formulare einfügen
-- ======================
INSERT INTO Formular (formular_id, titel, beschreibung) VALUES
  (1, 'Anmeldung Training', 'Online-Formular zur Anmeldung für Trainingseinheiten'),
  (2, 'Feedback Formular', 'Rückmeldung zu Training oder Auftritt');

-- ======================
-- Teilnahmen einfügen (N:M Beziehung)
-- ======================
INSERT INTO Teilnahme (mitglied_id, termin_id) VALUES
  (1, 1),  -- Milos bei Training A
  (2, 1),  -- Ana bei Training A
  (3, 2),  -- Luka im Gesangschor
  (4, 3),  -- Sofia bei Generalprobe
  (1, 3);  -- Milos ebenfalls bei Generalprobe
