package de.shop.artikelverwaltung.rest;

import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_ID_URI;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.PASSWORD_ADMIN;
import static de.shop.util.TestConstants.USERNAME_ADMIN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

import static java.util.Locale.GERMAN;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.fest.assertions.api.Assertions.assertThat;


import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(301);
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(302);

	private static final String NEUE_BEZEICHNUNG = "Schrank";
	private static final String NEUE_BEZEICHNUNG_UPDATE = "Wandschrank";

	private static final BigDecimal NEUER_PREIS = new BigDecimal("100.00");

	private static final boolean VERFUEGBAR = true;

	@Test
	@InSequence(1)
	public void validate() {
		assertThat(true).isTrue();
	}

	@Test
	@InSequence(2)
	public void findArtikelById() {
		LOGGER.finer("BEGINN");

		final Long artikelId = ARTIKEL_ID_VORHANDEN;

		final Response response = getHttpsClient().target(ARTIKEL_ID_URI)
				.resolveTemplate(ARTIKEL_ID_PATH_PARAM, artikelId).request().accept(APPLICATION_JSON).get();
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		final Artikel artikel = response.readEntity(Artikel.class);
		assertThat(artikel.getId()).isEqualTo(artikelId);

		LOGGER.finer("ENDE");
	}

	@Test
	@InSequence(3)
	public void findArtikelByIdNichtVorhanden() {
		LOGGER.finer("BEGINN");

		final Long artikelId = ARTIKEL_ID_NICHT_VORHANDEN;

		final Response response = getHttpsClient().target(ARTIKEL_ID_URI)
				.resolveTemplate(ArtikelResource.ARTIKEL_ID_PATH_PARAM, artikelId).request()
				.acceptLanguage(GERMAN).get();
		assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
		final String fehlermeldung = response.readEntity(String.class);
		assertThat(fehlermeldung).startsWith("Kein Artikel mit der ID").endsWith("gefunden.");

		LOGGER.finer("ENDE");
	}

	@Test
	@InSequence(7)
	public void createArtikel() {
		LOGGER.finer("BEGINN");

		final String bezeichnung = NEUE_BEZEICHNUNG;
		final BigDecimal preis = NEUER_PREIS;
		final boolean verfuegbar = VERFUEGBAR;


		final Artikel artikel = new Artikel();
		artikel.setBezeichnung(bezeichnung);
		artikel.setPreis(preis);
		artikel.setAusgesondert(verfuegbar);
		

		final Response response = getHttpsClient(USERNAME_ADMIN, PASSWORD_ADMIN).target(ARTIKEL_URI)
				.request().post(json(artikel));

		assertThat(response.getStatus()).isEqualTo(HTTP_CREATED);
		final String location = response.getLocation().toString();
		response.close();

		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id).isPositive();

		LOGGER.finer("ENDE");

	}

	@Test
	@InSequence(9)
	public void updateArtikel() {
		LOGGER.finer("BEGINN");

		final Long artikelId = ARTIKEL_ID_UPDATE;
		final String neueBezeichnung = NEUE_BEZEICHNUNG_UPDATE;

		Response response = getHttpsClient().target(ARTIKEL_ID_URI)
				.resolveTemplate(ArtikelResource.ARTIKEL_ID_PATH_PARAM, artikelId).request()
				.accept(APPLICATION_JSON).get();
		final Artikel artikel = response.readEntity(Artikel.class);
		assertThat(artikel.getId()).isEqualTo(artikelId);

		artikel.setBezeichnung(neueBezeichnung);

		response = getHttpsClient(USERNAME_ADMIN, PASSWORD_ADMIN).target(ARTIKEL_URI).request()
				.accept(APPLICATION_JSON).put(json(artikel));

		assertThat(response.getStatus()).isEqualTo(HTTP_OK);

		response.close();

		LOGGER.finer("ENDE");
	}
}
