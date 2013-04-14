package de.shop.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Firmenkunde;
import de.shop.kundenverwaltung.domain.HobbyType;
import de.shop.kundenverwaltung.domain.Privatkunde;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.ArtikelFarbeType;

/**
 * Emulation des Anwendungskerns
 */
public final class Mock {
	private static final int MAX_ID = 99;
	private static final int MAX_KUNDEN = 8;
	private static final int MAX_BESTELLUNGEN = 4;
	private static final int MAX_ARTIKEL = 15;
	
	public static AbstractKunde findKundeById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		
		final AbstractKunde kunde = id % 2 == 1 ? new Privatkunde() : new Firmenkunde();
		kunde.setId(id);
		kunde.setNachname("Nachname" + id);
		kunde.setEmail("" + id + "@hska.de");
		
		final Adresse adresse = new Adresse();
		adresse.setId(id + 1);        // andere ID fuer die Adresse
		adresse.setPlz("12345");
		adresse.setOrt("Testort");
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		if (kunde.getClass().equals(Privatkunde.class)) {
			final Privatkunde privatkunde = (Privatkunde) kunde;
			final Set<HobbyType> hobbies = new HashSet<>();
			hobbies.add(HobbyType.LESEN);
			hobbies.add(HobbyType.REISEN);
			privatkunde.setHobbies(hobbies);
		}
		
		return kunde;
	}

	public static Collection<AbstractKunde> findAllKunden() {
		final int anzahl = MAX_KUNDEN;
		final Collection<AbstractKunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final AbstractKunde kunde = findKundeById(Long.valueOf(i));
			kunden.add(kunde);			
		}
		return kunden;
	}

	public static Collection<AbstractKunde> findKundenByNachname(String nachname) {
		final int anzahl = nachname.length();
		final Collection<AbstractKunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final AbstractKunde kunde = findKundeById(Long.valueOf(i));
			kunde.setNachname(nachname);
			kunden.add(kunde);			
		}
		return kunden;
	}
	

	public static Collection<Bestellung> findBestellungenByKundeId(Long kundeId) {
		final AbstractKunde kunde = findKundeById(kundeId);
		
		// Beziehungsgeflecht zwischen Kunde und Bestellungen aufbauen
		final int anzahl = kundeId.intValue() % MAX_BESTELLUNGEN + 1;  // 1, 2, 3 oder 4 Bestellungen
		final List<Bestellung> bestellungen = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Bestellung bestellung = findBestellungById(Long.valueOf(i));
			bestellung.setKunde(kunde);
			bestellungen.add(bestellung);			
		}
		kunde.setBestellungen(bestellungen);
		
		return bestellungen;
	}

	public static Bestellung findBestellungById(Long id) {
		if (id > MAX_ID) {
			return null;
		}

		final AbstractKunde kunde = findKundeById(id + 1);  // andere ID fuer den Kunden

		final Bestellung bestellung = new Bestellung();
		bestellung.setId(id);
		bestellung.setAusgeliefert(false);
		bestellung.setKunde(kunde);
		
		return bestellung;
	}

	public static AbstractKunde createKunde(AbstractKunde kunde) {
		// Neue IDs fuer Kunde und zugehoerige Adresse
		// Ein neuer Kunde hat auch keine Bestellungen
		final String nachname = kunde.getNachname();
		kunde.setId(Long.valueOf(nachname.length()));
		final Adresse adresse = kunde.getAdresse();
		adresse.setId((Long.valueOf(nachname.length())) + 1);
		adresse.setKunde(kunde);
		kunde.setBestellungen(null);
		
		System.out.println("Neuer Kunde: " + kunde);
		return kunde;
	}

	public static void updateKunde(AbstractKunde kunde) {
		System.out.println("Aktualisierter Kunde: " + kunde);
	}

	public static void deleteKunde(Long kundeId) {
		System.out.println("Kunde mit ID=" + kundeId + " geloescht");
	}
	
	public static Artikel findArtikelById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		
		final Artikel artikel = new Artikel();
		final String bezeichnung;
				
		if(id % 3 == 2)
				{
					bezeichnung = "Schrank Verstauviel";
				}
				else if(id %3 == 1)
				{
					bezeichnung = "Couch Potato";
				}
				else
				{
					bezeichnung = "Tisch Vierbein";
				}
			
		artikel.setId(id);
		artikel.setArtikelbezeichnung("" + bezeichnung);
		artikel.setVerfügbarkeit("verfügbar");
		artikel.setPreis(id + 1.5);
		final Set<ArtikelFarbeType> farben = new HashSet<>();
		farben.add(ArtikelFarbeType.BLAU);
		farben.add(ArtikelFarbeType.SCHWARZ);
		farben.add(ArtikelFarbeType.WEISS);
		artikel.setFarbe(farben);
		
		return artikel;
	}
	
	public static Collection<Artikel> findAllArtikel() {
		final int anzahl = MAX_ARTIKEL;
		final Collection<Artikel> artikelliste = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikelliste.add(artikel);			
		}
		return artikelliste;
	}
	
	public static Collection<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		final int anzahl = bezeichnung.length();
		final Collection<Artikel> artikelliste = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikel.setArtikelbezeichnung(bezeichnung);
			artikelliste.add(artikel);			
		}
		return artikelliste;
	}
	
	public static Artikel createArtikel(Artikel artikel) {
		final String artikelbezeichnung = artikel.getArtikelbezeichnung();
		
		artikel.setId(Long.valueOf(artikelbezeichnung.length()));
		artikel.setPreis(artikel.getPreis());
		artikel.setFarbe(artikel.getFarbe());
		artikel.setVerfügbarkeit(artikel.getVerfügbarkeit());
		
		System.out.println("Neuer Artikel: " +artikel);
		return artikel;
	}
	
	public static void updateArtikel(Artikel artikel) {
		System.out.println("Aktualisierter Artikel: " + artikel.getArtikelbezeichnung());
	}
	
	public static void deleteArtikel(Long artikelId) {
		System.out.println("Artikel mit ID=" +artikelId +" geloescht");
	}

	private Mock() { /**/ }
}
