package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;

@Path("/bestellungen")
@Produces(APPLICATION_JSON)
@Consumes
@Log
public class BestellungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Inject
	private LocaleHelper localeHelper;
	
	@Context
	private HttpHeaders headers;
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperBestellposition uriHelperBestellposition;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;
	

	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id) {
		final Bestellung bestellung = bs.findBestellungById(id);
		if (bestellung == null) {
			throw new NotFoundException("Keine Bestellung mit der ID " + id + " gefunden.");
		}
		
		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		if (!bestellung.getBestellpositionen().isEmpty()) {
			final List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
			// URLs innerhalb der gefundenen Bestellpositionen anpassen
			for (Bestellposition bestellposition : bestellpositionen) {
				uriHelperBestellposition.updateUriBestellposition(bestellposition, uriInfo); 
			}
		}
		
		
		return bestellung;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createBestellung(Bestellung bestellung) {
		// Schluessel des Kunden extrahieren
		final String kundeUriStr = bestellung.getKundeUri().toString();
		int startPos = kundeUriStr.lastIndexOf('/') + 1;
		final String kundeIdStr = kundeUriStr.substring(startPos);
		Long kundeId = null;
		try {
			kundeId = Long.valueOf(kundeIdStr);
		}
		catch (NumberFormatException e) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeIdStr, e);
		}
		
		final Locale locale = localeHelper.getLocale(headers);
		final AbstractKunde kunde = ks.findKundeById(kundeId, locale);
		bestellung.setKunde(kunde);
		bestellung.setAusgeliefert(false);
		final List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
		//  Artikel ermitteln
		
				for (Bestellposition bp : bestellpositionen) {
					final String artikelUriStr = bp.getArtikelUri().toString();
					startPos = artikelUriStr.lastIndexOf('/') + 1;
					final String artikelIdStr = artikelUriStr.substring(startPos);
					Long artikelId = null;
					try {
						artikelId = Long.valueOf(artikelIdStr);
						final Artikel artikel = as.findArtikelById(artikelId, locale);
						bp.setArtikel(artikel);
					}
					catch (NumberFormatException e) {
						throw new NotFoundException("Kein Artikel vorhanden mit der ID " + artikelIdStr, e);
					}
				}	
				
		bestellung = bs.createBestellung(bestellung, kunde, bestellpositionen, locale);
		final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellung, uriInfo);
		return Response.created(bestellungUri).build();
	}
}
