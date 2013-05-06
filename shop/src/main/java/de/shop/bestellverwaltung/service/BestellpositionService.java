package de.shop.bestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;

public interface BestellpositionService {
	Bestellposition findBestellpositionById(Long id);
	List<Bestellposition> findBestellpositionenByBestellungId(Long bestellungId);
	Bestellposition createBestellposition(Bestellposition bestellposition, Bestellung bestellung, Locale locale);
}

