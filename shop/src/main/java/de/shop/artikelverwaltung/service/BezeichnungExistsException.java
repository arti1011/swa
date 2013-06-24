package de.shop.artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BezeichnungExistsException extends AbstractArtikelServiceException {
	
	private static final long serialVersionUID = 4312228898237485238L;
	private final String bezeichnung;
	
	public BezeichnungExistsException(String bezeichnung) {
		super("Die Bezeichnung " + bezeichnung + " existiert bereits");
		this.bezeichnung = bezeichnung;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}
}
