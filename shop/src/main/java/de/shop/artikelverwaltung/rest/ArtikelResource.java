package de.shop.artikelverwaltung.rest;

import static de.shop.util.Constants.KEINE_ID;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private LocaleHelper localeHelper;
	
	@Context
	private HttpHeaders headers;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
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
	public Artikel findArtikelById(@PathParam("id") Long id, @Context UriInfo uriInfo) {

		final Locale locale = localeHelper.getLocale(headers);
		final Artikel artikel = as.findArtikelById(id, locale);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		return artikel;
	}
	
	@GET
	public List<Artikel> findArtikelBySuchbegriff(@QueryParam("suchbegriff") 
		@DefaultValue("") String suchbegriff) {
		final Locale locale = localeHelper.getLocale(headers);
		
		List<Artikel> artikelliste = null;
		if ("".equals(suchbegriff)) {
			artikelliste = as.findVerfuegbareArtikel();
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Keine Artikel vorhanden.");
			}
		}
		else {
			artikelliste = as.findArtikelBySuchbegriff(suchbegriff, locale);
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Unter dem Suchbegriff " + suchbegriff + " wurde kein Artikel gefunden.");
			}
		}		
		return artikelliste;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createArtikel(Artikel artikel) {
		
		artikel.setId(KEINE_ID);
		
		final Locale locale = localeHelper.getLocale(headers);
		as.createArtikel(artikel, locale);
		
		return Response.noContent().build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response updateArtikel(Artikel artikel) {
		final Locale locale = localeHelper.getLocale(headers);
		final Artikel origArtikel = as.findArtikelById(artikel.getId(), locale);
		if (origArtikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("Artikel vorher: %s", origArtikel);
		origArtikel.setValues(artikel);
		LOGGER.tracef("Artikel nachher: %s", origArtikel);
		artikel = as.updateArtikel(origArtikel, locale);
		if (artikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + origArtikel.getId();
			throw new NotFoundException(msg);
		}
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public Response deleteArtikel(@PathParam("id") Long artikelId) {
		final Locale locale = localeHelper.getLocale(headers);
		final Artikel artikel = as.findArtikelById(artikelId, locale);
		as.deleteArtikel(artikel, locale);
		
		return Response.noContent().build();
	}
}
