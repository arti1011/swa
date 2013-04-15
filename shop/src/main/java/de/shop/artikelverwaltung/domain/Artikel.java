package de.shop.artikelverwaltung.domain;


import java.io.Serializable;


// TODO überprüfen ob nötig
//import java.net.URI;
import java.util.Set;
// TODO überprüfen ob nötig
//import org.codehaus.jackson.annotate.JsonIgnore;

public class Artikel implements Serializable  {
	
	private static final long serialVersionUID = 161835922543423714L;
	
	private Long id;
	private String artikelBezeichnung;
	private Double preis;
	private Set<ArtikelFarbeType> farbe;
	private String verfuegbarkeit;
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
	public String getVerfuegbarkeit() {
		return verfuegbarkeit;
	}
	public void setVerfuegbarkeit(String verfuegbarkeit) {
		this.verfuegbarkeit = verfuegbarkeit;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
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
		return "Artikel [id=" + id + ", artikelBezeichnung="
				+ artikelBezeichnung + ", preis=" + preis + ", farbe=" + farbe
				+ ", verfuegbarkeit=" + verfuegbarkeit + "]";
	}
	
	

	
	

}