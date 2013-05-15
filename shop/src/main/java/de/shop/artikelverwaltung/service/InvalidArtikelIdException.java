package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

public class InvalidArtikelIdException extends AbstractArtikelValidationException {
	private static final long serialVersionUID = -8973151010781329074L;
	
	private final Long artikelId;
	
	public InvalidArtikelIdException(Long artikelId, Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}
}
