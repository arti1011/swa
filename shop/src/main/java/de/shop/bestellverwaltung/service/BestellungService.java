package de.shop.bestellverwaltung.service;
import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.interceptor.Log;

@Log
public class BestellungService implements Serializable {
	
	private static final long serialVersionUID = -519454062519816252L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@Inject
	private KundeService ks;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public Bestellung findBestellungById(Long id) {
		final Bestellung bestellung = em.find(Bestellung.class, id);
		return bestellung;
	}
	
	public AbstractKunde findKundeById(Long id) {
		try {
			final AbstractKunde kunde = em.createNamedQuery(Bestellung.FIND_KUNDE_BY_ID, AbstractKunde.class)
                                          .setParameter(Bestellung.PARAM_ID, id)
					                      .getSingleResult();
			return kunde;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Bestellung> findBestellungenByKunde(AbstractKunde kunde) {
		if (kunde == null) {
			return Collections.emptyList();
		}
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                                                                  Bestellung.class)
                                                .setParameter(Bestellung.PARAM_KUNDE, kunde)
				                                .getResultList();
		return bestellungen;
	}
	
	public Bestellung createBestellung(Bestellung bestellung,
            Long kundeId) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		final AbstractKunde kunde = ks.findKundeById(kundeId, KundeService.FetchType.MIT_BESTELLUNGEN);
		return createBestellung(bestellung, kunde);
	}
	
	public Bestellung createBestellung(Bestellung bestellung,
            AbstractKunde kunde) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		if (!em.contains(kunde)) {
			kunde = ks.findKundeById(kunde.getId(), KundeService.FetchType.MIT_BESTELLUNGEN);
		}
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);

		// Vor dem Abspeichern IDs zuruecksetzen:
		// IDs koennten einen Wert != null haben, wenn sie durch einen Web Service uebertragen wurden
		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setPositionId(KEINE_ID);
			LOGGER.tracef("Bestellposition: %s", bp);				
		}

		em.persist(bestellung);
		event.fire(bestellung);

		return bestellung;
	}
	
	public List<Artikel> ladenhueter(int anzahl) {
		final List<Artikel> artikel = em.createNamedQuery(Bestellposition.FIND_LADENHUETER, Artikel.class)
				                        .setMaxResults(anzahl)
				                        .getResultList();
		return artikel;
	}
	
}
