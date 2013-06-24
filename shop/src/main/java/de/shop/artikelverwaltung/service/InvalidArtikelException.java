package de.shop.artikelverwaltung.service;
import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
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
}
