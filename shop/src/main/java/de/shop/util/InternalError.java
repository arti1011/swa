package de.shop.util;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InternalError extends RuntimeException {
	private static final long serialVersionUID = 2310957395861487292L;

	public InternalError(Throwable t) {
		super("Ein interner Fehler ist aufgetreten", t);
	}
	
	public InternalError(String msg) {
		super("Ein interner Fehler ist aufgetreten: " + msg);
	}
}
