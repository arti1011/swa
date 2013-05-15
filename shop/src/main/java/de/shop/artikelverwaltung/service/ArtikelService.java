package de.shop.artikelverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.artikelverwaltung.domain.Artikel;

public interface ArtikelService {
	Artikel findArtikelById(Long id, Locale locale);
	Artikel createArtikel(Artikel artikel, Locale locale);
	Artikel updateArtikel(Artikel artikel, Locale locale);
	Artikel deleteArtikel(Long artikelId, Locale locale);
	List<Artikel> findArtikelByBezeichnung(String bezeichnung, Locale locale);
	List<Artikel> findAllArtikel();
}

