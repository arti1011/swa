package de.shop.kundenverwaltung.rest;

import javax.ejb.ApplicationException;

import de.shop.kundenverwaltung.service.AbstractKundeServiceException;

@ApplicationException(rollback = true)
public class InvalidDateException extends AbstractKundeServiceException {
	private static final long serialVersionUID = 2113917506853352685L;
	
	private final String invalidDate;
	
	public InvalidDateException(String invalidDate) {
		super("Ungueltiges Datum: " + invalidDate);
		this.invalidDate = invalidDate;
	}
	
	public InvalidDateException(String invalidDate, Exception e) {
		super("Ungueltiges Datum: " + invalidDate, e);
		this.invalidDate = invalidDate;
	}
	
	public String getInvalidDate() {
		return invalidDate;
	}
}
