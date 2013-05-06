package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.service.BestellpositionService;
import de.shop.util.NotFoundException;

@Path("/bestellpositionen")
@Produces(APPLICATION_JSON)
@Consumes
public class BestellpositionResource {
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelperBestellposition uriHelperBestellposition;
	
	@Inject
	private BestellpositionService bsp;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellposition findBestellpositionById(@PathParam("id") Long id) {
		final Bestellposition bestellposition = bsp.findBestellpositionById(id);
		if (bestellposition== null) {
			throw new NotFoundException("Keine Bestellposition mit der ID " + id + " gefunden.");
		}
		
		// URLs innerhalb der gefundenen Bestellposition anpassen
		uriHelperBestellposition.updateUriBestellposition(bestellposition, uriInfo);
		return bestellposition;
	}
	
}
