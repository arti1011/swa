package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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
	
	private static final long serialVersionUID = -5105686816948437276L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				                       .getResultList();
		return result;
	}
	
	public Artikel findArtikelById(Long artikelId) {
		final Artikel artikel = em.find(Artikel.class, artikelId);
		return artikel;
	}
	
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		
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
		
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}
	
	public List<Artikel> findArtikelBySuchbegriff(String suchbegriff) {
		if (Strings.isNullOrEmpty(suchbegriff)) {
			final List<Artikel> artikel = findVerfuegbareArtikel();
			return artikel;
		}
				
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_SUCHBEGRIFF, Artikel.class)
				                        .setParameter(Artikel.PARAM_SUCHBEGRIFF, "%" + suchbegriff + "%")
				                        .getResultList();
		return artikel;
	}
	
	public Artikel findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			return null;
		}
		
		try {
		final Artikel artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
								.setParameter(Artikel.PARAM_BEZEICHNUNG, bezeichnung)
								.getSingleResult();
		return artikel;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Artikel> findArtikelByMaxPreis(double preis) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class)
				                        .setParameter(Artikel.PARAM_PREIS, preis)
				                        .getResultList();
		return artikel;
	}
	
	public Artikel createArtikel(Artikel artikel) {
		if (artikel == null) {
			return artikel;
		}
				
		try {
			em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
			  .setParameter(Artikel.PARAM_BEZEICHNUNG, artikel.getArtikelBezeichnung())
			  .getSingleResult();
			throw new BezeichnungExistsException(artikel.getArtikelBezeichnung());
		}
		catch (NoResultException e) {
			// Noch kein Artikel mit dieser Bezeichnung
			LOGGER.trace("Bezeichnung existiert noch nicht");
		}
		
		em.persist(artikel);
		return artikel;
	}
	
	
	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null) {
			return artikel;
		}
		em.detach(artikel);
		
		final Artikel	tmp = findArtikelByBezeichnung(artikel.getArtikelBezeichnung());
		if (tmp != null) {
			em.detach(tmp);
			if (tmp.getId().longValue() != artikel.getId().longValue()) {
				throw new BezeichnungExistsException(artikel.getArtikelBezeichnung());
			}
		}
		em.merge(artikel);
		return artikel;
	}
	


	public void deleteArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return;
		}
		if (!artikel.isVerfuegbar()) {
			return;
		}
		artikel.setVerfuegbar(false);
		em.merge(artikel);
	}
}
