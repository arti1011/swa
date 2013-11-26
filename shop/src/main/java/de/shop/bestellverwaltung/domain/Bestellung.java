package de.shop.bestellverwaltung.domain;



import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.validation.Valid;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.ERSTE_VERSION;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;





import de.shop.kundenverwaltung.domain.AbstractKunde;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;


import javax.persistence.Transient;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.logging.Logger;



@Entity
@Table(indexes = { @Index(columnList = "kunde_fk"), @Index(columnList = "erzeugt") })
@Inheritance
@NamedQueries({
	@NamedQuery(name  = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                query = "SELECT b"
			            + " FROM   Bestellung b"
						+ " WHERE  b.kunde = :" + Bestellung.PARAM_KUNDE),
   /*	@NamedQuery(name  = Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN,
			    query = "SELECT DISTINCT b"
                        + " FROM   Bestellung b LEFT JOIN FETCH b.lieferungen"
   			            + " WHERE  b.id = :" + Bestellung.PARAM_ID),
   			 */
	@NamedQuery(name  = Bestellung.FIND_KUNDE_BY_ID,
 			    query = "SELECT b.kunde"
                        + " FROM   Bestellung b"
  			            + " WHERE  b.id = :" + Bestellung.PARAM_ID)
})

@Cacheable
@XmlRootElement
public class Bestellung implements Serializable {


	private static final long serialVersionUID = -1900888975491172450L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX + "findBestellungenByKunde";
	public static final String FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN =
		                       PREFIX + "findBestellungenByIdFetchLieferungen";
	public static final String FIND_KUNDE_BY_ID = PREFIX + "findBestellungKundeById";
	
	public static final String PARAM_KUNDE = "kunde";
	public static final String PARAM_ID = "id";
	



		@Id
		@GeneratedValue
		@Column(nullable = false, updatable = false)
		private Long id = KEINE_ID;
		
		@Version
		@Basic(optional = false)
		private int version = ERSTE_VERSION;

		@ManyToOne(optional = false)
		@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
		@XmlTransient
		private AbstractKunde kunde;
		
		@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
		@JoinColumn(name = "bestellung_fk", nullable = false)
		@OrderColumn(name = "idx", nullable = false)
		@NotNull(message = "{bestellverwaltung.bestellung.bestellposten.notNull}")
		@Valid
		private List<Bestellposten> bestellposten;
		
		
		@Column(nullable = false)
		@Temporal(TIMESTAMP)
		@XmlTransient
		private Date erzeugt;
		
		@Column(nullable = false)
		@Temporal(TIMESTAMP)
		@XmlTransient
		private Date aktualisiert;
		
		@Transient
		private URI kundeUri;
		
		public Bestellung() {
			super();
		}
		
		public Bestellung(List<Bestellposten> bestellpositionen) {
			super();
			this.bestellposten = bestellpositionen;
		}

		@PrePersist
		private void prePersist() {
			erzeugt = new Date();
			aktualisiert = new Date();
		}
		
		@PostPersist
		private void postPersist() {
			LOGGER.debugf("Neue Bestellung mit ID=%d", id);
		}
		
		@PreUpdate
		private void preUpdate() {
			aktualisiert = new Date();
		}
		@PostUpdate
		private void postUpdate() {
			LOGGER.debugf("Bestellung mit ID=%d aktualisiert: version=%d", id, version);
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}

		public List<Bestellposten> getBestellposten() {
			if (bestellposten == null) {
				return null;
			}
			
			return Collections.unmodifiableList(bestellposten);
		}
		
		public void setBestellpositionen(List<Bestellposten> bestellpositionen) {
			if (this.bestellposten == null) {
				this.bestellposten = bestellpositionen;
				return;
			}
			
			// Wiederverwendung der vorhandenen Collection
			this.bestellposten.clear();
			if (bestellpositionen != null) {
				this.bestellposten.addAll(bestellpositionen);
			}
		}
		
		public Bestellung addBestellposition(Bestellposten bestellposition) {
			if (bestellposten == null) {
				bestellposten = new ArrayList<>();
			}
			bestellposten.add(bestellposition);
			return this;
		}
		
		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}
		
		public BigDecimal getGesamtpreis() {
			return calcPreis();
		}
		
		/*
		 * Hilfsmethode um den Gesamtpreis
		 * aus den einzelnen Positionen zu ermitteln
		 */
		
		public BigDecimal calcPreis() {
			BigDecimal ergebnis = new BigDecimal("0.0");
			for (Bestellposten bp : bestellposten) {
				ergebnis = ergebnis.add(bp.calcPreis());
			}
			return ergebnis;
		}
		
		public AbstractKunde getKunde() {
			return kunde;
		}
		public void setKunde(AbstractKunde kunde) {
			this.kunde = kunde;
		}
		public URI getKundeUri() {
			return kundeUri;
		}
		public void setKundeUri(URI kundeUri) {
			this.kundeUri = kundeUri;
		}

		@JsonProperty("datum")
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
					+ ((id == null) ? 0 : id.hashCode());

			result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
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
			final Bestellung other = (Bestellung) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			}
			else if (!id.equals(other.id))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "Bestellung [id=" + id
					+ ", erzeugt=" + erzeugt 
					+ "]";


		}
		
}
