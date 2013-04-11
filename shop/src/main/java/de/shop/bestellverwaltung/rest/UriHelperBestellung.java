package de.shop.bestellverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.rest.UriHelperKunde;


@ApplicationScoped
public class UriHelperBestellung {
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	public void updateUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		// URL fuer Kunde setzen
		final AbstractKunde kunde = bestellung.getKunde();
		if (kunde != null) {
			final URI kundeUri = uriHelperKunde.getUriKunde(bestellung.getKunde(), uriInfo);
			bestellung.setKundeUri(kundeUri);
		}
		
	}

	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(BestellungResource.class)
		                             .path(BestellungResource.class, "findBestellungById");
		final URI uri = ub.build(bestellung.getId());
		return uri;
	}
}
