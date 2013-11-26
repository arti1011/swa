package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.ERSTE_VERSION;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;







import javax.persistence.Basic;
import javax.persistence.Index;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;



@Entity
@Table(indexes = { @Index(columnList = "bestellung_fk"), @Index(columnList = "artikel_fk") })
@NamedQuery(name  = Bestellposten.FIND_LADENHUETER,
			query = "SELECT a"
   	            	 + " FROM   Artikel a"
   	            	 + " WHERE  a NOT IN (SELECT bp.artikel FROM Bestellposten bp)")
public class Bestellposten implements Serializable {
	
	
	private static final long serialVersionUID = -1427065293027144676L;
	private static final int ANZAHL_MIN = 1;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String PREFIX = "Bestellposten.";
	public static final String FIND_LADENHUETER = PREFIX + "findLadenhueter";
	public static final String FIND_POSTEN_BY_BESTELLID = PREFIX
			+ "findPostenByBestellid";
	public static final String PARAM_BES_ID = "id";
	
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(name = "anzahl", nullable = false)
	@Min(value = ANZAHL_MIN, message = "{bestellverwaltung.bestellposten.anzahl.min}")
	private short anzahl;
	
	
	@Transient
	private URI artikelUri;
	
	@ManyToOne
	@JoinColumn(name = "artikel_fk", nullable = false)
	@XmlTransient
	private Artikel artikel;
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Bestellposten mit ID=%d", id);
	}
	
	public Bestellposten() {
		super();
	}
	
	public Bestellposten(Artikel artikel) {
		super();
		this.artikel = artikel;
		this.anzahl = 1;
	}
	
	public Bestellposten(Artikel artikel, short anzahl) {
		super();
		this.artikel = artikel;
		this.anzahl = anzahl;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Artikel getArtikel() {
		return artikel;
	}
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public URI getArtikelUri() {
		return artikelUri;
	}
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}	
	public short getAnzahl() {
		return anzahl;
	}
	public void setAnzahl(short anzahl) {
		this.anzahl = anzahl;
	}
	
	@Override
	public String toString() {
		final Long artikelId = artikel == null ? null : artikel.getId();
		return "Bestellposten [id=" + id + ", artikel=" + artikelId
			   + ", artikelUri=" + artikelUri + ", anzahl=" + anzahl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
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
		final Bestellposten other = (Bestellposten) obj;
		
		// Bei persistenten Bestellpositionen koennen zu verschiedenen Bestellungen gehoeren
		// und deshalb den gleichen Artikel (s.u.) referenzieren, deshalb wird Id hier beruecksichtigt
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}

		// Wenn eine neue Bestellung angelegt wird, dann wird jeder zu bestellende Artikel
		// genau 1x referenziert (nicht zu verwechseln mit der "anzahl")
		if (artikel == null) {
			if (other.artikel != null) {
				return false;
			}
		}
		else if (!artikel.equals(other.artikel)) {
			return false;
		}
		
		return true;
	}

	/*
	 * Preis einer Position berechnen, 
	 * Hilfsmethode für den Gesamtpreis einer
	 * Bestellung
	 */
	public BigDecimal calcPreis() {
		return artikel.getPreis().multiply(new BigDecimal(this.getAnzahl()));
	}
}
