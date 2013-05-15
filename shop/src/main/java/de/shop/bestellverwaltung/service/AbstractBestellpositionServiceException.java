package de.shop.bestellverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractBestellpositionServiceException extends AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public AbstractBestellpositionServiceException(String msg) {
		super(msg);
	}
	
	public AbstractBestellpositionServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
