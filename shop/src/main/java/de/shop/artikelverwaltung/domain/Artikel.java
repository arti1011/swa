package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import javax.persistence.Index;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;



//import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;


@Entity
@Table(indexes = @Index(columnList = "bezeichnung"))
@NamedQueries({
	@NamedQuery(name  = Artikel.FIND_ARTIKEL,
            query = "SELECT a"
			        + " FROM   Artikel a"),
	@NamedQuery(name  = Artikel.FIND_VERFUEGBARE_ARTIKEL,
            	query = "SELECT      a"
            	        + " FROM     Artikel a"
						+ " WHERE    a.verfuegbar = TRUE"
                        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_BY_BEZ,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_BEZEICHNUNG
						+ "          AND a.verfuegbar = TRUE"
			 	        + " ORDER BY a.id ASC"),
   	@NamedQuery(name  = Artikel.FIND_ARTIKEL_MAX_PREIS,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.preis < :" + Artikel.PARAM_PREIS
			 	        + " ORDER BY a.id ASC")
})

@Cacheable
@XmlRootElement
public class Artikel implements Serializable {

private static final long serialVersionUID = 1472129607838538329L;
private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());


private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";

private static final String PREFIX = "Artikel.";
public static final String FIND_ARTIKEL = PREFIX + "findArtikel";
public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
public static final String FIND_ARTIKEL_BY_BEZ = PREFIX + "findArtikelByBez";
public static final String FIND_ARTIKEL_BY_KAT = PREFIX + "findArtikelByKat";
public static final String FIND_ARTIKEL_BY_KAT_AND_FAR = PREFIX + "findArtikelByKatAndFar";
public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX + "findArtikelByMaxPreis";
public static final String PARAM_BEZEICHNUNG = "bezeichnung";
public static final String PARAM_FARBE = "farbe";
public static final String PARAM_PREIS = "preis";

public static final String PARAM_ARTIKEL_BEZEICHNUNG = "bezeichnung";

private static final int BEZEICHNUNG_LENGTH_MIN = 3;
private static final int BEZEICHNUNG_LENGTH_MAX = 32;
private static final String BEZEICHNUNG_PATTERN = NAME_PATTERN;

@Id
@GeneratedValue
@Column(nullable = false, updatable = false)
@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}")
private Long id = KEINE_ID;

@Version
@Basic(optional = false)
private int version = ERSTE_VERSION;

@Column(length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
@Size(min = BEZEICHNUNG_LENGTH_MIN, max = BEZEICHNUNG_LENGTH_MAX,
message = "{artikelverwaltung.artikel.bezeichnung.length}")
@Pattern(regexp = BEZEICHNUNG_PATTERN, message = "{artikelverwaltung.artikel.bezeichnung.pattern}")
private String bezeichnung = "";

@NotNull
@Column(nullable = false, precision = 7, scale = 2)
private BigDecimal preis;

private boolean verfuegbar = true;

@Basic(optional = false)
@Column(nullable = false)
@Temporal(TIMESTAMP)
@XmlTransient
private Date erstellt;

@Basic(optional = false)
@Column(nullable = false)
@Temporal(TIMESTAMP)
@XmlTransient
private Date aktualisiert;




public Artikel() {
	super();
}

public void setValues(Artikel a) {
	this.bezeichnung = a.getBezeichnung();
	this.preis = a.getPreis();
	this.version = a.getVersion();
}

public Artikel(String bezeichnung, String farbe, BigDecimal preis) {
	super();
	this.bezeichnung = bezeichnung;
	this.preis = preis;
}

@PrePersist
private void prePersist() {
	erstellt = new Date();
	aktualisiert = new Date();
}

@PostPersist
private void postPersist() {
	LOGGER.debugf("Neuer Artikel mit ID=%d", id);
}

@PreUpdate
private void preUpdate() {
	aktualisiert = new Date();
}

@PostUpdate
private void postUpdate() {
	LOGGER.debugf("Artikel mit ID=%s aktualisiert: version=%d", id, version);
}

public Long getId() {
return id;
}
public void setId(Long id) {
this.id = id;
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
public boolean isVerfuegbar() {
return verfuegbar;
}
public void setVerfuegbar(boolean verfuegbar) {
this.verfuegbar = verfuegbar;
}
public Date getErstellt() {
return erstellt == null ? null : (Date) erstellt.clone();
}
public void setErstellt(Date erstellt) {
this.erstellt = erstellt == null ? null : (Date) erstellt.clone();
}
public Date getAktualisiert() {
return aktualisiert == null ? null : (Date) aktualisiert.clone();
}
public void setAktualisiert(Date aktualisiert) {
this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
}
public int getVersion() {
	return version;
}
public void setVersion(int version) {
	this.version = version;
}



@Override
public String toString() {
return "Artikel [id=" + id + ", bezeichnung=" + bezeichnung
  + ", preis="
+ preis + ", verfügbar=" + verfuegbar + ", erstellt=" + erstellt
+ ", aktualisiert=" + aktualisiert + "]";
}



@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
			+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + ((preis == null) ? 0 : preis.hashCode());
	result = prime * result + (verfuegbar ? 1231 : 1237);
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
	if (bezeichnung == null) {
		if (other.bezeichnung != null)
			return false;
	}
	else if (!bezeichnung.equals(other.bezeichnung))
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
	if (verfuegbar != other.verfuegbar)
		return false;
	return true;
}

@Override
public Object clone() throws CloneNotSupportedException {
	final Artikel neuesObjekt = Artikel.class.cast(super.clone());
	neuesObjekt.id = id;
	neuesObjekt.version = version;
	neuesObjekt.bezeichnung = bezeichnung;
	neuesObjekt.preis = preis;
	neuesObjekt.erstellt = erstellt;
	neuesObjekt.aktualisiert = aktualisiert;
	return neuesObjekt;
}

}
