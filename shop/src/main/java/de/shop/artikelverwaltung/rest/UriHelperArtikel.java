package de.shop.artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;

@ApplicationScoped
public class UriHelperArtikel {
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ArtikelResource.class)
		                             .path(ArtikelResource.class, "findArtikelById")
		                             .path(ArtikelResource.class, "findArtikelByBezeichnung");
		final URI artikelUri = ub.build(artikel.getId());
		return artikelUri;
	}
	
}