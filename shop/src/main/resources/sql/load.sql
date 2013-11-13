-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================

--
-- kunde
--
INSERT INTO hobby VALUES (0, 'SPORT');
INSERT INTO hobby VALUES (1, 'LESEN');
INSERT INTO hobby VALUES (2, 'REISEN');

INSERT INTO kunde (id, nachname, vorname, seit, art, email, erzeugt, aktualisiert) VALUES (1,'Admin','Admin','01.01.2001','F','admin@hska.de','01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO kunde (id, nachname, vorname, seit, art, email, erzeugt, aktualisiert) VALUES (2,'Alpha','Adrian','31.01.2001','P','101@hska.de','01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO kunde (id, nachname, vorname, seit, art, email, erzeugt, aktualisiert) VALUES (3,'Alpha','Alfred','28.02.2002','P','102@hska.de','02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO kunde (id, nachname, vorname, seit, art, email, erzeugt, aktualisiert) VALUES (4,'Alpha','Anton','15.09.2003','F','103@hska.de','03.08.2006 00:00:00','03.08.2006 00:00:00');
INSERT INTO kunde (id, nachname, vorname, seit, art, email, erzeugt, aktualisiert) VALUES (5,'Delta','Dirk','30.04.2004','F','104@hska.de','04.08.2006 00:00:00','04.08.2006 00:00:00');
INSERT INTO kunde (id, nachname, vorname, seit, art, email, erzeugt, aktualisiert) VALUES (6,'Epsilon','Emil','31.03.2005','P','105@hska.de','05.08.2006 00:00:00','05.08.2006 00:00:00');

INSERT INTO adresse (id, plz, ort, kunde_fk, erzeugt, aktualisiert) VALUES (200,'76133','Karlsruhe',1,'01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO adresse (id, plz, ort, kunde_fk, erzeugt, aktualisiert) VALUES (201,'76133','Karlsruhe',2,'02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO adresse (id, plz, ort, kunde_fk, erzeugt, aktualisiert) VALUES (202,'76133','Karlsruhe',3,'03.08.2006 00:00:00','03.08.2006 00:00:00');
INSERT INTO adresse (id, plz, ort, kunde_fk, erzeugt, aktualisiert) VALUES (203,'76133','Karlsruhe',4,'04.08.2006 00:00:00','04.08.2006 00:00:00');
INSERT INTO adresse (id, plz, ort, kunde_fk, erzeugt, aktualisiert) VALUES (204,'76133','Karlsruhe',5,'05.08.2006 00:00:00','05.08.2006 00:00:00');
INSERT INTO adresse (id, plz, ort, kunde_fk, erzeugt, aktualisiert) VALUES (205,'76133','Karlsruhe',6,'06.08.2006 00:00:00','06.08.2006 00:00:00');


INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (2,0);
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (2,1);
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (3,0);
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (3,2);
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (6,1);
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (6,2);

INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (101,'Tisch ''Oval''',80,1,'01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (102,'Stuhl ''Sitz bequem''',10,1,'02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (103,'Tür ''Hoch und breit''',300,1,'03.08.2006 00:00:00','03.08.2006 00:00:00');
INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (104,'Fenster ''Glasklar''',150,1,'04.08.2006 00:00:00','04.08.2006 00:00:00');
INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (105,'Spiegel ''Mach mich schöner''',60,0,'05.08.2006 00:00:00','05.08.2006 00:00:00');
INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (106,'Kleiderschrank ''Viel Platz''',500,1,'06.08.2006 00:00:00','06.08.2006 00:00:00');
INSERT INTO artikel (id, artikelBezeichnung, preis, verfuegbar, erzeugt, aktualisiert) VALUES (107,'Bett ''Mit Holzwurm''',600,1,'07.08.2006 00:00:00','07.08.2006 00:00:00');

INSERT INTO bestellung (id, kunde_fk, ausgeliefert, idx, erzeugt, aktualisiert) VALUES (201,2,0,1,'01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO bestellung (id, kunde_fk, ausgeliefert, idx, erzeugt, aktualisiert) VALUES (202,2,1,1,'02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO bestellung (id, kunde_fk, ausgeliefert, idx, erzeugt, aktualisiert) VALUES (203,3,0,0,'03.08.2006 00:00:00','03.08.2006 00:00:00');
INSERT INTO bestellung (id, kunde_fk, ausgeliefert, idx, erzeugt, aktualisiert) VALUES (204,3,1,0,'04.08.2006 00:00:00','04.08.2006 00:00:00');
INSERT INTO bestellung (id, kunde_fk, ausgeliefert, idx, erzeugt, aktualisiert) VALUES (205,5,0,1,'05.08.2006 00:00:00','05.08.2006 00:00:00');

INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (500,201,101,1,0);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (501,201,102,4,1);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (502,202,103,5,0);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (503,203,104,3,0);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (504,203,105,2,1);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (505,204,106,1,0);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (506,205,107,5,0);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (507,205,101,2,1);
INSERT INTO bestellposition (positionId, bestellung_fk, artikel_fk, anzahl, idx) VALUES (508,205,102,8,2);
