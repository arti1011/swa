package de.shop.bestellverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.Log;
import de.shop.util.Mock;
import de.shop.util.ValidatorProvider;

@Log
public class BestellpositionServiceImpl implements BestellpositionService, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -519454062519816252L;

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
	
	@Override
	public Bestellposition findBestellpositionById(Long id) {
		// TODO Datenbanzugriffsschicht statt Mock
		return Mock.findBestellpositionById(id);
	}

	@Override
	public List<Bestellposition> findBestellpositionenByBestellungId(Long bestellungId) {
		// TODO Datenbanzugriffsschicht statt Mock
		return Mock.findBestellpositionenByBestellungId(bestellungId);
	}

	@Override
	public Bestellposition createBestellposition(Bestellposition bestellposition, Bestellung bestellung, Locale locale) {
		validateBestellposition(bestellposition, locale, Default.class);
		
		// TODO Datenbanzugriffsschicht statt Mock
		bestellposition = Mock.createBestellposition(bestellposition, bestellung);
		
		return bestellposition;
	}
	
	private void validateBestellposition(Bestellposition bestellposition, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellposition>> violations = validator.validate(bestellposition);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debugf("createBestellposition: violations=%s", violations);
			throw new InvalidBestellpositionException(bestellposition, violations);
		}
	}
}

