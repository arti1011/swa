package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static de.shop.util.Constants.KEINE_ID;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
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

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.interceptor.Log;
import de.shop.util.NotFoundException;


@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Log
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpHeaders headers;

	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public AbstractKunde findKundeById(@PathParam("id") Long id) {
		final Locale locale = localeHelper.getLocale(headers);
		final AbstractKunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
	
		// URLs innerhalb des gefundenen Kunden anpassen
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		
		return kunde;
	}
	
	@GET
	public Collection<AbstractKunde> findKundenByNachname(@QueryParam("nachname") @DefaultValue("") String nachname) {
		Collection<AbstractKunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				final String msg = "Keine Kunden vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			final Locale locale = localeHelper.getLocale(headers);
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, locale);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname " + nachname;
				throw new NotFoundException(msg);
			}
		}
		
		// URLs innerhalb der gefundenen Kunden anpassen
		for (AbstractKunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}
		
		return kunden;
	}
	
	/**
	 * Mit der URL /kunden/{id}/bestellungen die Bestellungen zu eine Kunden ermitteln
	 * @param kundeId ID des Kunden
	 * @return Objekt mit Bestellungsdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(@PathParam("id") Long kundeId) {
		final Locale locale = localeHelper.getLocale(headers);
		
		final AbstractKunde kunde = ks.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN, locale);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde mit der ID " + kundeId + " gefunden.");
		}
		
		final Collection<Bestellung> bestellungen = bs.findBestellungenByKunde(kunde);
		
		// URLs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}
		
		return bestellungen;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungenIds")
	public Collection<Long> findBestellungenIdsByKundeId(@PathParam("id") Long kundeId) {
		final Collection<Bestellung> bestellungen = findBestellungenByKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Kunde gefunden mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}
		
		final int anzahl = bestellungen.size();
		final Collection<Long> bestellungenIds = new ArrayList<>(anzahl);
		for (Bestellung bestellung : bestellungen) {
			bestellungenIds.add(bestellung.getId());
		}
		
		return bestellungenIds;
	}
	
	/**
	 * Mit der URL /kunden einen Privatkunden per POST anlegen.
	 * @param kunde neuer Kunde
	 * @return Response-Objekt mit URL des neuen Kunden
	 */
	@POST
	@Consumes(APPLICATION_JSON)
	@Transactional
	@Produces
	public Response createKunde(AbstractKunde kunde) {
		final Locale locale = localeHelper.getLocale(headers);

		kunde.setId(KEINE_ID);
		
		
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
			adresse.setId(KEINE_ID);
		}
		kunde.setBestellungenUri(null);
		kunde.setAdresse(adresse);
		
		kunde = ks.createKunde(kunde, locale);
		LOGGER.tracef("Kunde: %s", kunde);
		
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	/**
	 * Mit der URL /kunden einen Kunden per PUT aktualisieren
	 * @param kunde zu aktualisierende Daten des Kunden
	 */
	@PUT
	@Consumes(APPLICATION_JSON)
	@Transactional
	@Produces
	public void updateKunde(AbstractKunde kunde) {
		// Vorhandenen Kunden ermitteln
		final Adresse adresse = kunde.getAdresse();
		if (adresse == null) {
			throw new NotFoundException("Keine Adresse");
		}
		adresse.setKunde(kunde);
		
		final Locale locale = localeHelper.getLocale(headers);
		final AbstractKunde origKunde = ks.findKundeById(kunde.getId(), FetchType.NUR_KUNDE, locale);
		if (origKunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + kunde.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("Kunde vorher: %s", origKunde);
		final Adresse origAdresse = origKunde.getAdresse();
		origAdresse.setValues(adresse);
		// Daten des vorhandenen Kunden ueberschreiben
		origKunde.setValues(kunde);
		LOGGER.tracef("Kunde nachher: %s", origKunde);
		
		// Update durchfuehren
		kunde = ks.updateKunde(origKunde, origAdresse, locale);
		if (kunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getId();
			throw new NotFoundException(msg);
		}
	}
	
	/**
	 * Mit der URL /kunden{id} einen Kunden per DELETE l&ouml;schen
	 * @param kundeId des zu l&ouml;schenden Kunden
	 */
	@Path("{id:[0-9]+}")
	@DELETE
	@Transactional
	@Produces
	public void deleteKunde(@PathParam("id") Long kundeId) {
		final Locale locale = localeHelper.getLocale(headers);
		final AbstractKunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}
		ks.deleteKunde(kunde);
	}
	
	@GET
	@Path("/prefix/nachname/{nachname}")
	public Collection<String> findNachnamenByPrefix(@PathParam("nachname") String nachnamePrefix) {
		final Collection<String> nachnamen = ks.findNachnamenByPrefix(nachnamePrefix);
		return nachnamen;
	}
	

}
