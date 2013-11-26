package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.UPDATE_LINK;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;



import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.ArtikelResource;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposten;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungServiceImpl;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.rest.KundeResource;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.NotFoundException;
import de.shop.util.rest.UriHelper;

@Path("/bestellung")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5", TEXT_PLAIN })
@RequestScoped
@Consumes
@Log
public class BestellungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String NOT_FOUND_ID = "bestellung.notFound.id";
	private static final String NOT_FOUND_USERNAME = "bestellung.notFound.username";
	private static final String NOT_FOUND_ID_ARTIKEL = "artikel.notFound.id";

	@Context
	private UriInfo uriInfo;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelper uriHelper;
	
	@Inject
	private KundeResource kundeResource;
	
	@Inject 
	private ArtikelResource artikelResource;
	
	@Inject
	private BestellungServiceImpl bs;

	@Inject
	private Principal principal;
	
	@GET
	@Produces(TEXT_PLAIN)
	@Path("version")
	public String getVersion() {
		return "1.0";
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findBestellungById(@PathParam("id") Long id) {
		final Bestellung bestellung = bs.findBestellungById(id);
		
		if (bestellung == null) {
			throw new NotFoundException(NOT_FOUND_ID, id);
		}
		
		// URLs innerhalb der gefundenen Bestellung anpassen
		setStructuralLinks(bestellung, uriInfo);
		
		// Link-Header setzen
		return Response.ok(bestellung)
					   .links(getTransitionalLinks(bestellung, uriInfo))
					   .build();
			
	}
		
	public Link[] getTransitionalLinks(Bestellung bestellung, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriBestellung(bestellung, uriInfo))
							  .rel(SELF_LINK)
							  .build();
		final Link add = Link.fromUri(uriHelper.getUri(BestellungResource.class, uriInfo))
				             .rel(ADD_LINK)
				             .build();
		final Link update = Link.fromUri(uriHelper.getUri(BestellungResource.class, uriInfo))
				                .rel(UPDATE_LINK)
				                .build();
		
				
		return new Link[] {self, add, update };
	}

	public void setStructuralLinks(Bestellung bestellung, UriInfo uriInfo) {
		// URI fuer den Kunden setzen
		final AbstractKunde kunde = bestellung.getKunde();
		if (kunde != null) {
			final URI kundeUri = kundeResource.getUriKunde(bestellung.getKunde(), uriInfo);
			bestellung.setKundeUri(kundeUri);
		}
			
		final List<Bestellposten> bestellposten = bestellung.getBestellposten();
		if (bestellposten != null && !bestellposten.isEmpty()) {
			for (Bestellposten bp : bestellposten) {
				final URI artikelUri = artikelResource.getUriArtikel(bp.getArtikel(), uriInfo);
				bp.setArtikelUri(artikelUri);
			}
			
		}
		LOGGER.trace(bestellung);
		
	}

	@GET
	@Path("{id:[1-9][0-9]*}/kunde")
	public Response findKundeByBestellungId(@PathParam("id") Long id) {
		final AbstractKunde kunde = bs.findKundeById(id);
		if (kunde == null) {
			throw new NotFoundException(NOT_FOUND_USERNAME, id);
		}

		kundeResource.setStructuralLinks(kunde, uriInfo);
		
		//Link Header setzen
		return Response.ok(kunde)
					   .links(kundeResource.getTransitionalLinks(kunde, uriInfo))
					   .build();
	}

	
	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Transactional
	public Response createBestellung(@Valid Bestellung bestellung) {
		if (bestellung == null) {
			return null;
		}
		
		// Username aus dem Principal ermitteln
		final String username = principal.getName();
		
		
		// persistente Artikel ermitteln (IDs)
		final Collection<Bestellposten> bestellposten = bestellung.getBestellposten();
		final List<Long> artikelIds = new ArrayList<>(bestellposten.size());
		for (Bestellposten bp : bestellposten) {
			final URI artikelUri = bp.getArtikelUri();
			if (artikelUri == null) {
				continue;
			}
			final String artikelUriStr = artikelUri.toString();
			final int startPos = artikelUriStr.lastIndexOf('/') + 1;
			final String artikelIdStr = artikelUriStr.substring(startPos);
			Long artikelId = null;
			try {
				artikelId = Long.valueOf(artikelIdStr);
			}
			catch (NumberFormatException ignore) {
				// Ungueltige Artikel-ID: wird nicht beruecksichtigt
				continue;
			}
			artikelIds.add(artikelId);
		}
		
		if (artikelIds.isEmpty()) {
			// keine einzige gueltige Artikel-ID
			String artikelId = null;
			for (Bestellposten bp : bestellposten) {
				final URI artikelUri = bp.getArtikelUri();
				if (artikelUri == null) {
					continue;
				}
				final String artikelUriStr = artikelUri.toString();
				final int startPos = artikelUriStr.lastIndexOf('/') + 1;
				artikelId = artikelUriStr.substring(startPos);
				break;
			}
			throw new NotFoundException(NOT_FOUND_ID_ARTIKEL, artikelId);
		}

		final List<Artikel> gefundeneArtikel = as.findArtikelByIds(artikelIds);
		if (gefundeneArtikel.isEmpty()) {
			throw new NotFoundException(NOT_FOUND_ID_ARTIKEL, artikelIds.get(0));
		}
		
		// Bestellpositionen haben URLs fuer persistente Artikel.
		// Diese persistenten Artikel wurden in einem DB-Zugriff ermittelt (s.o.)
		// Fuer jede Bestellposition wird der Artikel passend zur Artikel-URL bzw. Artikel-ID gesetzt.
		// Bestellpositionen mit nicht-gefundene Artikel werden eliminiert.
		int i = 0;
		final List<Bestellposten> neueBestellpositionen = new ArrayList<>(bestellposten.size());
		for (Bestellposten bp : bestellposten) {
			// Artikel-ID der aktuellen Bestellposition (s.o.):
			// artikelIds haben gleiche Reihenfolge wie bestellpositionen
			final long artikelId = artikelIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel.getId().longValue() == artikelId) {
					// Der Artikel wurde gefunden
					bp.setArtikel(artikel);
					neueBestellpositionen.add(bp);
					break;					
				}
			}
		}
		bestellung.setBestellpositionen(neueBestellpositionen);
		
		bestellung = bs.createBestellung(bestellung, username);
		if (bestellung == null) {
			throw new NotFoundException(NOT_FOUND_USERNAME, username);
		}
		LOGGER.trace(bestellung);
			
		return Response.created(getUriBestellung(bestellung, uriInfo))
								.build();
	}
		
	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		return uriHelper.getUri(BestellungResource.class, "findBestellungById", bestellung.getId(), uriInfo);
	}
	
}
