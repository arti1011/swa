package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import java.net.URI;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.sun.xml.internal.rngom.util.Uri;

import de.shop.artikelverwaltung.domain.Artikel;

public class Bestellposition implements Serializable {

	private static final long serialVersionUID = 1618359231454653714L;
	
	private Long positionId;
	@JsonIgnore
	private Artikel artikel;
	private URI artikelUri;
	@JsonIgnore
	private Bestellung bestellung;
	private URI bestellungUri;
	private Long Anzahl;
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
	public Bestellung getBestellung() {
		return bestellung;
	}
	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
	}
	public URI getBestellungUri() {
		return bestellungUri;
	}
	public void setBestellungUri(URI bestellungUri) {
		this.bestellungUri = bestellungUri;
	}
	public Long getAnzahl() {
		return Anzahl;
	}
	public void setAnzahl(Long anzahl) {
		Anzahl = anzahl;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bestellung == null) ? 0 : bestellung.hashCode());
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
		Bestellposition other = (Bestellposition) obj;
		if (bestellung == null) {
			if (other.bestellung != null)
				return false;
		} else if (!bestellung.equals(other.bestellung))
			return false;
		if (positionId == null) {
			if (other.positionId != null)
				return false;
		} else if (!positionId.equals(other.positionId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Bestellposition [positionId=" + positionId + ", artikel="
				+ artikel + ", artikelUri=" + artikelUri + ", bestellung="
				+ bestellung + ", bestellungUri=" + bestellungUri + ", Anzahl="
				+ Anzahl + "]";
	}
	
	
	
}
