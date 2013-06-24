package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;


@Log
public class ArtikelService implements Serializable {
	
	private static final long serialVersionUID = -5105686816948437276L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@PersistenceContext
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
	
	public Artikel findArtikelById(Long artikelId, Locale locale) {
		validateArtikelId(artikelId, locale, IdGroup.class);
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
	
	public List<Artikel> findArtikelBySuchbegriff(String suchbegriff, Locale locale) {
		if (Strings.isNullOrEmpty(suchbegriff)) {
			final List<Artikel> artikel = findVerfuegbareArtikel();
			return artikel;
		}
		validateBezeichnung(suchbegriff, locale);
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_SUCHBEGRIFF, Artikel.class)
				                        .setParameter(Artikel.PARAM_SUCHBEGRIFF, "%" + suchbegriff + "%")
				                        .getResultList();
		return artikel;
	}
	
	public Artikel findArtikelByBezeichnung(String bezeichnung, Locale locale) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			return null;
		}
		validateBezeichnung(bezeichnung, locale);
		
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
	
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}
		validateArtikel(artikel, locale, Default.class);
		
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
	
	
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}
		
		validateArtikel(artikel, locale, Default.class, IdGroup.class);
		
		em.detach(artikel);
		
		final Artikel	tmp = findArtikelByBezeichnung(artikel.getArtikelBezeichnung(), locale);
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
	
	private void validateArtikel(Artikel artikel, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debugf("createArtikel: violations=%s", violations);
			throw new InvalidArtikelException(artikel, violations);
		}
	}
	
	private void validateBezeichnung(String bezeichnung, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Artikel artikel = new Artikel();
		artikel.setId(Long.valueOf(bezeichnung.length()));
		artikel.setVerfuegbar(true);
		artikel.setPreis(new BigDecimal(Long.valueOf(bezeichnung.length())));
		artikel.setArtikelBezeichnung(bezeichnung);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debugf("createArtikel: violations=%s", violations);
			throw new InvalidArtikelException(artikel, violations);
		}
	}
	
	private void validateArtikelId(Long artikelId, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class,
				                                                                           "id",
				                                                                           artikelId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidArtikelIdException(artikelId, violations);
	}

}
