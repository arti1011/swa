package de.shop.kundenverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.kundenverwaltung.domain.AbstractKunde;


@ApplicationScoped
public class UriHelperKunde {
	public URI getUriKunde(AbstractKunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(KundeResource.class)
		                             .path(KundeResource.class, "findKundeById");
		final URI kundeUri = ub.build(kunde.getId());
		return kundeUri;
	}
	
	public void updateUriKunde(AbstractKunde kunde, UriInfo uriInfo) {
		// URL fuer Bestellungen setzen
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
                                     .path(KundeResource.class)
                                     .path(KundeResource.class, "findBestellungenByKundeId");
		final URI bestellungenUri = ub.build(kunde.getId());
		kunde.setBestellungenUri(bestellungenUri);
	}
}
