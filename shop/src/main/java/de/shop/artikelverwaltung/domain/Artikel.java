package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

import de.shop.util.IdGroup;



@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name  = Artikel.FIND_VERFUEGBARE_ARTIKEL,
            	query = "SELECT      a"
            	        + " FROM     Artikel a"
						+ " WHERE    a.ausgesondert = FALSE"
                        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_BY_BEZ,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_BEZEICHNUNG
						+ "          AND a.ausgesondert = FALSE"
			 	        + " ORDER BY a.id ASC"),
   	@NamedQuery(name  = Artikel.FIND_ARTIKEL_MAX_PREIS,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.preis < :" + Artikel.PARAM_PREIS
			 	        + " ORDER BY a.id ASC")
})
public class Artikel implements Serializable  {
	
	private static final long serialVersionUID = 4377328456462469678L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String PREFIX = "Artikel.";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_BEZ = PREFIX + "findArtikelByBez";
	public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX + "findArtikelByMaxPreis";

	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	public static final String PARAM_PREIS = "preis";
	
	public static final int ARTIKELBEZEICHNUNG_LENGTH_MIN = 2;
	public static final int ARTIKELBEZEICHNUNG_LENGTH_MAX = 32;
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Column(length = ARTIKELBEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.artikelbezeichnung.notNull}")
	@Size(min = ARTIKELBEZEICHNUNG_LENGTH_MIN, max = ARTIKELBEZEICHNUNG_LENGTH_MAX,
	      message = "{artikelverwaltung.artikel.artikelbezeichnung.length}")
	private String artikelBezeichnung = "";
	
	@NotNull(message = "{artikelverwaltung.artikel.preis.notNull}")
	@DecimalMin(value = "0.0", message = "{artikelverwaltung.artikel.preis.min}")
	private BigDecimal preis;
	
	@Column(nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.farbe.notNull}")
	private Set<ArtikelFarbeType> farbe;
	
	@Column(nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.verfuegbarkeit.notNull}")
	private String verfuegbarkeit;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getArtikelBezeichnung() {
		return artikelBezeichnung;
	}
	public void setArtikelBezeichnung(String artikelBezeichnung) {
		this.artikelBezeichnung = artikelBezeichnung;
	}
	public BigDecimal getPreis() {
		return preis;
	}
	public void setPreis(BigDecimal preis) {
		this.preis = preis;
	}
	public Set<ArtikelFarbeType> getFarbe() {
		return farbe;
	}
	public void setFarbe(Set<ArtikelFarbeType> farbe) {
		this.farbe = farbe;
	}
	public String getVerfuegbarkeit() {
		return verfuegbarkeit;
	}
	public void setVerfuegbarkeit(String verfuegbarkeit) {
		this.verfuegbarkeit = verfuegbarkeit;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
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
		result = prime
				* result
				+ ((artikelBezeichnung == null) ? 0 : artikelBezeichnung
						.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
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
		if (artikelBezeichnung == null) {
			if (other.artikelBezeichnung != null)
				return false;
		} else if (!artikelBezeichnung.equals(other.artikelBezeichnung))
			return false;
		if (preis == null) {
			if (other.preis != null)
				return false;
		} else if (!preis.equals(other.preis))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Artikel [id=" + id + ", artikelBezeichnung="
				+ artikelBezeichnung + ", preis=" + preis + ", farbe=" + farbe
				+ ", verfuegbarkeit=" + verfuegbarkeit + ", erzeugt=" + erzeugt
				+ ", aktualisiert=" + aktualisiert + "]";
	}
	
}
