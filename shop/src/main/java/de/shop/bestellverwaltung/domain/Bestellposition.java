package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;

public class Bestellposition implements Serializable {

	private static final long serialVersionUID = 1618359231454653714L;
	
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellposition.positionId.min}", groups = IdGroup.class)
	private Long positionId;
	
	@NotNull(message = "{bestellverwaltung.bestellposition.artikel.notNull}")
	@JsonIgnore
	private Artikel artikel;
	
	private URI artikelUri;
	
	@Min(value = 1, message = "{bestellverwaltung.bestellposition.anzahl.min}")
	private Long anzahl;
	
	public Long getPositionId() {
		return positionId;
	}
	public void setPositionId(Long positionId) {
		this.positionId = positionId;
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
	public Long getAnzahl() {
		return anzahl;
	}
	public void setAnzahl(Long anzahl) {
		this.anzahl = anzahl;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((anzahl == null) ? 0 : anzahl.hashCode());
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
		result = prime * result
				+ ((positionId == null) ? 0 : positionId.hashCode());
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
		final Bestellposition other = (Bestellposition) obj;
		if (anzahl == null) {
			if (other.anzahl != null)
				return false;
		} 
		else if (!anzahl.equals(other.anzahl))
			return false;
		if (artikel == null) {
			if (other.artikel != null)
				return false;
		}
		else if (!artikel.equals(other.artikel))
			return false;
		if (positionId == null) {
			if (other.positionId != null)
				return false;
		}
		else if (!positionId.equals(other.positionId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Bestellposition [positionId=" + positionId + ", artikel="
				+ artikel + ", artikelUri=" + artikelUri + ", anzahl=" + anzahl
				+ "]";
	}
	
	
}
