package de.shop.kundenverwaltung.web;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;

import org.jboss.logging.Logger;
import org.richfaces.push.cdi.Push;
import org.richfaces.ui.iteration.SortOrder;
import org.richfaces.ui.toggle.panelMenu.UIPanelMenuItem;

import de.shop.auth.web.AuthModel;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.HobbyType;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.kundenverwaltung.domain.Privatkunde;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.kundenverwaltung.service.KundeService.OrderByType;
import de.shop.util.AbstractShopException;
import de.shop.util.interceptor.Log;
import de.shop.util.persistence.ConcurrentDeletedException;
import de.shop.util.persistence.File;
import de.shop.util.persistence.FileHelper;
import de.shop.util.web.Captcha;
import de.shop.util.web.Client;
import de.shop.util.web.Messages;

@Named
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)
public class KundeModel implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private static final String JSF_KUNDENVERWALTUNG = "/kundenverwaltung/";
	private static final String JSF_VIEW_KUNDE = JSF_KUNDENVERWALTUNG + "viewKunde";
	private static final String JSF_VIEW_MEINEKUNDENDATEN = JSF_KUNDENVERWALTUNG + "viewMeineKundendaten";
	private static final String JSF_LIST_KUNDEN = JSF_KUNDENVERWALTUNG + "listKunden";
	private static final String JSF_UPDATE_PRIVATKUNDE = JSF_KUNDENVERWALTUNG + "updatePrivatkunde";
	private static final String JSF_UPDATE_FIRMENKUNDE = JSF_KUNDENVERWALTUNG + "updateFirmenkunde";
	private static final String CLIENT_ID_KUNDEID = "form:kundeIdInput";
	private static final String MSG_KEY_KUNDE_NOT_FOUND_BY_ID = "kunde.notFound.id";

	private static final String CLIENT_ID_KUNDEN_NACHNAME = "form:nachname";
	private static final String MSG_KEY_KUNDEN_NOT_FOUND_BY_NACHNAME = "kunde.notFound.nachname";

	private static final String CLIENT_ID_CREATE_EMAIL = "createKundeForm:email";
	private static final String MSG_KEY_EMAIL_EXISTS = ".kunde.emailExists";

	private static final String CLIENT_ID_CREATE_CAPTCHA_INPUT = "createKundeForm:captchaInput";
	private static final String MSG_KEY_CREATE_PRIVATKUNDE_WRONG_CAPTCHA = "kunde.wrongCaptcha";

	private static final Class<?>[] PASSWORD_GROUP = {PasswordGroup.class };

	private static final String CLIENT_ID_UPDATE_EMAIL = "updateKundeForm:email";
	private static final String MSG_KEY_CONCURRENT_UPDATE = "persistence.concurrentUpdate";
	private static final String MSG_KEY_CONCURRENT_DELETE = "persistence.concurrentDelete";

	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;

	@Inject
	private KundeService ks;

	@Inject
	private transient HttpServletRequest request;

	@Inject
	private AuthModel auth;

	@Inject
	@Client
	private Locale locale;

	@Inject
	private Messages messages;

	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerKundeEvent;

	@Inject
	@Push(topic = "updateKunde")
	private transient Event<String> updateKundeEvent;

	@Inject
	private Captcha captcha;

	private Long kundeId;

	@Inject
	private FileHelper fileHelper;

	private AbstractKunde kunde;
	private List<String> hobbies;

	@Pattern(regexp = AbstractKunde.NACHNAME_PATTERN, message = "{kunde.nachname.pattern}")
	private String nachname;

	private List<AbstractKunde> kunden = Collections.emptyList();

	private SortOrder vornameSortOrder = SortOrder.unsorted;
	private String vornameFilter = "";

	private boolean geaendertKunde; // fuer ValueChangeListener
	private Privatkunde neuerPrivatkunde;
	private String captchaInput;

	private transient UIPanelMenuItem menuItemEmail; // eigentlich nicht dynamisch, nur zur Demo

	@PostConstruct
	private void postConstruct() {
		// // Dynamischer Menuepunkt fuer Emails
		// final Application app = facesCtx.getApplication(); // javax.faces.application.Application
		// menuItemEmail = (UIPanelMenuItem) app.createComponent(facesCtx,
		// UIPanelMenuItem.COMPONENT_TYPE,
		// PanelMenuItemRenderer.class.getName());
		// menuItemEmail.setLabel("Email dynamisch");
		// menuItemEmail.setId("kundenverwaltungViewByEmail");
		//
		// // <h:outputLink>
		// // component-family: javax.faces.Output renderer-type: javax.faces.Link
		//
		// //menuGroup = (UIPanelMenuGroup) app.createComponent(facesCtx,
		// // UIPanelMenuGroup.COMPONENT_TYPE,
		// // PanelMenuGroupRenderer.class.getName());
		// //UIPanelMenuItem item = (UIPanelMenuItem) app.createComponent(facesCtx,
		// // UIPanelMenuItem.COMPONENT_TYPE,
		// // PanelMenuItemRenderer.class.getName());
		// //menuGroup.getChildren().add(item);

		LOGGER.debugf("EJB %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("EJB %s wird geloescht", this);
	}

	@Override
	public String toString() {
		return "KundeModel [kundeId=" + kundeId + ", nachname=" + nachname + ", geaendertKunde=" + geaendertKunde + "]";
	}

	public Long getKundeId() {
		return kundeId;
	}

	public void setKundeId(Long kundeId) {
		this.kundeId = kundeId;
	}

	public AbstractKunde getKunde() {
		return kunde;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public List<AbstractKunde> getKunden() {
		return kunden;
	}

	public SortOrder getVornameSortOrder() {
		return vornameSortOrder;
	}

	public void setVornameSortOrder(SortOrder vornameSortOrder) {
		this.vornameSortOrder = vornameSortOrder;
	}

	public void sortByVorname() {
		vornameSortOrder = vornameSortOrder.equals(SortOrder.ascending) ? SortOrder.descending : SortOrder.ascending;
	}

	public String getVornameFilter() {
		return vornameFilter;
	}

	public void setVornameFilter(String vornameFilter) {
		this.vornameFilter = vornameFilter;
	}

	public Privatkunde getNeuerPrivatkunde() {
		return neuerPrivatkunde;
	}

	public String getCaptchaInput() {
		return captchaInput;
	}

	public void setCaptchaInput(String captchaInput) {
		this.captchaInput = captchaInput;
	}

	public void setMenuItemEmail(UIPanelMenuItem menuItemEmail) {
		this.menuItemEmail = menuItemEmail;
	}

	public UIPanelMenuItem getMenuItemEmail() {
		return menuItemEmail;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * 
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	// ggf. Bestellungen ueber den Extended Persistence Context nachladen
	@TransactionAttribute
	@Log
	public String findKundeById() {
		if (kundeId == null) {
			return null;
		}

		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE);
		if (kunde == null) {
			// Kein Kunde zu gegebener ID gefunden
			return findKundeByIdErrorMsg(kundeId.toString());
		}
		if (kunde.getFile() != null) {
			kunde.getFile().getId(); // nachladen
		}

		return JSF_VIEW_KUNDE;
	}
	
	@TransactionAttribute
	@Log
	public String findMeineKundendaten() {
		
		kundeId = Long.valueOf(auth.getUsername());
		
		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE);
		if (kunde == null) {
			// Kein Kunde zu gegebener ID gefunden
			return findKundeByIdErrorMsg(kundeId.toString());
		}
		if (kunde.getFile() != null) {
			kunde.getFile().getId(); // nachladen
		}

		return JSF_VIEW_MEINEKUNDENDATEN;
	}
	
	

	private String findKundeByIdErrorMsg(String id) {
		messages.error(MSG_KEY_KUNDE_NOT_FOUND_BY_ID, locale, CLIENT_ID_KUNDEID, id);
		return null;
	}

	/**
	 * F&uuml;r rich:autocomplete
	 * 
	 * @param idPrefix
	 *            Praefix fuer potenzielle Kunden-IDs
	 * @return Liste der potenziellen Kunden
	 */
	@Log
	public List<AbstractKunde> findKundenByIdPrefix(String idPrefix) {
		List<AbstractKunde> kundenPrefix = null;
		Long id = null;
		try {
			id = Long.valueOf(idPrefix);
		}
		catch (NumberFormatException e) {
			findKundeByIdErrorMsg(idPrefix);
			return null;
		}

		kundenPrefix = ks.findKundenByIdPrefix(id);
		if (kundenPrefix == null || kundenPrefix.isEmpty()) {
			// Kein Kunde zu gegebenem ID-Praefix vorhanden
			findKundeByIdErrorMsg(idPrefix);
			return null;
		}

		return kundenPrefix;
	}

	@Log
	public void loadKundeById() {
		// Request-Parameter "kundeId" fuer ID des gesuchten Kunden
		final String idStr = request.getParameter("kundeId");
		Long id;
		try {
			id = Long.valueOf(idStr);
		}
		catch (NumberFormatException e) {
			return;
		}

		// Suche durch den Anwendungskern
		kunde = ks.findKundeById(id, FetchType.NUR_KUNDE);
		// kunde = ks.findKundeById(id, FetchType.MIT_BESTELLUNGEN);
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * 
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	// Bestellungen ggf. nachladen
	@TransactionAttribute
	@Log
	public String findKundenByNachname() {
		if (nachname == null || nachname.isEmpty()) {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, OrderByType.UNORDERED);
			return JSF_LIST_KUNDEN;
		}

		kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
		return JSF_LIST_KUNDEN;
	}

	/**
	 * F&uuml;r rich:autocomplete
	 * 
	 * @param nachnamePrefix
	 *            Praefix fuer gesuchte Nachnamen
	 * @return Liste der potenziellen Nachnamen
	 */
	@Log
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		// NICHT: Liste von Kunden. Sonst waeren gleiche Nachnamen mehrfach vorhanden.
		final List<String> nachnamen = ks.findNachnamenByPrefix(nachnamePrefix);
		if (nachnamen == null || nachnamen.isEmpty()) {
			messages.error(MSG_KEY_KUNDEN_NOT_FOUND_BY_NACHNAME, locale, CLIENT_ID_KUNDEN_NACHNAME, nachnamePrefix);
			return Collections.emptyList();
		}

		return nachnamen;
	}

	// Bestellungen ggf. nachladen
	@TransactionAttribute
	@Log
	public String details(AbstractKunde ausgewaehlterKunde) {
		if (ausgewaehlterKunde == null) {
			return null;
		}

		kunde = ausgewaehlterKunde;
		kundeId = ausgewaehlterKunde.getId();

		return JSF_VIEW_KUNDE;
	}

	@TransactionAttribute
	@Log
	public String createPrivatkunde() {
		if (!captcha.getValue().equals(captchaInput)) {
			final String outcome = createPrivatkundeErrorMsg(null);
			return outcome;
		}

		// Liste von Strings als Set von Enums konvertieren
		final Set<HobbyType> hobbiesPrivatkunde = new HashSet<>();
		for (String s : hobbies) {
			hobbiesPrivatkunde.add(HobbyType.valueOf(s));
		}
		neuerPrivatkunde.setHobbies(hobbiesPrivatkunde);
		try {
			neuerPrivatkunde = ks.createKunde(neuerPrivatkunde);
		}
		catch (EmailExistsException e) {
			return createPrivatkundeErrorMsg(e);
		}

		// Push-Event fuer Webbrowser
		neuerKundeEvent.fire(String.valueOf(neuerPrivatkunde.getId()));

		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerPrivatkunde.getId();
		kunde = neuerPrivatkunde;
		neuerPrivatkunde = null; // zuruecksetzen
		hobbies = null;

		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	@TransactionAttribute
	@Log
	public String registrieren() {
		if (!captcha.getValue().equals(captchaInput)) {
			final String outcome = createPrivatkundeErrorMsg(null);
			return outcome;
		}

		// Liste von Strings als Set von Enums konvertieren
		final Set<HobbyType> hobbiesPrivatkunde = new HashSet<>();
		for (String s : hobbies) {
			hobbiesPrivatkunde.add(HobbyType.valueOf(s));
		}
		neuerPrivatkunde.setHobbies(hobbiesPrivatkunde);
		try {
			neuerPrivatkunde = ks.createKunde(neuerPrivatkunde);
		}
		catch (EmailExistsException e) {
			return createPrivatkundeErrorMsg(e);
		}

		// Push-Event fuer Webbrowser
		neuerKundeEvent.fire(String.valueOf(neuerPrivatkunde.getId()));

		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerPrivatkunde.getId();
		kunde = neuerPrivatkunde;
		neuerPrivatkunde = null; // zuruecksetzen
		hobbies = null;

		return JSF_VIEW_MEINEKUNDENDATEN + JSF_REDIRECT_SUFFIX;
	}

	private String createPrivatkundeErrorMsg(AbstractShopException e) {
		if (e == null) {
			messages.error(MSG_KEY_CREATE_PRIVATKUNDE_WRONG_CAPTCHA, locale, CLIENT_ID_CREATE_CAPTCHA_INPUT);
		}
		else {
			final Class<?> exceptionClass = e.getClass();
			if (EmailExistsException.class.equals(exceptionClass)) {
				final EmailExistsException e2 = EmailExistsException.class.cast(e);
				messages.error(MSG_KEY_EMAIL_EXISTS, locale, CLIENT_ID_CREATE_EMAIL, e2.getEmail());
			}
			else {
				throw new RuntimeException(e);
			}
		}

		return null;
	}

	public void createEmptyPrivatkunde() {
		captchaInput = null;

		if (neuerPrivatkunde != null) {
			return;
		}

		neuerPrivatkunde = new Privatkunde();
		final Adresse adresse = new Adresse();
		adresse.setKunde(neuerPrivatkunde);
		neuerPrivatkunde.setAdresse(adresse);

		final int anzahlHobbies = HobbyType.values().length;
		hobbies = new ArrayList<>(anzahlHobbies);
	}

	/**
	 * https://issues.jboss.org/browse/WFLY-678 http://community.jboss.org/thread/169487
	 * 
	 * @return Array mit PasswordGroup.class
	 */
	public Class<?>[] getPasswordGroup() {
		return PASSWORD_GROUP.clone();
	}

	/**
	 * Hobbies bei preRenderView als Liste von Strings fuer JSF aufbereiten, wenn ein existierender Privatkunde in
	 * updatePrivatkunde.xhtml aktualisiert wird
	 */
	public void hobbyTypeToString() {
		if (!kunde.getClass().equals(Privatkunde.class)) {
			return;
		}

		final Privatkunde privatkunde = Privatkunde.class.cast(kunde);

		hobbies = new ArrayList<>(HobbyType.values().length);
		final Set<HobbyType> hobbiesPrivatkunde = privatkunde.getHobbies();
		for (HobbyType h : hobbiesPrivatkunde) {
			hobbies.add(h.name());
		}
	}

	/**
	 * Verwendung als ValueChangeListener bei updatePrivatkunde.xhtml und updateFirmenkunde.xhtml
	 * 
	 * @param e
	 *            Ereignis-Objekt mit der Aenderung in einem Eingabefeld, z.B. inputText
	 */
	public void geaendert(ValueChangeEvent e) {
		if (geaendertKunde) {
			return;
		}

		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertKunde = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertKunde = true;
		}
	}

	@TransactionAttribute
	@Log
	public String update() {
		auth.preserveLogin();

		if (!geaendertKunde || kunde == null) {
			return JSF_INDEX;
		}

		// Hobbies konvertieren: String -> HobbyType
		if (kunde.getClass().equals(Privatkunde.class)) {
			final Privatkunde privatkunde = Privatkunde.class.cast(kunde);
			final Set<HobbyType> hobbiesPrivatkunde = new HashSet<>();
			for (String s : hobbies) {
				hobbiesPrivatkunde.add(HobbyType.valueOf(s));
			}
			privatkunde.setHobbies(hobbiesPrivatkunde);
		}

		LOGGER.tracef("Aktualisierter Kunde: %s", kunde);
		try {
			kunde = ks.updateKunde(kunde, false);
		}
		catch (EmailExistsException | ConcurrentDeletedException | OptimisticLockException e) {
			final String outcome = updateErrorMsg(e, kunde.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateKundeEvent.fire(String.valueOf(kunde.getId()));
		return JSF_INDEX + JSF_REDIRECT_SUFFIX;
	}

	private String updateErrorMsg(RuntimeException e, Class<? extends AbstractKunde> kundeClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (EmailExistsException.class.equals(exceptionClass)) {
			final EmailExistsException e2 = EmailExistsException.class.cast(e);
			messages.error(MSG_KEY_EMAIL_EXISTS, locale, CLIENT_ID_UPDATE_EMAIL, e2.getEmail());
		}
		else if (OptimisticLockException.class.equals(exceptionClass)) {
			messages.error(MSG_KEY_CONCURRENT_UPDATE, locale, null);

		}
		else if (ConcurrentDeletedException.class.equals(exceptionClass)) {
			messages.error(MSG_KEY_CONCURRENT_DELETE, locale, null);
		}
		else {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Log
	public String selectForUpdate(AbstractKunde ausgewaehlterKunde) {
		if (ausgewaehlterKunde == null) {
			return null;
		}

		kunde = ausgewaehlterKunde;

		return Privatkunde.class.equals(ausgewaehlterKunde.getClass()) ? JSF_UPDATE_PRIVATKUNDE
				: JSF_UPDATE_FIRMENKUNDE;
	}

	public String getFilename(File file) {
		if (file == null) {
			return "";
		}

		fileHelper.store(file);
		return file.getFilename();
	}

}
