package de.shop.artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.ArtikelRessource;
//import de.shop.artikelverwaltung.domain.Artikel;
//import de.shop.kundenverwaltung.domain.AbstractKunde;

@ApplicationScoped
public class UriHelperArtikel {
	
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ArtikelRessource.class)
		                             .path(ArtikelRessource.class, "findArtikelById");
		final URI uri = ub.build(artikel.getId());
		return uri;
	}
}