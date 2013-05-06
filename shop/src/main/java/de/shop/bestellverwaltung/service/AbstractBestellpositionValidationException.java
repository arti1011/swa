package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellposition;

public abstract class AbstractBestellpositionValidationException extends AbstractBestellpositionServiceException {
	private static final long serialVersionUID = -6924234959157503601L;
	private final Collection<ConstraintViolation<Bestellposition>> violations;
	
	public AbstractBestellpositionValidationException(Collection<ConstraintViolation<Bestellposition>> violations) {
		super("Violations: " + violations);
		this.violations = violations;
	}
	
	public Collection<ConstraintViolation<Bestellposition>> getViolations() {
		return violations;
	}
}