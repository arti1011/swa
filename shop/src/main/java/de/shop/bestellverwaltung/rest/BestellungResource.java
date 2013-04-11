package de.shop.bestellverwaltung.rest;

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

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.LocaleHelper;
import de.shop.util.Mock;
import de.shop.util.NotFoundException;

@Path("/bestellungen")
@Produces(APPLICATION_JSON)
@Consumes
public class BestellungResource {
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpHeaders headers;

	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id) {
		@SuppressWarnings("unused")
		final Locale locale = localeHelper.getLocale(headers);
		
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Bestellung bestellung = Mock.findBestellungById(id);
		if (bestellung == null) {
			throw new NotFoundException("Keine Bestellung mit der ID " + id + " gefunden.");
		}
		
		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		return bestellung;
	}
}
