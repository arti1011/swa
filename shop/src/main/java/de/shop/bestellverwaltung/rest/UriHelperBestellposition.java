package de.shop.bestellverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;


@ApplicationScoped
public class UriHelperBestellposition {
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	public void updateUriBestellposition(Bestellposition bestellposition, UriInfo uriInfo) {
		// URL fuer Bestellung setzen
		final Bestellung bestellung = bestellposition.getBestellung();
		if (bestellung != null) {
			final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellposition.getBestellung(), uriInfo);
			bestellposition.setBestellungUri(bestellungUri);
		}
		//URL fuer Artikel setzen
		final Artikel artikel = bestellposition.getArtikel();
		if (artikel != null) {
			final URI artikelUri = uriHelperArtikel.getUriArtikel(bestellposition.getArtikel(), uriInfo);
			bestellposition.setArtikelUri(artikelUri);
		}
	}

	public URI getUriBestellposition(Bestellposition bestellposition, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(BestellpositionResource.class)
		                             .path(BestellpositionResource.class, "findBestellpositionById");
		final URI uri = ub.build(bestellposition.getPositionId());
		return uri;
	}
	
}
