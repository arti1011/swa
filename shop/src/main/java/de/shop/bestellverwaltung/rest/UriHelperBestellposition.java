package de.shop.bestellverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.bestellverwaltung.domain.Bestellposition;


@ApplicationScoped
public class UriHelperBestellposition {
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	public void updateUriBestellposition(Bestellposition bestellposition, UriInfo uriInfo) {

		//URL fuer Artikel setzen
		final Artikel artikel = bestellposition.getArtikel();
		if (artikel != null) {
			final URI artikelUri = uriHelperArtikel.getUriArtikel(bestellposition.getArtikel(), uriInfo);
			bestellposition.setArtikelUri(artikelUri);
		}
	}
	
}
