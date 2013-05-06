package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellpositionService;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.util.NotFoundException;

@Path("/bestellungen")
@Produces(APPLICATION_JSON)
@Consumes
public class BestellungResource {
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperBestellposition uriHelperBestellposition;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private BestellpositionService bsp;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id) {
		final Bestellung bestellung = bs.findBestellungById(id);
		if (bestellung == null) {
			throw new NotFoundException("Keine Bestellung mit der ID " + id + " gefunden.");
		}
		
		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		return bestellung;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}/bestellpositionen")
	public Collection<Bestellposition> findBestellpositionenByBestellungId(@PathParam("id") Long bestellungId) {
		final Collection<Bestellposition> bestellpositionen = bsp.findBestellpositionenByBestellungId(bestellungId);
		if (bestellpositionen.isEmpty()) {
			throw new NotFoundException("Zur ID " + bestellungId + " wurden keine Bestellpositionen gefunden");
		}
		
		// URLs innerhalb der gefundenen Bestellpositionen anpassen
		for (Bestellposition bestellposition : bestellpositionen) {
			uriHelperBestellposition.updateUriBestellposition(bestellposition, uriInfo); 
		}
		
		return bestellpositionen;
	}
}
