package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.service.InvalidBestellpositionException;
import de.shop.util.Log;


@Provider
@ApplicationScoped
@Log
public class InvalidBestellpositionExceptionMapper implements ExceptionMapper<InvalidBestellpositionException> {
	private static final String NEWLINE = System.getProperty("line.separator");

	@Override
	public Response toResponse(InvalidBestellpositionException e) {
		final Collection<ConstraintViolation<Bestellposition>> violations = e.getViolations();
		final StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<Bestellposition> v : violations) {
			sb.append(v.getMessage());
			sb.append(NEWLINE);
		}
		
		final String responseStr = sb.toString();
		final Response response = Response.status(CONFLICT)
		                                  .type(TEXT_PLAIN)
		                                  .entity(responseStr)
		                                  .build();
		return response;
	}

}
