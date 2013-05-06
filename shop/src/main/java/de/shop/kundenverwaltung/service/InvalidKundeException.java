package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.AbstractKunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
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
