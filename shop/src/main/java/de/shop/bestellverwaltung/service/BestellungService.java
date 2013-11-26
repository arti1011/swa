package de.shop.bestellverwaltung.service;

import java.util.List;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;

public interface BestellungService {

	Bestellung findBestellungById(Long id);
	
	List<Bestellung> findBestellungenByIds(List<Long> ids);
	
	AbstractKunde findKundeById(Long id);
	
	List<Bestellung> findBestellungenByKunde(AbstractKunde kunde);
	
	Bestellung createBestellung(Bestellung bestellung, String username);
	
	Bestellung createBestellung(Bestellung bestellung, AbstractKunde kunde);
	
	Bestellung findBestellungByPostenId(Long id);
	
}
