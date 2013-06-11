-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================

-- ===============================================================================
-- Tabellen fuer Enum-Werte *einmalig* anlegen und jeweils Werte einfuegen
-- Beim ALLERERSTEN Aufruf die Zeilen mit "DROP TABLE ..." durch -- auskommentieren
-- ===============================================================================

DROP TABLE hobby;
CREATE TABLE hobby(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(16) NOT NULL UNIQUE) CACHE;
INSERT INTO hobby VALUES (0, 'SPORT');
INSERT INTO hobby VALUES (1, 'LESEN');
INSERT INTO hobby VALUES (2, 'REISEN');

-- ===============================================================================
-- Fremdschluessel in den bereits *generierten* Tabellen auf die obigen "Enum-Tabellen" anlegen
-- ===============================================================================

ALTER TABLE kunde_hobby ADD CONSTRAINT kunde_hobby__hobby_fk FOREIGN KEY (hobby_fk) REFERENCES hobby;
