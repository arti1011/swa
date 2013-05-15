package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.ArtikelFarbeType;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.Mock;
import de.shop.util.ValidatorProvider;

@Log
public class ArtikelServiceImpl implements ArtikelService, Serializable {
	
	private static final long serialVersionUID = -5105686816948437276L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public Artikel findArtikelById(Long artikelId, Locale locale) {
		validateArtikelId(artikelId, locale);

		// TODO Datenbanzugriffsschicht statt Mock
		return Mock.findArtikelById(artikelId);
	}
	
	public List<Artikel> findArtikelByBezeichnung(String bezeichnung, Locale locale) {
		validateBezeichnung(bezeichnung, locale);
		// ToDo Datenbankzugriffsschicht statt Mock
		
		return Mock.findArtikelByBezeichnung(bezeichnung);
	}
	
	public List<Artikel> findAllArtikel() {
		// ToDo Datenbankzugriffsschicht statt Mock
		
		return Mock.findAllArtikel();
	}
	
	@Override
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		validateArtikel(artikel, locale, Default.class);
		
		// TODO Datenbanzugriffsschicht statt Mock
		artikel = Mock.createArtikel(artikel);
		
		return artikel;
	}
	
	@Override
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		validateArtikelId(artikel.getId(), locale);
		validateArtikel(artikel, locale, Default.class);
		
		
		// TODO Datenbanzugriffsschicht statt Mock
		artikel = Mock.updateArtikel(artikel);
		
		return artikel;
	}
	
	@Override
	public Artikel deleteArtikel(Long artikelId, Locale locale) {
		validateArtikelId(artikelId, locale);
		
		// TODO Datenbanzugriffsschicht statt Mock
		Artikel artikel = Mock.findArtikelById(artikelId);
		Mock.deleteArtikel(artikel);
		
		return artikel;
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
		artikel.setVerfuegbarkeit(bezeichnung +" ist verfuegbar");
		final Set<ArtikelFarbeType> farben = new HashSet<>();
		farben.add(ArtikelFarbeType.BLAU);
		farben.add(ArtikelFarbeType.SCHWARZ);
		farben.add(ArtikelFarbeType.WEISS);
		artikel.setFarbe(farben);
		artikel.setPreis(new BigDecimal(Long.valueOf(bezeichnung.length())));
		artikel.setArtikelBezeichnung(bezeichnung);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debugf("createArtikel: violations=%s", violations);
			throw new InvalidArtikelException(artikel, violations);
		}
	}
	
	private void validateArtikelId(Long artikelId, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class,
				                                                                           "id",
				                                                                           artikelId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidArtikelIdException(artikelId, violations);
	}
}