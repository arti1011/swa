package de.shop.artikelverwaltung.rest;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.SELF_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.NotFoundException;
import de.shop.util.rest.UriHelper;


@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
@RequestScoped
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	// public fuer Testklassen
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_BEZEICHNUNG_QUERY_PARAM = "bezeichnung";
	public static final String ARTIKEL_PREIS_QUERY_PARAM = "preis";
	public static final String ARTIKEL_FARBE_QUERY_PARAM = "farbe";
	public static final String ARTIKEL_KATEGORIE_QUERY_PARAM = "kategorie";
	
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	@Inject
	private ArtikelService as;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@GET
	@Produces(TEXT_XML)
	@Path("version")
	public String getVersion() {
		return "1.0";
	}
	
	
	//TODO Bean Validation für Methodenparameter, z.B: @Valid, @Pattern, ...
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findArtikelById(@PathParam("id") Long id, @Context UriInfo uirInfo) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			throw new NotFoundException("Kein Artikel mit der ID " + id + " gefunden.");
		}
		
		return Response.ok(artikel)
                .links(getTransitionalLinks(artikel, uriInfo))
                .build();
	}

	private Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
                              .rel(SELF_LINK)
                              .build();

		return new Link[] { self };
	}
		
	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	@Transactional
	public Response createArtikel(@Valid Artikel artikel) {
		//@SuppressWarnings("unused")
		artikel.setId(KEINE_ID);
		artikel = as.createArtikel(artikel);		
		LOGGER.trace(artikel);
		
		return Response.created(getUriArtikel(artikel, uriInfo)).build();
	}
	
	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	@Transactional
	public void updateArtikel(Artikel artikel) {
		// Vorhandenen Kunden ermitteln
		final Artikel origArtikel = as.findArtikelById(artikel.getId());
		if (origArtikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("Artikel vorher: %s", origArtikel);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origArtikel.setValues(artikel);
		LOGGER.tracef("Kunde nachher: %s", origArtikel);
		
		// Update durchfuehren
		artikel = as.updateArtikel(origArtikel);
		if (artikel == null) {
			
			final String msg = "Kein Artikel gefunden mit der ID " + origArtikel.getId();
			throw new NotFoundException(msg);
		}
	
	}

	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
	}
}
