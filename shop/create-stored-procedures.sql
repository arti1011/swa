-- Stored Procedure zum Einfuegen einer Datei
-- Als Hilfsroutine fuer andere Stored Procedures, wie z.B. insert_file_kunde (s.u.)

-- Aufruf in einer Eingabeaufforderung (rechte Maustaste auf das Projekt> Easy Shell > Open) mit Oracle-Username "shop" und Password "p":
-- sqlplus shop/p @create-stored-procedures.sql
-- exit

CREATE OR REPLACE PROCEDURE insert_file(
	ftbl_id                 NUMBER,
	ftbl_version            NUMBER,
	filename                VARCHAR2,
	ftbl_filename           VARCHAR2,
	ftbl_mimetype           VARCHAR2,
	ftbl_multimedia_type    VARCHAR2,
	ftbl_erzeugt            TIMESTAMP,
	ftbl_aktualisiert       TIMESTAMP)
IS
	dest_lob BLOB;
	src_lob  BFILE;
BEGIN
	-- Voraussetzung:
	-- CREATE OR REPLACE DIRECTORY FILES AS 'C:\temp\db';
    -- GRANT READ, WRITE ON DIRECTORY FILES TO shop;
	src_lob := BFILENAME('FILES', filename);
	
	INSERT INTO file_tbl(id,
	                     version,
	                     bytes,
	                     filename,
	                     mimetype,
	                     multimedia_type,
	                     erzeugt,
	                     aktualisiert)
	     VALUES (ftbl_id,
	             ftbl_version,
	             EMPTY_BLOB(),
	             ftbl_filename,
	             ftbl_mimetype,
	             ftbl_multimedia_type,
	             ftbl_erzeugt,
	             ftbl_aktualisiert)
	  RETURNING bytes
	       INTO dest_lob;
	
	DBMS_LOB.OPEN(src_lob, DBMS_LOB.LOB_READONLY);
	DBMS_LOB.LoadFromFile(DEST_LOB => dest_lob,
	                      SRC_LOB  => src_lob,
	                      AMOUNT   => DBMS_LOB.GETLENGTH(src_lob));
	DBMS_LOB.CLOSE(src_lob);
	
END;
/

CREATE OR REPLACE PROCEDURE insert_file_kunde(
	kunde_id                NUMBER,
	-- Parameter fuer die Stored Procedure insert_file
	ftbl_id                 NUMBER,
	ftbl_version            NUMBER,
	filename                VARCHAR2,
	ftbl_filename           VARCHAR2,
	ftbl_mimetype           VARCHAR2,
	ftbl_multimedia_type    VARCHAR2,
	ftbl_erzeugt            TIMESTAMP,
	ftbl_aktualisiert       TIMESTAMP)
IS
BEGIN
	insert_file(ftbl_id,
	            ftbl_version,
	            filename,
	            ftbl_filename,
	            ftbl_mimetype,
	            ftbl_multimedia_type,
	            ftbl_erzeugt,
	            ftbl_aktualisiert);
	
	UPDATE kunde
	   SET file_fk = ftbl_id
	 WHERE id = kunde_id;
END;
/
