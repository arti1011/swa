package de.shop.artikelverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class ArtikelServiceException extends AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public ArtikelServiceException(String msg) {
		super(msg);
	}
	
	public ArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
