package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen Zimmermann</a>
 */
@Entity
@Table(indexes = @Index(columnList = "bezeichnung"))
@NamedQueries({
		@NamedQuery(name = Artikel.FIND_VERFUEGBARE_ARTIKEL, query = "SELECT      a" + " FROM     Artikel a"
				+ " WHERE    a.ausgesondert = FALSE" + " ORDER BY a.bezeichnung ASC"),
		@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_BEZ, query = "SELECT      a" + " FROM     Artikel a"
				+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_ARTIKEL_BEZEICHNUNG
				+ "          AND a.ausgesondert = FALSE" + " ORDER BY a.id ASC"),
		@NamedQuery(name = Artikel.FIND_LADENHUETER, query = "SELECT    a" + " FROM   Artikel a"
				+ " WHERE  a NOT IN (SELECT bp.artikel FROM Bestellposition bp)") })
@Cacheable
@XmlRootElement
public class Artikel implements Serializable {
	private static final long serialVersionUID = -3700579190995722151L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	public static final String PARAM_ARTIKEL_BEZEICHNUNG = "bezeichnung";

	public static final int BEZEICHNUNG_LENGTH_MIN = 2;
	public static final int BEZEICHNUNG_LENGTH_MAX = 32;
	public static final String BEZEICHNUNG_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF0-9]+"
			+ "(-[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF0-9]+)?";

	private static final String PREFIX = "Artikel.";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findausgesonderteArtikel";
	public static final String FIND_ARTIKEL_BY_BEZ = PREFIX + "findArtikelByBez";
	public static final String FIND_LADENHUETER = PREFIX + "findLadenhueter";

	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private Long id = KEINE_ID;

	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikel.bezeichnung.notNull}")
	@Size(min = BEZEICHNUNG_LENGTH_MIN, max = BEZEICHNUNG_LENGTH_MAX, message = "{artikel.bezeichnung.length}")
	@Pattern(regexp = BEZEICHNUNG_PATTERN, message = "{artikel.bezeichnung.pattern}")
	private String bezeichnung = "";

	@Column(nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.preis.notNull}")
	@DecimalMin(value = "0.00", message = "{artikelverwaltung.artikel.preis.min}")
	private BigDecimal preis;

	private boolean ausgesondert = false;

	@Basic(optional = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;

	@Basic(optional = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	public Artikel() {
		super();
	}

	public Artikel(String bezeichnung, BigDecimal preis) {
		super();
		this.bezeichnung = bezeichnung;
		this.preis = preis;
	}

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}

	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neuer Artikel mit ID=%d", id);
	}

	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Artikel mit ID=%s aktualisiert: version=%d", id, version);
	}

	public void setValues(Artikel a) {
		this.version = a.version;
		this.bezeichnung = a.bezeichnung;
		this.preis = a.preis;
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

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public BigDecimal getPreis() {
		return preis;
	}

	public void setPreis(BigDecimal preis) {
		this.preis = preis;
	}

	public boolean isAusgesondert() {
		return ausgesondert;
	}

	public void setAusgesondert(boolean ausgesondert) {
		this.ausgesondert = ausgesondert;
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aktualisiert == null) ? 0 : aktualisiert.hashCode());
		result = prime * result + ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
		result = prime * result + (ausgesondert ? 1231 : 1237);
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Artikel other = (Artikel) obj;
		if (aktualisiert == null) {
			if (other.aktualisiert != null)
				return false;
		}
		else if (!aktualisiert.equals(other.aktualisiert))
			return false;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		}
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (erzeugt == null) {
			if (other.erzeugt != null)
				return false;
		}
		else if (!erzeugt.equals(other.erzeugt))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (preis == null) {
			if (other.preis != null)
				return false;
		}
		else if (!preis.equals(other.preis))
			return false;
		if (ausgesondert != other.ausgesondert)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", version=" + version + ", bezeichnung=" + bezeichnung + ", preis=" + preis
				+ ", ausgesondert=" + ausgesondert + ", erzeugt=" + erzeugt + ", aktualisiert=" + aktualisiert
			 + "]";
	}
	
}
