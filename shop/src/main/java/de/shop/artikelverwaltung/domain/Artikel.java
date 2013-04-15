package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
// TODO �berpr�fen ob n�tig
//import java.net.URI;
import java.util.Set;
// TODO �berpr�fen ob n�tig
//import org.codehaus.jackson.annotate.JsonIgnore;


import de.shop.kundenverwaltung.domain.HobbyType;

public class Artikel implements Serializable  {
	private static final long serialVersionUID = 161835922343423714L;
	
	private Long id;
	private String artikelbezeichnung;
	private Double preis;
	private Set<ArtikelFarbeType> farbe;
	private String verf�gbarkeit;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArtikelbezeichnung() {
		return artikelbezeichnung;
	}

	public void setArtikelbezeichnung(String artikelbezeichnung) {
		this.artikelbezeichnung = artikelbezeichnung;
	}

	public Double getPreis() {
		return preis;
	}

	public void setPreis(Double preis) {
		this.preis = preis;
	}

	public Set<ArtikelFarbeType> getFarbe() {
		return farbe;
	}

	public void setFarbe(Set<ArtikelFarbeType> farbe) {
		this.farbe = farbe;
	}

	public String getVerf�gbarkeit() {
		return verf�gbarkeit;
	}

	public void setVerf�gbarkeit(String verf�gbarkeit) {
		this.verf�gbarkeit = verf�gbarkeit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
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
		Artikel other = (Artikel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [artikelid=" + id + ", artikelbezeichnung="
				+ artikelbezeichnung + ", preis=" + preis + ", farbe=" + farbe
				+ ", verf�gbarkeit=" + verf�gbarkeit + "]";
	}

}
