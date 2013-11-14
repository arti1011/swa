package de.shop.artikelverwaltung.rest;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.NotFoundException;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.UriHelper;

@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	// public fuer Testklassen
	public static final String ARTIKEL_BEZEICHNUHNG_QUERY_PARAM = "artikelBezeichnung";
	
	private static final String NOT_FOUND_ID = "artikel.notFound.id";
	private static final String NOT_FOUND_BEZEICHNUNG = "artikel.notFound.bezeichnung";
	private static final String NO_ARTICLES_EXCEPTION = "artikel.notFound.AnyArticle";
	
	@Inject
	private UriHelper uriHelper;
	
	@Context
	private UriInfo uriInfo;
	
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
	@Path("{id:[1-9][0-9]*}")
	public Response findArtikelById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			throw new NotFoundException(NOT_FOUND_ID, id);
		}

		return Response.ok(artikel)
	                   .links(getTransitionalLinks(artikel, uriInfo))
	                   .build();
	}
	
	@GET
	public Response findArtikelBySuchbegriff(@QueryParam(ARTIKEL_BEZEICHNUHNG_QUERY_PARAM)
	                           String bezeichnung) {
		List<Artikel> artikelliste = null;
		// TODO Mehrere Query-Parameter koennen angegeben sein
		if (!Strings.isNullOrEmpty(bezeichnung)) {
			artikelliste = as.findArtikelBySuchbegriff(bezeichnung);
			if (artikelliste.isEmpty()) {
				throw new NotFoundException(NOT_FOUND_BEZEICHNUNG, bezeichnung);
			}
			
		}
		else {
			artikelliste = as.findVerfuegbareArtikel();
		}
		
		if (artikelliste == null || artikelliste.isEmpty()) {
			throw new NotFoundException(NO_ARTICLES_EXCEPTION);
		}
			

		
		Link[] links = null;
		
		links = getTransitionalLinksArtikelliste(artikelliste, uriInfo);
			
		return Response.ok(artikelliste)
                       .links(links)
                       .build();
	}
	
	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	@Transactional
	public Response createKunde(@Valid Artikel artikel) {
		artikel.setId(KEINE_ID);
		
		artikel = as.createArtikel(artikel);
		LOGGER.trace(artikel);
		
		return Response.created(getUriArtikel(artikel, uriInfo))
				       .build();
	}


	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Transactional
	public Response updateKunde(@Valid Artikel artikel) {
		// Vorhandenen Artikel ermitteln
		final Artikel origArtikel = as.findArtikelById(artikel.getId());
		if (origArtikel == null) {
			throw new NotFoundException(NOT_FOUND_ID, artikel.getId());
		}
		LOGGER.tracef("Artikel vorher = %s", origArtikel);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origArtikel.setValues(artikel);
		LOGGER.tracef("Artikel nachher = %s", origArtikel);
		
		// Update durchfuehren
		artikel = as.updateArtikel(origArtikel);
		
		return Response.ok(artikel)
				       .links(getTransitionalLinks(artikel, uriInfo))
				       .build();
	}

//	@DELETE
//	@Path("{id:[1-9][0-9]*}")
//	@Produces
//	@Transactional
//	public Response deleteArtikel(@PathParam("id") Long artikelId) {
//		final Locale locale = localeHelper.getLocale(headers);
//		final Artikel artikel = as.findArtikelById(artikelId, locale);
//		as.deleteArtikel(artikel, locale);
//		
//		return Response.noContent().build();
//	}
//	
//	private Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
//		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
//                              .rel(SELF_LINK)
//                              .build();
//
//		return new Link[] { self };
//	}
	
	private Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
                              .rel(SELF_LINK)
                              .build();

		return new Link[] { self };
	}
	
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
	}
	
	private Link[] getTransitionalLinksArtikelliste(List<Artikel> artikelliste, UriInfo uriInfo) {
		if (artikelliste == null || artikelliste.isEmpty()) {
			return null;
		}
		
		final Link first = Link.fromUri(getUriArtikel(artikelliste.get(0), uriInfo))
	                           .rel(FIRST_LINK)
	                           .build();
		final int lastPos = artikelliste.size() - 1;
		final Link last = Link.fromUri(getUriArtikel(artikelliste.get(lastPos), uriInfo))
                              .rel(LAST_LINK)
                              .build();
		
		return new Link[] { first, last };
	}
}
