-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================

--
-- hibernate_sequence
--
DROP TABLE hobby;
DROP SEQUENCE hibernate_sequence;

CREATE TABLE hobby(id VARCHAR2(2) NOT NULL PRIMARY KEY, txt VARCHAR2(16) NOT NULL UNIQUE) CACHE;
CREATE SEQUENCE hibernate_sequence START WITH 5000;


--ALTER TABLE kunde_hobby ADD CONSTRAINT kunde_hobby__hobby_fk FOREIGN KEY (hobby_fk) REFERENCES hobby;