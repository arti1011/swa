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
	
	@NotNull(message = "{bestellverwaltung.bestellposition.bestellung.notNull}")
	@JsonIgnore
	private Bestellung bestellung;
	
	private URI bestellungUri;
	
	@Min(1)
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
		return anzahl;
	}
	public void setAnzahl(Long anzahl) {
		this.anzahl = anzahl;
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
		}
		else if (!bestellung.equals(other.bestellung))
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
				+ artikel + ", artikelUri=" + artikelUri + ", bestellung="
				+ bestellung + ", bestellungUri=" + bestellungUri + ", anzahl="
				+ anzahl + "]";
	}

}
