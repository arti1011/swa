package de.shop.artikelverwaltung.web;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Pattern;

import org.jboss.logging.Logger;
import org.richfaces.push.cdi.Push;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.artikelverwaltung.service.BezeichnungExistsException;
import de.shop.auth.web.AuthModel;
import de.shop.util.AbstractShopException;
import de.shop.util.interceptor.Log;
import de.shop.util.web.Captcha;
import de.shop.util.web.Client;
import de.shop.util.web.Messages;

@Named
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)
public class ArtikelModel implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private static final String JSF_ARTIKELVERWALTUNG = "/artikelverwaltung/";
	private static final String JSF_VIEW_ARTIKEL = JSF_ARTIKELVERWALTUNG + "viewArtikel";
	private static final String JSF_LIST_ARTIKEL = JSF_ARTIKELVERWALTUNG + "listArtikel";
	private static final String JSF_UPDATE_ARTIKEL = JSF_ARTIKELVERWALTUNG + "updateArtikel";

	private static final String FLASH_ARTIKEL = "artikel";
	private static final int ANZAHL_LADENHUETER = 5;

	private static final String CLIENT_ID_ARTIKELID = "form:artikelIdInput";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_ID = "artikel.notFound.id";

	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";

	private static final String CLIENT_ID_CREATE_BEZEICHNUNG = "createArtikelForm:bezeichnung";
	private static final String MSG_KEY_BEZEICHNUNG_EXISTS = ".artikel.bezeichnungExists";

	private static final String CLIENT_ID_CREATE_CAPTCHA_INPUT = "createArtikelForm:captchaInput";
	private static final String MSG_KEY_CREATE_ARTIKEL_WRONG_CAPTCHA = "artikel.wrongCaptcha";

	private static final String CLIENT_ID_UPDATE_BEZEICHNUNG = "updateArtikelForm:bezeichnung";
	private static final String MSG_KEY_CONCURRENT_UPDATE = "persistence.concurrentUpdate";

	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;

	@Inject
	private ArtikelService as;

	@Inject
	private AuthModel auth;

	@Inject
	@Client
	private Locale locale;

	@Inject
	private Messages messages;

	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerArtikelEvent;

	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;

	@Inject
	private Captcha captcha;

	private Long artikelId;

	private Artikel artikel;

	private List<Artikel> ladenhueter;

	@Inject
	private Flash flash;

	@Pattern(regexp = Artikel.BEZEICHNUNG_PATTERN, message = "{artikel.bezeichnung.pattern}")
	private String bezeichnung;

	@Inject
	private transient HttpSession session;

	private boolean geaendertArtikel;
	private Artikel neuerArtikel;
	private String captchaInput;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	@Override
	public String toString() {
		return "ArtikelModel [bezeichnung=" + bezeichnung + "]";
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	public List<Artikel> getLadenhueter() {
		return ladenhueter;
	}

	public String getCaptchaInput() {
		return captchaInput;
	}

	public void setCaptchaInput(String captchaInput) {
		this.captchaInput = captchaInput;
	}

	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}

	@TransactionAttribute
	@Log
	public String findArtikelById() {
		if (artikelId == null) {
			return null;
		}
		artikel = as.findArtikelById(artikelId);
		if (artikel == null) {
			return findArtikelByIdErrorMsg(artikelId.toString());
		}

		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}

	private String findArtikelByIdErrorMsg(String id) {
		messages.error(MSG_KEY_ARTIKEL_NOT_FOUND_BY_ID, locale, CLIENT_ID_ARTIKELID, id);
		return null;
	}

	@TransactionAttribute
	@Log
	public String findArtikelByBezeichnung() {
		final List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}

	@Log
	public void loadLadenhueter() {
		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
	}

	@Log
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) == null) {
			final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
			session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		}

		return JSF_SELECT_ARTIKEL;
	}

	@TransactionAttribute
	@Log
	public String createArtikel() {
		if (!captcha.getValue().equals(captchaInput)) {
			final String outcome = createArtikelErrorMsg(null);
			return outcome;
		}

		try {
			neuerArtikel = as.createArtikel(neuerArtikel);
		}
		catch (BezeichnungExistsException e) {
			return createArtikelErrorMsg(e);
		}

		// Push-Event fuer Webbrowser
		neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getId()));

		// Aufbereitung fuer viewArtikel.xhtml
		artikelId = neuerArtikel.getId();
		artikel = neuerArtikel;
		neuerArtikel = null; // zuruecksetzen

		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}

	private String createArtikelErrorMsg(AbstractShopException e) {
		if (e == null) {
			messages.error(MSG_KEY_CREATE_ARTIKEL_WRONG_CAPTCHA, locale, CLIENT_ID_CREATE_CAPTCHA_INPUT);
		}
		else {
			final Class<?> exceptionClass = e.getClass();
			if (BezeichnungExistsException.class.equals(exceptionClass)) {
				final BezeichnungExistsException e2 = BezeichnungExistsException.class.cast(e);
				messages.error(MSG_KEY_BEZEICHNUNG_EXISTS, locale, CLIENT_ID_CREATE_BEZEICHNUNG, e2.getBezeichnung());
			}
			else {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public void createEmptyArtikel() {
		captchaInput = null;

		if (neuerArtikel != null) {
			return;
		}
		neuerArtikel = new Artikel();
	}

	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}

		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;
		}
	}

	@TransactionAttribute
	@Log
	public String update() {
		if (!captcha.getValue().equals(captchaInput)) {
			final String outcome = createArtikelErrorMsg(null);
			return outcome;
		}

		auth.preserveLogin();

		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}

		LOGGER.tracef("Aktualisierter Artikel: %s", artikel);
		try {
			artikel = as.updateArtikel(artikel);
		}
		catch (BezeichnungExistsException | OptimisticLockException e) {
			final String outcome = updateErrorMsg(e, artikel.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getId()));
		return JSF_INDEX + JSF_REDIRECT_SUFFIX;
	}

	private String updateErrorMsg(RuntimeException e, Class<? extends Artikel> artikelClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (BezeichnungExistsException.class.equals(exceptionClass)) {
			final BezeichnungExistsException e2 = BezeichnungExistsException.class.cast(e);
			messages.error(MSG_KEY_BEZEICHNUNG_EXISTS, locale, CLIENT_ID_UPDATE_BEZEICHNUNG, e2.getBezeichnung());
		}
		else if (OptimisticLockException.class.equals(exceptionClass)) {
			messages.error(MSG_KEY_CONCURRENT_UPDATE, locale, null);

		}
		else {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Log
	public String selectForUpdate(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}

		artikel = ausgewaehlterArtikel;

		return JSF_UPDATE_ARTIKEL;
	}
}
