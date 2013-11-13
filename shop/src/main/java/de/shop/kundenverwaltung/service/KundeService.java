package de.shop.kundenverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.common.collect.Sets;

import de.shop.auth.domain.RolleType;
import de.shop.auth.service.AuthService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellposition_;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Bestellung_;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.AbstractKunde_;
import de.shop.util.interceptor.Log;
import de.shop.util.persistence.ConcurrentDeletedException;
import de.shop.util.persistence.File;
import de.shop.util.persistence.FileHelper;
import de.shop.util.persistence.MimeType;
import de.shop.util.NoMimeTypeException;

@Log
public class KundeService implements Serializable {
	private static final long serialVersionUID = 3188789767052580247L;
	
	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN
	}
	
	public enum OrderByType {
		UNORDERED,
		ID
	}
private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	// genau 1 Eintrag mit 100 % Fuellgrad
	private static final Map<String, Object> GRAPH_BESTELLUNGEN = new HashMap<>(1, 1);
	
	static {
		GRAPH_BESTELLUNGEN.put("javax.persistence.loadgraph", AbstractKunde.GRAPH_BESTELLUNGEN);
	}
	
	@Inject
	private transient EntityManager em;
	
	@Inject
	private AuthService authService;
	
	@Inject
	private FileHelper fileHelper;
	
	@Inject
	private transient ManagedExecutorService managedExecutorService;

	@Inject
	@NeuerKunde
	private transient Event<AbstractKunde> event;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	public List<AbstractKunde> findAllKunden(FetchType fetch, OrderByType order) {
		final TypedQuery<AbstractKunde> query = OrderByType.ID.equals(order)
				                        ? em.createNamedQuery(AbstractKunde.FIND_KUNDEN_ORDER_BY_ID,
										                      AbstractKunde.class)
				                        : em.createNamedQuery(AbstractKunde.FIND_KUNDEN, AbstractKunde.class);
		switch (fetch) {
			case NUR_KUNDE:
				break;
			case MIT_BESTELLUNGEN:
				query.setHint("javax.persistence.loadgraph", AbstractKunde.GRAPH_BESTELLUNGEN);
				break;
			default:
				break;
		}
		
		final List<AbstractKunde> kunden = query.getResultList();
		return kunden;
	}
	
	public List<AbstractKunde> findKundenByNachname(String nachname, FetchType fetch) {
		List<AbstractKunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = em.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME, AbstractKunde.class)
						   .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
                           .getResultList();
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = em.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
						                     AbstractKunde.class)
						   .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
                           .getResultList();
				break;

			default:
				kunden = em.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME, AbstractKunde.class)
						   .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
                           .getResultList();
				break;
		}
		
		// FIXME https://hibernate.atlassian.net/browse/HHH-8285 : @NamedEntityGraph ab Java EE 7 bzw. JPA 2.1
		//final TypedQuery<AbstractKunde> query = em.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
		//                                                            AbstractKunde.class)
		//				                          .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname);
		//switch (fetch) {
		//	case NUR_KUNDE:
		//		break;
		//	case MIT_BESTELLUNGEN:
		//		query.setHint("javax.persistence.loadgraph", AbstractKunde.GRAPH_BESTELLUNGEN);
		//		break;
		//	case MIT_WARTUNGSVERTRAEGEN:
		//		query.setHint("javax.persistence.loadgraph", AbstractKunde.GRAPH_WARTUNGSVERTRAEGE);
		//		break;
		//	default:
		//		break;
		//}
		//
		//final List<AbstractKunde> kunden = query.getResultList();
		return kunden;
	}
	
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		return em.createNamedQuery(AbstractKunde.FIND_NACHNAMEN_BY_PREFIX, String.class)
				 .setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				 .getResultList();
	}
	
	public AbstractKunde findKundeById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}
		
		AbstractKunde kunde;
		switch (fetch) {
			case NUR_KUNDE:
				kunde = em.find(AbstractKunde.class, id);
				break;
			
			case MIT_BESTELLUNGEN:
				try {
					kunde = em.createNamedQuery(AbstractKunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN, AbstractKunde.class)
					          .setParameter(AbstractKunde.PARAM_KUNDE_ID, id)
                              .getSingleResult();
				}
				catch (NoResultException e) {
					kunde = null;
				}
				// FIXME https://hibernate.atlassian.net/browse/HHH-8285 : @NamedEntityGraph ab Java EE 7 bzw. JPA 2.1
				//kunde = em.find(AbstractKunde.class, id, GRAPH_BESTELLUNGEN);
				break;

			default:
				kunde = em.find(AbstractKunde.class, id);
				break;
		}
		return kunde;
	}
	
	public <T extends AbstractKunde> T createKunde(T kunde) {
		if (kunde == null) {
			return kunde;
		}
	
		// Pruefung, ob ein solcher Kunde schon existiert
		final AbstractKunde tmp = findKundeByEmail(kunde.getEmail());
		if (tmp != null) {
			throw new EmailExistsException(kunde.getEmail());
		}
		
		// Password verschluesseln
		passwordVerschluesseln(kunde);
		
		// Rolle setzen
		kunde.addRollen(Sets.newHashSet(RolleType.KUNDE));
	
		em.persist(kunde);
		event.fire(kunde);
		
		return kunde;
	}
	
	public <T extends AbstractKunde> T updateKunde(T kunde, boolean geaendertPassword) {
		if (kunde == null) {
			return null;
		}
		
		// kunde vom EntityManager trennen, weil anschliessend z.B. nach Id und Email gesucht wird
		em.detach(kunde);
		
		// Wurde das Objekt konkurrierend geloescht?
		AbstractKunde tmp = findKundeById(kunde.getId(), FetchType.NUR_KUNDE);
		if (tmp == null) {
			throw new ConcurrentDeletedException(kunde.getId());
		}
		em.detach(tmp);
		
		// Gibt es ein anderes Objekt mit gleicher Email-Adresse?
		tmp = findKundeByEmail(kunde.getEmail());
		if (tmp != null) {
			em.detach(tmp);
			if (tmp.getId().longValue() != kunde.getId().longValue()) {
				// anderes Objekt mit gleichem Attributwert fuer email
				throw new EmailExistsException(kunde.getEmail());
			}
		}
		
		// Password verschluesseln
		if (geaendertPassword) {
			passwordVerschluesseln(kunde);
		}

		kunde = em.merge(kunde);   // OptimisticLockException
		kunde.setPasswordWdh(kunde.getPassword());
		
		return kunde;
	}
	
	public AbstractKunde findKundeByEmail(String email) {
		AbstractKunde kunde;
		try {
			kunde = em.createNamedQuery(AbstractKunde.FIND_KUNDE_BY_EMAIL, AbstractKunde.class)
					  .setParameter(AbstractKunde.PARAM_KUNDE_EMAIL, email)
					  .getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		
		return kunde;
	}
	
	/**
	 * Einen Kunden in der DB loeschen.
	 * @param kunde Der zu loeschende Kunde
	 */
	public void deleteKunde(AbstractKunde kunde) {
		if (kunde == null) {
			return;
		}

		deleteKundeById(kunde.getId());
	}

	/**
	 * Einen Kunden zu gegebener ID loeschen
	 * @param kundeId Die ID des zu loeschenden Kunden
	 */
	public void deleteKundeById(Long kundeId) {
		final AbstractKunde kunde = findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN);
		if (kunde == null) {
			// Der Kunde existiert nicht oder ist bereits geloescht
			return;
		}

		final boolean hasBestellungen = hasBestellungen(kunde);
		if (hasBestellungen) {
			throw new KundeDeleteBestellungException(kunde);
		}

		// Kundendaten loeschen
		em.remove(kunde);
	}
	
	public List<AbstractKunde> findKundenByPLZ(String plz) {
		return em.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_PLZ, AbstractKunde.class)
				 .setParameter(AbstractKunde.PARAM_KUNDE_ADRESSE_PLZ, plz)
				 .getResultList();
	}
	
	public List<AbstractKunde> findKundenBySeit(Date seit) {
		return em.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_DATE, AbstractKunde.class)
				 .setParameter(AbstractKunde.PARAM_KUNDE_SEIT, seit)
				 .getResultList();
	}
	
	public List<AbstractKunde> findPrivatkundenFirmenkunden() {
		return em.createNamedQuery(AbstractKunde.FIND_PRIVATKUNDEN_FIRMENKUNDEN, AbstractKunde.class)
				 .getResultList();
	}
	
	public List<AbstractKunde> findKundenByNachnameCriteria(String nachname) {
		// SELECT k
		// FROM   AbstractKunde k
		// WHERE  k.nachname = ?
				
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractKunde> criteriaQuery = builder.createQuery(AbstractKunde.class);
		final Root<AbstractKunde> k = criteriaQuery.from(AbstractKunde.class);

		final Path<String> nachnamePath = k.get(AbstractKunde_.nachname);
		
		final Predicate pred = builder.equal(nachnamePath, nachname);
		criteriaQuery.where(pred);

		final List<AbstractKunde> kunden = em.createQuery(criteriaQuery).getResultList();
		return kunden;
	}
	
	public List<AbstractKunde> findKundenMitMinBestMenge(short minMenge) {
		// SELECT DISTINCT k
		// FROM   AbstractKunde k
		//        JOIN k.bestellungen b
		//        JOIN b.bestellpositionen bp
		// WHERE  bp.anzahl >= ?
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractKunde> criteriaQuery  = builder.createQuery(AbstractKunde.class);
		final Root<AbstractKunde> k = criteriaQuery.from(AbstractKunde.class);

		final Join<AbstractKunde, Bestellung> b = k.join(AbstractKunde_.bestellungen);
		final Join<Bestellung, Bestellposition> bp = b.join(Bestellung_.bestellpositionen);
		criteriaQuery.where(builder.gt(bp.<Long>get(Bestellposition_.anzahl), minMenge))
		             .distinct(true);
		
		return em.createQuery(criteriaQuery).getResultList();
	}
	
	private static boolean hasBestellungen(AbstractKunde kunde) {
		LOGGER.debugf("hasBestellungen BEGINN: %s", kunde);
		
		boolean result = false;
		
		// Gibt es den Kunden und hat er mehr als eine Bestellung?
		// Bestellungen nachladen wegen Hibernate-Caching
		if (kunde != null && kunde.getBestellungen() != null && !kunde.getBestellungen().isEmpty()) {
			result = true;
		}
		
		LOGGER.debugf("hasBestellungen ENDE: %s", result);
		return result;
	}

	
	private void passwordVerschluesseln(AbstractKunde kunde) {
		LOGGER.debugf("passwordVerschluesseln BEGINN: %s", kunde);

		final String unverschluesselt = kunde.getPassword();
		final String verschluesselt = authService.verschluesseln(unverschluesselt);
		kunde.setPassword(verschluesselt);
		kunde.setPasswordWdh(verschluesselt);

		LOGGER.debugf("passwordVerschluesseln ENDE: %s", verschluesselt);
	}
	
	/**
	 * Einem Kunden eine hochgeladene Datei ohne MIME Type (bei RESTful WS) zuordnen
	 * @param kundeId Die ID des Kunden
	 * @param bytes Das Byte-Array der hochgeladenen Datei
	 */
	public AbstractKunde setFile(Long kundeId, byte[] bytes) {
		final AbstractKunde kunde = findKundeById(kundeId, FetchType.NUR_KUNDE);
		if (kunde == null) {
			return null;
		}
		final MimeType mimeType = fileHelper.getMimeType(bytes);
		setFile(kunde, bytes, mimeType);
		return kunde;
	}
	
	/**
	 * Einem Kunden eine hochgeladene Datei zuordnen
	 * @param kunde Der betroffene Kunde
	 * @param bytes Das Byte-Array der hochgeladenen Datei
	 * @param mimeTypeStr Der MIME-Type als String
	 */
	public AbstractKunde setFile(AbstractKunde kunde, byte[] bytes, String mimeTypeStr) {
		final MimeType mimeType = MimeType.build(mimeTypeStr);
		setFile(kunde, bytes, mimeType);
		return kunde;
	}
	
	private void setFile(AbstractKunde kunde, byte[] bytes, MimeType mimeType) {
		if (mimeType == null) {
			throw new NoMimeTypeException();
		}
		
		final String filename = fileHelper.getFilename(kunde.getClass(), kunde.getId(), mimeType);
		
		// Gibt es noch kein (Multimedia-) File
		File file = kunde.getFile();
		if (kunde.getFile() == null) {
			file = new File(bytes, filename, mimeType);
			LOGGER.tracef("Neue Datei %s", file);
			kunde.setFile(file);
			em.persist(file);
		}
		else {
			file.set(bytes, filename, mimeType);
			LOGGER.tracef("Ueberschreiben der Datei %s", file);
			em.merge(file);
		}

		// Hochgeladenes Bild/Video/Audio in einem parallelen Thread als Datei fuer die Web-Anwendung abspeichern
		final File newFile = kunde.getFile();
		final Runnable storeFile = new Runnable() {
			@Override
			public void run() {
				fileHelper.store(newFile);
			}
		};
		managedExecutorService.execute(storeFile);
	}
	
}
