package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.interceptor.Log;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 5292529185811096603L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	private transient EntityManager em;

	@Inject
	@NeuerArtikel
	private transient Event<Artikel> event;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	public Artikel findArtikelById(Long id) {
		return em.find(Artikel.class, id);
	}

	public List<Artikel> findVerfuegbareArtikel() {
		return em.createNamedQuery(Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class).getResultList();
	}

	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			return findVerfuegbareArtikel();
		}

		return em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZ, Artikel.class)
				.setParameter(Artikel.PARAM_ARTIKEL_BEZEICHNUNG, "%" + bezeichnung + "%").getResultList();

	}

	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		/*
		 * SELECT a FROM Artikel a WHERE a.id = ? OR a.id = ? OR ...
		 */
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		// final Path<String> idPath = a.get(Artikel_.id); // Metamodel-Klassen funktionieren nicht mit Eclipse

		Predicate pred = null;
		if (ids.size() == 1) {
			// Genau 1 id: kein OR notwendig
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			// Mind. 2x id, durch OR verknuepft
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}

			pred = builder.or(equals);
		}
		criteriaQuery.where(pred);

		return em.createQuery(criteriaQuery).getResultList();
	}

	public List<Artikel> ladenhueter(int anzahl) {
		return em.createNamedQuery(Artikel.FIND_LADENHUETER, Artikel.class).setMaxResults(anzahl).getResultList();
	}

	public <T extends Artikel> T createArtikel(T artikel) {
		if (artikel == null) {
			return artikel;
		}

		// Pruefung, ob ein solcher Artikel schon existiert
		final List<Artikel> tmp = findArtikelByBezeichnung(artikel.getBezeichnung());
		for (Artikel a : tmp) {
			if (a.getBezeichnung().equals(artikel.getBezeichnung()))
				throw new BezeichnungExistsException(artikel.getBezeichnung());
		}

		em.persist(artikel);
		event.fire(artikel);

		return artikel;
	}

	public <T extends Artikel> T updateArtikel(T artikel) {
		if (artikel == null) {
			return null;
		}

		
		em.detach(artikel);

		// Gibt es ein anderes Objekt mit gleicher Bezeichnung?
		List<Artikel> tmp = findArtikelByBezeichnung(artikel.getBezeichnung());
		for (Artikel a : tmp) {
			em.detach(a);
			if ((a.getBezeichnung().equals(artikel.getBezeichnung()))
					&& (a.getId().longValue() != artikel.getId().longValue()))
				throw new BezeichnungExistsException(artikel.getBezeichnung());
		}

		artikel = em.merge(artikel); // OptimisticLockException

		return artikel;
	}
}
