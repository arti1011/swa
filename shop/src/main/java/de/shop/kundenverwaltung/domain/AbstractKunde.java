
package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.util.Constants.ERSTE_VERSION;




import java.io.Serializable;
import java.lang.invoke.MethodHandles;


import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;



import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import de.shop.auth.domain.RolleType;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.persistence.File;



@Entity
@Cacheable
@Table(name = "kunde", indexes = { @Index(columnList = "nachname"), @Index(columnList = "file_fk") })
@Inheritance
@DiscriminatorColumn(name = "art", length = 1)
@NamedQueries({
	@NamedQuery(name  = AbstractKunde.FIND_KUNDEN,
                query = "SELECT k"
				        + " FROM   AbstractKunde k"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
				query = "SELECT  DISTINCT k"
						+ " FROM AbstractKunde k LEFT JOIN FETCH k.bestellungen"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDEN_ORDER_BY_ID,
		        query = "SELECT   k"
				        + " FROM  AbstractKunde k"
		                + " ORDER BY k.id"),
	@NamedQuery(name  = AbstractKunde.FIND_IDS_BY_PREFIX,
		        query = "SELECT   k.id"
		                + " FROM  AbstractKunde k"
		                + " WHERE CONCAT('', k.id) LIKE :" + AbstractKunde.PARAM_KUNDE_ID_PREFIX
		                + " ORDER BY k.id"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDEN_BY_ID_PREFIX,
				query = "SELECT   k"
				        + " FROM  AbstractKunde k"
				        + " WHERE CONCAT('', k.id) LIKE :" + AbstractKunde.PARAM_KUNDE_ID_PREFIX
				        + " ORDER BY k.id"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
	            query = "SELECT k"
				        + " FROM   AbstractKunde k"
	            		+ " WHERE  UPPER(k.nachname) = UPPER(:" + AbstractKunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = AbstractKunde.FIND_NACHNAMEN_BY_PREFIX,
   	            query = "SELECT   DISTINCT k.nachname"
				        + " FROM  AbstractKunde k "
	            		+ " WHERE UPPER(k.nachname) LIKE UPPER(:"
	            		+ AbstractKunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
	            query = "SELECT DISTINCT k"
			            + " FROM   AbstractKunde k LEFT JOIN FETCH k.bestellungen"
			            + " WHERE  UPPER(k.nachname) = UPPER(:" + AbstractKunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
	            query = "SELECT DISTINCT k"
			            + " FROM   AbstractKunde k LEFT JOIN FETCH k.bestellungen"
			            + " WHERE  k.id = :" + AbstractKunde.PARAM_KUNDE_ID),
   	@NamedQuery(name  = AbstractKunde.FIND_KUNDE_BY_EMAIL,
   	            query = "SELECT DISTINCT k"
   			            + " FROM   AbstractKunde k"
   			            + " WHERE  k.email = :" + AbstractKunde.PARAM_KUNDE_EMAIL),
   	@NamedQuery(name = AbstractKunde.FIND_ADRESSE_BY_KUNDE,
   				query = "SELECT k.adresse"
   						+ " FROM AbstractKunde k"),
    @NamedQuery(name  = AbstractKunde.FIND_KUNDEN_BY_PLZ,
	            query = "SELECT k"
				        + " FROM  AbstractKunde k"
			            + " WHERE k.adresse.plz = :" + AbstractKunde.PARAM_KUNDE_ADRESSE_PLZ),
	@NamedQuery(name = AbstractKunde.FIND_KUNDEN_BY_DATE,
			    query = "SELECT k"
			            + " FROM  AbstractKunde k"
			    		+ " WHERE k.seit = :" + AbstractKunde.PARAM_KUNDE_SEIT),
	@NamedQuery(name = AbstractKunde.FIND_PRIVATKUNDEN_FIRMENKUNDEN,
			    query = "SELECT k"
			            + " FROM  AbstractKunde k"
			    		+ " WHERE TYPE(k) IN (Privatkunde, Firmenkunde)"),
	@NamedQuery(name  = AbstractKunde.FIND_ALL_NACHNAMEN,
		   	            query = "SELECT      DISTINCT k.nachname"
		   				        + " FROM     AbstractKunde k"
		   				        + " ORDER BY k.nachname"),
    @NamedQuery(name  = AbstractKunde.FIND_KUNDEN_OHNE_BESTELLUNGEN,
			            query = "SELECT k"
					            + " FROM   AbstractKunde k"
					            + " WHERE  k.bestellungen IS EMPTY"),
	@NamedQuery(name  = AbstractKunde.FIND_KUNDE_BY_USERNAME,
					            query = "SELECT   k"
								        + " FROM  AbstractKunde k"
					            		+ " WHERE CONCAT('', k.id) = :" + AbstractKunde.PARAM_KUNDE_USERNAME),
	@NamedQuery(name  = AbstractKunde.FIND_USERNAME_BY_USERNAME_PREFIX,
				  	            query = "SELECT   CONCAT('', k.id)"
				  				        + " FROM  AbstractKunde k"
				   	            		+ " WHERE CONCAT('', k.id) LIKE :" + AbstractKunde.PARAM_USERNAME_PREFIX)
					            
})

@NamedEntityGraphs({
	@NamedEntityGraph(name = AbstractKunde.GRAPH_BESTELLUNGEN,
					  attributeNodes = @NamedAttributeNode("bestellungen"))
})

@ScriptAssert(lang = "javascript",
script = "_this.password != null && !_this.password.equals(\"\")"
		   + "&& _this.password.equals(_this.passwordWdh)",
message = "{kunde.password.notEqual}",
groups = { Default.class, PasswordGroup.class })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
@Type(value = Privatkunde.class, name = AbstractKunde.PRIVATKUNDE),
@Type(value = Firmenkunde.class, name = AbstractKunde.FIRMENKUNDE) })
@XmlRootElement
@Formatted
@XmlSeeAlso({ Firmenkunde.class, Privatkunde.class })
public abstract class AbstractKunde implements Serializable, Cloneable {
	private static final long serialVersionUID = 7401524595142572933L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";
	
	//Pattern
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String NACHNAME_PREFIX = "(o'|von|von der|von und zu|van)?";
	
	private static final String VORNAME_PATTERN = NAME_PATTERN;
	private static final int VORNAME_LENGTH_MIN = 2;
	private static final int VORNAME_LENGTH_MAX = 32;
	public static final String NACHNAME_PATTERN = NACHNAME_PREFIX + NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	public static final int NACHNAME_LENGTH_MIN = 2;
	public static final int NACHNAME_LENGTH_MAX = 32;
	public static final int EMAIL_LENGTH_MAX = 128;
	public static final int DETAILS_LENGTH_MAX = 128 * 1024;
	public static final int PASSWORD_LENGTH_MAX = 256;

	
	private static final String PREFIX = "AbstractKunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundenFetchBestellungen";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderById";
	public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
	public static final String FIND_KUNDEN_BY_ID_PREFIX = PREFIX + "findKundenByIdPrefix";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN =
		                       PREFIX + "findKundenByNachnameFetchBestellungen";
	public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX + "findNachnamenByPrefix";

	public static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN =
		                       PREFIX + "findKundeByIdFetchBestellungen";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
	public static final String FIND_ADRESSE_BY_KUNDE = PREFIX + "findAdresseByKunde";
	public static final String FIND_KUNDEN_BY_DATE = PREFIX + "findKundenByDate";
	public static final String FIND_PRIVATKUNDEN_FIRMENKUNDEN = PREFIX + "findPrivatkundenFirmenkunden";
	public static final String FIND_ALL_NACHNAMEN = PREFIX + "findAllNachnamen";
	public static final String FIND_KUNDEN_OHNE_BESTELLUNGEN = PREFIX + "findKundenOhneBestellungen";
	public static final String FIND_KUNDE_BY_USERNAME = PREFIX + "findKundeByUsername";
	public static final String FIND_USERNAME_BY_USERNAME_PREFIX = PREFIX + "findKundeByUsernamePrefix";
	
	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
	public static final String PARAM_KUNDE_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_SEIT = "seit";
	public static final String PARAM_KUNDE_EMAIL = "email";
	public static final String PARAM_KUNDE_USERNAME = "username";
	public static final String PARAM_USERNAME_PREFIX = "usernamePrefix";
	
	public static final String GRAPH_BESTELLUNGEN = "bestellungen";


	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(length = VORNAME_LENGTH_MAX)
	@NotNull(message = "{kundenverwaltung.kunde.vorname.notNull}")
	@Size(min = VORNAME_LENGTH_MIN, max = VORNAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.vorname.length}")
	@Pattern(regexp = VORNAME_PATTERN, message = "{kundenverwaltung.kunde.vorname.pattern}")
	private String vorname;
	
	@Column(length = NACHNAME_LENGTH_MAX)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	private String nachname;
	
	@Basic(optional = false)
	@Temporal(DATE)
	@Past(message = "{kundenverwaltung.kunde.seit.past}")
	private Date seit;
	
	@Column(length = EMAIL_LENGTH_MAX, nullable = false, unique = true)
	@Email(message = "{kundenverwaltung.kunde.email}")
	@NotNull(message = "{kundenverwaltung.kunde.email.notNull}")
	@Size(max = EMAIL_LENGTH_MAX, message = "{kundenverwaltung.kunde.email.length}")
	private String email;

	@Column(length = PASSWORD_LENGTH_MAX)
	@Size(max = PASSWORD_LENGTH_MAX, message = "{kundenverwaltung.kunde.password.length}")
	private String password;
	
	@Transient
	private String passwordWdh;
	
	
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;

	// Default: fetch=LAZY
	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@XmlTransient
	private List<Bestellung> bestellungen;
	
	//Transient wird nicht in der Datenbank abgespeichert
	@Transient
	private URI bestellungenUri;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
   	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle" }))
	@Column(table = "kunde_rolle", name = "rolle", length = 32, nullable = false)
	private Set<RolleType> rollen;
	
	@OneToOne(fetch = LAZY, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "file_fk")
	@XmlTransient
	private File file;
	
	@PrePersist
	protected void prePersist() {
		aktualisiert = new Date();
	}
	
	public AbstractKunde() {
		super();
	}
	
	public AbstractKunde(String nachname, String vorname, String email, Date seit) {
		super();
		this.nachname = nachname;
		this.vorname = vorname;
		this.email = email;
		this.seit = seit == null ? null : (Date) seit.clone();
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Kunde mit ID=%d", id);
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostUpdate
	protected void postUpdate() {
		LOGGER.debugf("Kunde mit ID=%d aktualisiert: version=%d", id, version);
	}
	
	public void setValues(AbstractKunde k) {
		nachname = k.nachname;
		version = k.version;
		vorname = k.vorname;
		seit = k.seit;
		email = k.email;
		password = k.password;
		passwordWdh = k.password;
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}	
	public Date getSeit() {
		return seit == null ? null : (Date) seit.clone();
	}
	public void setSeit(Date seit) {
		this.seit = seit == null ? null : (Date) seit.clone();
	}

	// Parameter, z.B. DateFormat.MEDIUM, Locale.GERMANY
	// MEDIUM fuer Format dd.MM.yyyy
	public String getSeitAsString(int style, Locale locale) {
		Date temp = seit;
		if (temp == null) {
			temp = new Date();
		}
		final DateFormat f = DateFormat.getDateInstance(style, locale);
		return f.format(temp);
	}
	
	// Parameter, z.B. DateFormat.MEDIUM, Locale.GERMANY
	// MEDIUM fuer Format dd.MM.yyyy
	public void setSeit(String seitStr, int style, Locale locale) {
		final DateFormat f = DateFormat.getDateInstance(style, locale);
		try {
			this.seit = f.parse(seitStr);
		}
		catch (ParseException e) {
			throw new RuntimeException("Kein gueltiges Datumsformat fuer: " + seitStr, e);
		}
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordWdh() {
		return passwordWdh;
	}

	public void setPasswordWdh(String passwordWdh) {
		this.passwordWdh = passwordWdh;
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	public Adresse getAdresse() {
		return adresse;
	}
	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
	public List<Bestellung> getBestellungen() {
		if (bestellungen == null) {
			return null;
		}		
		return Collections.unmodifiableList(bestellungen);
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}
	
	public AbstractKunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<>();
		}
		bestellungen.add(bestellung);
		return this;
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}
	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}
		
	public Set<RolleType> getRollen() {
		if (rollen == null) {
			return null;
		}
		
		return Collections.unmodifiableSet(rollen);
	}

	public void setRollen(Set<RolleType> rollen) {
		if (this.rollen == null) {
			this.rollen = rollen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.rollen.clear();
		if (rollen != null) {
			this.rollen.addAll(rollen);
		}
	}
	
	public AbstractKunde addRollen(Collection<RolleType> rollen) {
		LOGGER.tracef("neue Rollen: %s", rollen);
		if (this.rollen == null) {
			this.rollen = new HashSet<>();
		}
		this.rollen.addAll(rollen);
		LOGGER.tracef("Rollen nachher: %s", this.rollen);
		return this;
	}
	
	public AbstractKunde removeRollen(Collection<RolleType> rollen) {
		LOGGER.tracef("zu entfernende Rollen: %s", rollen);
		if (this.rollen == null) {
			return this;
		}
		this.rollen.removeAll(rollen);
		LOGGER.tracef("Rollen nachher: %s", this.rollen);
		return this;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aktualisiert == null) ? 0 : aktualisiert.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nachname == null) ? 0 : nachname.hashCode());	
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractKunde other = (AbstractKunde) obj;
		
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		}
		else if (!email.equals(other.email)) {
			return false;
		}
		
		return true;
	}
	
	
	
	@Override
	public String toString() {
		return "AbstractKunde [id=" + id
			   + ", nachname=" + nachname + ", vorname=" + vorname
			   + ", seit=" + getSeitAsString(DateFormat.MEDIUM, Locale.GERMANY)
			   + ", rollen=" + rollen + ", password=" + password + ", passwordWdh=" + passwordWdh
			   + ", password=" + password + ", passwordWdh=" + passwordWdh
			   + ", aktualisiert=" + aktualisiert 
			   + "]";
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		final AbstractKunde neuesObjekt = AbstractKunde.class.cast(super.clone());
		neuesObjekt.id = id;
		neuesObjekt.version = version;
		neuesObjekt.nachname = nachname;
		neuesObjekt.vorname = vorname;
		neuesObjekt.email = email;
		neuesObjekt.password = password;
		neuesObjekt.passwordWdh = passwordWdh;
		neuesObjekt.adresse = adresse;
		neuesObjekt.aktualisiert = aktualisiert;
		return neuesObjekt;
	}

}
