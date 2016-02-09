DROP DATABASE IF EXISTS go2dhbw;
DROP VIEW IF EXISTS go2dhbw.studentensucheview;
DROP VIEW IF EXISTS go2dhbw.firmensucheview;

CREATE DATABASE go2dhbw;

CREATE TABLE go2dhbw.accounttyp
(
id int NOT NULL,
typ varchar(13),
PRIMARY KEY (id)
);

INSERT INTO go2dhbw.accounttyp VALUES(0, "Administrator");
INSERT INTO go2dhbw.accounttyp VALUES(1, "Bewerber");
INSERT INTO go2dhbw.accounttyp VALUES(2, "Firma");

CREATE TABLE go2dhbw.benutzer
(
id int NOT NULL AUTO_INCREMENT,
account_id int,
email varchar(255) NOT NULL UNIQUE,
passwort varchar(255) NOT NULL,
FOREIGN KEY (account_id) REFERENCES go2dhbw.accounttyp(id),
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.studiengang 
(
id int NOT NULL AUTO_INCREMENT,
bezeichnung VARCHAR(255) UNIQUE,
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.firmenprofil
(
id int NOT NULL AUTO_INCREMENT,
name VARCHAR(255),
logo BLOB,
website VARCHAR(255),
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.ansprechpartner
(
id int NOT NULL AUTO_INCREMENT,
name VARCHAR(255),
email VARCHAR(255),
telefonnummer VARCHAR(255),
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.standort
(
id int NOT NULL AUTO_INCREMENT,
firmenprofil_id int,
ansprechpartner_id int,
alias VARCHAR(255),
strasse VARCHAR(255),
ort VARCHAR(255),
plz varchar(255),
lat varchar(255),
lng varchar(255),
FOREIGN KEY (firmenprofil_id) REFERENCES go2dhbw.firmenprofil(id),
FOREIGN KEY (ansprechpartner_id) REFERENCES go2dhbw.ansprechpartner(id),
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.studienplaetze
(
id int NOT NULL AUTO_INCREMENT,
studiengang_id int,
standort_id int,
anzahl int,
note_deutsch DOUBLE(2,1),
note_englisch DOUBLE(2,1),
note_mathe DOUBLE(2,1),
zeugnisschnitt DOUBLE(2,1),
FOREIGN KEY (studiengang_id) REFERENCES go2dhbw.studiengang(id),
FOREIGN KEY (standort_id) REFERENCES go2dhbw.standort(id),
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.bewerberprofil
(
id int NOT NULL AUTO_INCREMENT,
benutzer_id int,
name VARCHAR(255),
geburtsjahr VARCHAR(255),
telefonnummer VARCHAR(255),
hobbies VARCHAR(255),
zusatzqualifikationen VARCHAR(255),
note_deutsch DOUBLE(2,1),
note_englisch DOUBLE(2,1),
note_mathe DOUBLE(2,1),
zeugnisschnitt DOUBLE(2,1),
plz varchar(255),
lat varchar(255),
lng varchar(255),
FOREIGN KEY (benutzer_id) REFERENCES go2dhbw.benutzer(id),
PRIMARY KEY (id)
);

CREATE TABLE go2dhbw.lieblingsfaecher
(
id int NOT NULL,
bezeichnung VARCHAR(255),
PRIMARY KEY (id)
);

INSERT INTO go2dhbw.lieblingsfaecher (id,bezeichnung) VALUE (1,'Mathematik');
INSERT INTO go2dhbw.lieblingsfaecher (id,bezeichnung) VALUE (2,'Deutsch');
INSERT INTO go2dhbw.lieblingsfaecher (id,bezeichnung) VALUE (3,'Englisch');

CREATE TABLE go2dhbw.studiengang_bewerberprofil
(
studiengang_id int NOT NULL,
bewerberprofil_id int NOT NULL,
FOREIGN KEY (studiengang_id) REFERENCES go2dhbw.studiengang(id),
FOREIGN KEY (bewerberprofil_id) REFERENCES go2dhbw.bewerberprofil(id)
);

CREATE TABLE go2dhbw.lieblingsfaecher_bewerberprofil
(
lieblingsfaecher_id int NOT NULL,
bewerberprofil_id int NOT NULL,
FOREIGN KEY (lieblingsfaecher_id) REFERENCES go2dhbw.lieblingsfaecher(id),
FOREIGN KEY (bewerberprofil_id) REFERENCES go2dhbw.bewerberprofil(id)
);

CREATE TABLE go2dhbw.benutzer_firmenprofil
(
benutzer_id int NOT NULL,
firmenprofil_id int NOT NULL,
FOREIGN KEY (benutzer_id) REFERENCES go2dhbw.benutzer(id),
FOREIGN KEY (firmenprofil_id) REFERENCES go2dhbw.firmenprofil(id)
);

CREATE VIEW go2dhbw.firmensucheview 
AS SELECT sp.id, sp.note_deutsch, sp.note_englisch, sp.note_mathe, sp.zeugnisschnitt, so.firmenprofil_id, sp.studiengang_id, sp.standort_id, sp.anzahl
		  , fp.name, fp.logo, fp.website
		  , ap.name as ansprechpartnername
          , st.Bezeichnung
          , ap.email, ap.telefonnummer
          , so.alias, so.strasse, so.ort, so.lat, so.lng
FROM 	  go2dhbw.studienplaetze sp, 
		  go2dhbw.studiengang st, 
          go2dhbw.firmenprofil fp, 
          go2dhbw.ansprechpartner ap, 
          go2dhbw.standort so
WHERE 	  so.firmenprofil_id = fp.id 
	  AND sp.studiengang_id = st.id 
      AND so.firmenprofil_id = fp.id
      AND sp.standort_id = so.id
      AND so.ansprechpartner_id = ap.id;
	
CREATE VIEW go2dhbw.studentensuchepreview1 AS 
SELECT bp.*, bn.email, GROUP_CONCAT(DISTINCT st.bezeichnung SEPARATOR ', ') AS studiengang 
FROM   go2dhbw.bewerberprofil bp, 
go2dhbw.studiengang_bewerberprofil stb, 
            go2dhbw.studiengang st,  
            go2dhbw.benutzer bn 
WHERE   bp.id = stb.bewerberprofil_id 
AND stb.studiengang_id = st.id 
    AND bn.id = bp.benutzer_id
	AND bn.account_id = '1'
	GROUP BY bp.id;

CREATE VIEW go2dhbw.studentensuchepreview2
AS SELECT 
	bp.id, 
	GROUP_CONCAT(DISTINCT lb.bezeichnung SEPARATOR ', ')  AS lieblingsfach
FROM 
	go2dhbw.bewerberprofil bp, 
	go2dhbw.lieblingsfaecher lb, 
	go2dhbw.lieblingsfaecher_bewerberprofil lfb, 
	go2dhbw.benutzer bn
WHERE 
	bp.id=lfb.bewerberprofil_id 
AND lfb.lieblingsfaecher_id=lb.id 
AND bn.id = bp.benutzer_id 
AND bn.account_id = '1'
GROUP BY bp.id;
	
CREATE VIEW go2dhbw.studentensucheview AS
SELECT ssp1.*, ssp2.lieblingsfach
FROM 
	go2dhbw.studentensuchepreview1 ssp1
LEFT JOIN 
	go2dhbw.studentensuchepreview2 ssp2
ON 	ssp1.id=ssp2.id;

Commit;