package de.shop.artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BezeichnungExistsException extends AbstractArtikelServiceException {
	
	//FIXME properties für exception bezeichnung exists fehlt
	private static final long serialVersionUID = 4312228898237485238L;
	private static final String MESSAGE_KEY = "artikel.bezeichnungExists";
	private final String bezeichnung;
	
	public BezeichnungExistsException(String bezeichnung) {
		super("Die Bezeichnung " + bezeichnung + " existiert bereits");
		this.bezeichnung = bezeichnung;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}
	
	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
