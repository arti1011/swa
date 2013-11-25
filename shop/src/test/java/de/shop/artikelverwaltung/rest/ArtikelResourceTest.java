package de.shop.artikelverwaltung.rest;

import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.TestConstants.ARTIKEL_ID_URI;
import static de.shop.util.TestConstants.ARTIKEL_URI;

import static de.shop.util.TestConstants.KUNDEN_URI;
import static de.shop.util.TestConstants.PASSWORD_MITARBEITER;
import static de.shop.util.TestConstants.PASSWORD_ADMIN;
import static de.shop.util.TestConstants.PASSWORD_FALSCH;
import static de.shop.util.TestConstants.USERNAME_MITARBEITER;
import static de.shop.util.TestConstants.USERNAME_ADMIN;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.filter;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.auth.domain.RolleType;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.KategorieType;
import de.shop.artikelverwaltung.rest.ArtikelResource;
import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
public class ArtikelResourceTest extends AbstractResourceTest{
private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(300);
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(350);
	
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(302);
	
	private static final String BEZEICHNUNG_VORHANDEN = "Tisch";
	private static final String BEZEICHNUNG_NICHT_VORHANDEN = "Falschebez";
	private static final String BEZEICHNUNG_INVALID = "Test9";
	private static final String NEUE_BEZEICHNUNG = "Neuebezeichnung";
    private static final String NEUE_BEZEICHNUNG_UPDATE = "Schrank";
	private static final String NEUE_BEZEICHNUNG_INVALID = "!";
	private static final String NEUE_FARBE = "neuefarbe";
	private static final String NEUE_FARBE_INVALID = "TEST";
	private static final KategorieType NEUE_KATEGORIE = KategorieType.GARTEN;
	private static final BigDecimal NEUER_PREIS = new BigDecimal("120.60");

	
	/*
	private static final String IMAGE_FILENAME = "image.png";
	private static final String IMAGE_PATH_UPLOAD = "src/test/resources/rest/" + IMAGE_FILENAME;
	private static final String IMAGE_MIMETYPE = "image/png";
	private static final String IMAGE_PATH_DOWNLOAD = "target/" + IMAGE_FILENAME;
	private static final Long KUNDE_ID_UPLOAD = Long.valueOf(6);

	private static final String IMAGE_INVALID = "image.bmp";
	private static final String IMAGE_INVALID_PATH = "src/test/resources/rest/" + IMAGE_INVALID;
	private static final String IMAGE_INVALID_MIMETYPE = "image/bmp";
*/

	
	
	@Test
	@InSequence(1)
	public void validate() {
		assertThat(true).isTrue();
	}
	
	
	/*
	@Test
	@InSequence(10)
	public void findArtikelById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		// When
		Response response = getHttpsClient().target(ARTIKEL_ID_URI)
                                            .resolveTemplate(ArtikelResource.ARTIKEL_ID_PATH_PARAM, artikelId)
                                            .request()
                                            .accept(APPLICATION_JSON).get();
	
		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		final Artikel artikel = response.readEntity(Artikel.class);
		assertThat(artikel.getId()).isEqualTo(artikelId);
		assertThat(artikel.getBezeichnung()).isNotEmpty();
		
		// notwenig?
		assertThat(response.getLinks()).isNotEmpty();
		assertThat(response.getLink(SELF_LINK).getUri().toString()).contains(String.valueOf(artikelId));
		
		LOGGER.finer("ENDE");
	}
	*/
	
    @Test
    @InSequence(2)
    public void findArtikelByIdVorhanden() {
            LOGGER.finer("BEGINN");

            final Long artikelId = ARTIKEL_ID_VORHANDEN;

            final Response response = getHttpsClient().target(ARTIKEL_ID_URI)
                            .resolveTemplate(ArtikelResource.ARTIKEL_ID_PATH_PARAM, artikelId)
                            .request().acceptLanguage(GERMAN).get();
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
                            .resolveTemplate(ArtikelResource.ARTIKEL_ID_PATH_PARAM, artikelId)
                            .request().acceptLanguage(GERMAN).get();
            assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
            final String fehlermeldung = response.readEntity(String.class);
            assertThat(fehlermeldung).startsWith("Kein Artikel mit der ID").endsWith("gefunden.");

            LOGGER.finer("ENDE");
    }


	@Test
	@InSequence(20)
	public void findArtikelByBezeichnungVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;

		// When
		Response response = getHttpsClient().target(ARTIKEL_URI)
                                            .queryParam(ArtikelResource.ARTIKEL_BEZEICHNUNG_QUERY_PARAM, bezeichnung)
                                            .request()
                                            .accept(APPLICATION_JSON)
                                            .get();

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		
		final Collection<Artikel> artikel =
				                        response.readEntity(new GenericType<Collection<Artikel>>() { });
		assertThat(artikel).isNotEmpty()
		                  .doesNotContainNull()
		                  .doesNotHaveDuplicates();
		
		assertThat(response.getLinks()).isNotEmpty();
		assertThat(response.getLink(FIRST_LINK)).isNotNull();
		assertThat(response.getLink(LAST_LINK)).isNotNull();

		for (Artikel a : artikel) {
			assertThat(a.getBezeichnung()).isEqualTo(bezeichnung);
			
			assertThat(response.getStatus()).isIn(HTTP_OK, HTTP_NOT_FOUND);
			response.close();           // readEntity() wurde nicht aufgerufen
		}
		
		LOGGER.finer("ENDE");
	}
	
	
	@Test
	@InSequence(21)
	public void findArtikelByBezeichungNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = BEZEICHNUNG_NICHT_VORHANDEN;
		
		// When
		final Response response = getHttpsClient().target(ARTIKEL_URI)
                                                  .queryParam(ArtikelResource.ARTIKEL_BEZEICHNUNG_QUERY_PARAM, bezeichnung)
                                                  .request()
                                                  .acceptLanguage(GERMAN)
                                                  .get();
		
		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
		final String fehlermeldung = response.readEntity(String.class);
		assertThat(fehlermeldung).isEqualTo("Kein Artikel mit der Bezeichnung \"" + bezeichnung + "\" gefunden.");

		LOGGER.finer("ENDE");
	}
	
	
	@Test
	@InSequence(22)
	public void findArtikelByBezeichnungInvalid() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = BEZEICHNUNG_INVALID;
		
		// When
		final Response response = getHttpsClient().target(ARTIKEL_URI)
                                                  .queryParam(ArtikelResource.ARTIKEL_BEZEICHNUNG_QUERY_PARAM, bezeichnung)
                                                  .request()
                                                  .accept(APPLICATION_JSON)
                                                  .acceptLanguage(ENGLISH)
                                                  .get();
		
		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_BAD_REQUEST);
		assertThat(response.getHeaderString("validation-exception")).isEqualTo("true");
		final ViolationReport violationReport = response.readEntity(ViolationReport.class);
		final List<ResteasyConstraintViolation> violations = violationReport.getParameterViolations();
		assertThat(violations).isNotEmpty();
		
		final ResteasyConstraintViolation violation =
				                          filter(violations).with("message")
                                                            .equalsTo("A description must start with exactly one capital letter followed by lower letters.")
                                                            .get()
                                                            .iterator()
                                                            .next();
		assertThat(violation.getValue()).isEqualTo(String.valueOf(bezeichnung));

		LOGGER.finer("ENDE");
	}
	
	
	
	@Test
	@InSequence(40)
	public void createArtikel() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = NEUE_BEZEICHNUNG;
		final String farbe = NEUE_FARBE;
		final KategorieType kategorie = NEUE_KATEGORIE;
		final BigDecimal preis = NEUER_PREIS;
		
		final Artikel artikel = new Artikel(bezeichnung, kategorie, farbe, preis);
		artikel.setBezeichnung(bezeichnung);
		artikel.setKategorie(kategorie);
		artikel.setFarbe(farbe);
		artikel.setPreis(preis);

		LOGGER.finer("ENDE");
	}
	
	
	@Test
	@InSequence(41)
	public void createArtikelInvalid() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = NEUE_BEZEICHNUNG_INVALID;
		final String farbe = NEUE_FARBE_INVALID;
		final KategorieType kategorie= NEUE_KATEGORIE;
		final BigDecimal preis = NEUER_PREIS;

		final Artikel artikel = new Artikel(bezeichnung, kategorie, farbe, preis);
		artikel.setBezeichnung(bezeichnung);
		artikel.setKategorie(kategorie);
		artikel.setFarbe(farbe);
		artikel.setPreis(preis);
		
		// When
		final Response response = getHttpsClient(USERNAME_MITARBEITER, PASSWORD_MITARBEITER).target(KUNDEN_URI)
                                                                    .request()
                                                                    .accept(APPLICATION_JSON)
                                                                    .acceptLanguage(ENGLISH)
                                                                    .post(json(artikel));
		
		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_BAD_REQUEST);
		assertThat(response.getHeaderString("validation-exception")).isEqualTo("true");
		final ViolationReport violationReport = response.readEntity(ViolationReport.class);
		response.close();
		
		
		final List<ResteasyConstraintViolation> violations = violationReport.getParameterViolations();
		assertThat(violations).isNotEmpty();
		
		ResteasyConstraintViolation violation =
				                    filter(violations).with("message")
                                                      .equalsTo("The description must have at least 2 an may only have up to 32 character.")
                                                      .get().iterator().next();
		assertThat(violation.getValue()).isEqualTo(String.valueOf(bezeichnung));
		
		violation = filter(violations).with("message")
                                      .equalsTo("A description have to start with exactly one capital letter followed by lower letters.")
                                      .get().iterator().next();
		assertThat(violation.getValue()).isEqualTo(String.valueOf(bezeichnung));
		
		
		violation = filter(violations).with("message")
                                      .equalsTo("The colour has to be written in lower letters.").get().iterator().next();
		assertThat(violation.getValue()).isEqualTo(farbe);
		
		LOGGER.finer("ENDE");
	}
	
	
    @Test
    @InSequence(9)
    public void updateArtikel() {
            LOGGER.finer("BEGINN");

            final Long artikelId = ARTIKEL_ID_UPDATE;
            final String neueBezeichnung = NEUE_BEZEICHNUNG_UPDATE;

            Response response = getHttpsClient().target(ARTIKEL_ID_URI)
                            .resolveTemplate(ArtikelResource.ARTIKEL_ID_PATH_PARAM, artikelId).request().accept(APPLICATION_JSON)
                            .get();
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
