
package de.shop.util;

import static de.shop.util.Constants.REST_PATH;

public final class TestConstants {

	public static final String WEB_PROJEKT = "shop";
	
	// IDs
	public static final int BESTELLUNG_ID_EXISTS = 400;
	public static final int NO_ID = 100000;
	
	// https
	public static final String HTTPS = "https";
	public static final String HOST = "localhost";
	public static final int PORT = 8443;
	public static final String KEYSTORE_TYPE = "JKS";
	public static final String TRUSTSTORE_NAME = "client.truststore";
	public static final String TRUSTSTORE_PASSWORD = "Zimmermann";
	
	// Basis-URI
	// https://localhost:8443/shop/rest
	public static final String BASE_URI = HTTPS + "://" + HOST + ":" + PORT + "/" + WEB_PROJEKT + REST_PATH;
	
	// Pfade 
	//Kunde
	public static final String KUNDEN_URI = BASE_URI + "/kunden";
	public static final String KUNDEN_ID_PATH_PARAM = "kundenId";
	public static final String KUNDEN_ID_URI = KUNDEN_URI + "/{" + KUNDEN_ID_PATH_PARAM + "}";
	public static final String KUNDEN_ID_FILE_URI = KUNDEN_ID_URI + "/file";
	
	// Bestellung
	public static final String BESTELLUNGEN_URI = BASE_URI + "/bestellung";
	public static final String BESTELLUNGEN_ID_PATH_PARAM = "bestellungId";
	public static final String BESTELLUNGEN_ID_URI = BESTELLUNGEN_URI + "/{" + BESTELLUNGEN_ID_PATH_PARAM + "}";
	public static final String BESTELLUNGEN_ID_KUNDE_URI = BESTELLUNGEN_ID_URI + "/kunde";
	
	public static final Long BESTELLUNG_ID = Long.valueOf(400);
	public static final Long BESTELLUNG_ID_NOT_FOUND = Long.valueOf(444);
	
	//Artikel
	public static final String ARTIKEL_URI = BASE_URI + "/artikel";
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_ID_URI = ARTIKEL_URI + "/{" + ARTIKEL_ID_PATH_PARAM + "}";
	public static final String ARTIKEL_ID_FILE_URI = ARTIKEL_ID_URI + "/file";
	
	public static final Long ARTIKEL_STUHL = Long.valueOf(300);
	public static final Long ARTIKEL_DOPPELBETT = Long.valueOf(301);
	
	// Username und Password
	public static final String USERNAME_MITARBEITER = "102";
	public static final String PASSWORD_MITARBEITER = "102";
	public static final String USERNAME_ADMIN = "1";
	public static final String PASSWORD_ADMIN = "1";
	public static final String USERNAME_KUNDE = "104";
	public static final String PASSWORD_KUNDE = "104";
	public static final String PASSWORD_FALSCH = "falsch";
	
	//Allgemein gültige Attribute
	public static final int VERSION = 1;
	
	// Testklassen fuer Service- und Domain-Tests (nicht in Software Engineering)
	public static final Class<?>[] TEST_CLASSES = {};
	
	private TestConstants() {
		
	}
			
}
