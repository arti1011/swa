package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.util.LocaleHelper;
import de.shop.util.Mock;
import de.shop.util.NotFoundException;

//TODO ArtikelRessource in ArtikelResource umbenennen (mit einem s) und auf alles anwenden!
@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
public class ArtikelRessource {
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpHeaders headers;
//TODO uriHelperArtikel anlegen
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id) {
		@SuppressWarnings("unused")
		final Locale locale = localeHelper.getLocale(headers);
		
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Artikel artikel = Mock.findArtikelById(id);
		if (artikel == null) {
			throw new NotFoundException("Kein Artikel mit der ID " + id + " gefunden.");
		}
		
		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperArtikel.updateUriArtikel(artikel, uriInfo);
		return artikel;
	}
}