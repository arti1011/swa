package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

@Entity
@Table(name = "bestellung")
@NamedQueries({
	@NamedQuery(name  = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                query = "SELECT b"
			            + " FROM   Bestellung b"
						+ " WHERE  b.kunde = :" + Bestellung.PARAM_KUNDE),
   	@NamedQuery(name  = Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN,
			    query = "SELECT DISTINCT b"
                        + " FROM   Bestellung b LEFT JOIN FETCH b.lieferungen"
   			            + " WHERE  b.id = :" + Bestellung.PARAM_ID),
	@NamedQuery(name  = Bestellung.FIND_KUNDE_BY_ID,
 			    query = "SELECT b.kunde"
                        + " FROM   Bestellung b"
  			            + " WHERE  b.id = :" + Bestellung.PARAM_ID)
})
public class Bestellung implements Serializable {
	
	private static final long serialVersionUID = 1618359234119003714L;
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
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long id;
	
	
	private boolean ausgeliefert;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
	@NotNull(message = "{bestellverwaltung.bestellung.kunde.notNull}", groups = PreExistingGroup.class)
	@JsonIgnore
	private AbstractKunde kunde;
	
	@Transient
	private URI kundeUri;
	
	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@NotEmpty(message = "{bestellverwaltung.bestellung.bestellpositionen.notEmpty}")
	@Valid
	private List<Bestellposition> bestellpositionen;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;
	
	public Bestellung() {
		super();
	}
	
	public Bestellung(List<Bestellposition> bestellpositionen) {
		super();
		this.bestellpositionen = bestellpositionen;
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
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isAusgeliefert() {
		return ausgeliefert;
	}
	public void setAusgeliefert(boolean ausgeliefert) {
		this.ausgeliefert = ausgeliefert;
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
	
	public List<Bestellposition> getBestellpositionen() {
		if (bestellpositionen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(bestellpositionen);
	}
	
	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellpositionen.clear();
		if (bestellpositionen != null) {
			this.bestellpositionen.addAll(bestellpositionen);
		}
	}
	
	public Bestellung addBestellposition(Bestellposition bestellposition) {
		if (bestellpositionen == null) {
			bestellpositionen = new ArrayList<>();
		}
		bestellpositionen.add(bestellposition);
		return this;
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
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
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
		if (erzeugt == null) {
			if (other.erzeugt != null)
				return false;
		} else if (!erzeugt.equals(other.erzeugt))
			return false;
		if (kunde == null) {
			if (other.kunde != null)
				return false;
		} else if (!kunde.equals(other.kunde))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", ausgeliefert=" + ausgeliefert
				+ ", kunde=" + kunde + ", kundeUri=" + kundeUri
				+ ", bestellpositionen=" + bestellpositionen + ", erzeugt="
				+ erzeugt + ", aktualisiert=" + aktualisiert + "]";
	}

}
