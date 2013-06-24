package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.AbstractKunde;


@ApplicationException(rollback = true)
public class InvalidKundeException extends AbstractKundeValidationException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final AbstractKunde kunde;
	
	public InvalidKundeException(AbstractKunde kunde,
			                     Collection<ConstraintViolation<AbstractKunde>> violations) {
		super(violations);
		this.kunde = kunde;
	}

	public AbstractKunde getKunde() {
		return kunde;
	}
}
