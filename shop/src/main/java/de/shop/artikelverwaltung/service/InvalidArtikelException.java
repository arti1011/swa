package de.shop.artikelverwaltung.service;

 
import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
public class InvalidArtikelException extends AbstractArtikelValidationException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Artikel artikel;
	
	public InvalidArtikelException(Artikel artikel,
			                     Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.artikel = artikel;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	@Override
	public String getMessageKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
