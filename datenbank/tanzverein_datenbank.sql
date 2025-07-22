-- SQL-Skript zur Erstellung der Datenbankstruktur 

-- Tabelle: Mitglied
CREATE TABLE Mitglied (
    mitglied_id INT PRIMARY KEY,
    vorname VARCHAR(50) NOT NULL,
    nachname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rolle ENUM('Mitglied', 'Trainer', 'Admin') NOT NULL
);

-- Tabelle: Termin
CREATE TABLE Termin (
    termin_id INT PRIMARY KEY,
    datum DATE NOT NULL,
    uhrzeit TIME NOT NULL,
    ort VARCHAR(100) NOT NULL,
    ferien_flag BOOLEAN DEFAULT FALSE
);

-- Tabelle: Formular
CREATE TABLE Formular (
    formular_id INT PRIMARY KEY,
    typ VARCHAR(50) NOT NULL,
    ausgabedatum DATE NOT NULL,
    rueckgabedatum DATE,
    status ENUM('ausstehend', 'eingereicht', 'geprueft') NOT NULL,
    mitglied_id INT NOT NULL,
    FOREIGN KEY (mitglied_id) REFERENCES Mitglied(mitglied_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Tabelle: Teilnahme
CREATE TABLE Teilnahme (
    teilnahme_id INT PRIMARY KEY,
    mitglied_id INT NOT NULL,
    termin_id INT NOT NULL,
    formular_id INT,
    status ENUM('zugesagt', 'abgesagt', 'abwesend') NOT NULL,
    FOREIGN KEY (mitglied_id) REFERENCES Mitglied(mitglied_id),
    FOREIGN KEY (termin_id) REFERENCES Termin(termin_id),
    FOREIGN KEY (formular_id) REFERENCES Formular(formular_id)
);