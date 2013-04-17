package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.kundenverwaltung.domain.AbstractKunde;

public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1618359234119003714L;
	
	private Long id;
	private boolean ausgeliefert;
	@JsonIgnore
	private AbstractKunde kunde;
	private URI kundeUri;
	@JsonIgnore
	private List<Bestellposition> bestellpositionen;
	private URI bestellpositionenUri;
	
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
		return bestellpositionen;
	}
	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		this.bestellpositionen = bestellpositionen;
	}
	public URI getBestellpositionenUri() {
		return bestellpositionenUri;
	}
	public void setBestellpositionenUri(URI bestellungenUri) {
		this.bestellpositionenUri = bestellungenUri;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		return "Bestellung [id=" + id + ", ausgeliefert=" + ausgeliefert
				+ ", kunde=" + kunde + ", kundeUri=" + kundeUri
				+ ", bestellpositionen=" + bestellpositionen
				+ ", bestellungenUri=" + bestellpositionenUri + "]";
	}
	
	
}
