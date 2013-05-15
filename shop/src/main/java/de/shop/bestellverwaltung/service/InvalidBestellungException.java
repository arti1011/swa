package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellung;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte einer Bestellung nicht korrekt sind
 */
public class InvalidBestellungException extends AbstractBestellungValidationException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Bestellung bestellung;
	
	public InvalidBestellungException(Bestellung bestellung,
			                          Collection<ConstraintViolation<Bestellung>> violations) {
		super(violations);
		this.bestellung = bestellung;
	}

	public Bestellung getBestellung() {
		return bestellung;
	}
}
