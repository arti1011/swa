package de.shop.artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.rest.KundeResource;


@ApplicationScoped
public class UriHelperArtikel {
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ArtikelResource.class)
		                             .path(ArtikelResource.class, "findArikelById");
		final URI artikelUri = ub.build(artikel.getId());
		return artikelUri;
	}
	
}
