package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.LocaleHelper;
import de.shop.util.Mock;
import de.shop.util.NotFoundException;

@Path("/bestellpositionen")
@Produces(APPLICATION_JSON)
@Consumes
public class BestellpositionResource {
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpHeaders headers;
	
	@Inject
	private UriHelperBestellposition uriHelperBestellposition;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellposition findBestellpositionById(@PathParam("id") Long id) {
		@SuppressWarnings("unused")
		final Locale locale = localeHelper.getLocale(headers);
		
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Bestellposition bestellposition = Mock.findBestellpositionById(id);
		if (bestellposition == null) {
			throw new NotFoundException("Keine Bestellposition mit der ID " + id + " gefunden.");
		}
		
		// URLs innerhalb der gefundenen Bestellposition anpassen
		uriHelperBestellposition.updateUriBestellposition(bestellposition, uriInfo);
		return bestellposition;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createBestellposition(Bestellposition bestellposition) {
		@SuppressWarnings("unused")
		final Locale locale = localeHelper.getLocale(headers);
		
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		bestellposition = Mock.createBestellposition(bestellposition);
		final URI bestellpositionUri = uriHelperBestellposition.getUriBestellposition(bestellposition, uriInfo);
		return Response.created(bestellpositionUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response updateBestellposition(Bestellposition bestellposition) {
		@SuppressWarnings("unused")
		final Locale locale = localeHelper.getLocale(headers);
		
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.updateBestellposition(bestellposition);
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public Response deleteBestellposition(@PathParam("id") Long id) {
		@SuppressWarnings("unused")
		final Locale locale = localeHelper.getLocale(headers);
		
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteBestellposition(id);
		return Response.noContent().build();
	}
}
	
	
	