package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Artikel implements Serializable {

	private static final long serialVersionUID = -6241224714579020680L;

	public Artikel(Long id, String artikelBezeichnung, BigDecimal preis,
			String information) {
		super();
		this.id = id;
		this.artikelBezeichnung = artikelBezeichnung;
		this.preis = preis;
		this.information = information;
	}

	private Long id;
	private String artikelBezeichnung;
	private BigDecimal preis;
	/*
	 * private Enum farbeType {
		Schwarz,
		Weiﬂ,
		Grau,
		Braun,
		Blau
	}
	*/
	private String information;
	
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
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((artikelBezeichnung == null) ? 0 : artikelBezeichnung
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((information == null) ? 0 : information.hashCode());
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
		Artikel other = (Artikel) obj;
		if (artikelBezeichnung == null) {
			if (other.artikelBezeichnung != null)
				return false;
		} else if (!artikelBezeichnung.equals(other.artikelBezeichnung))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (information == null) {
			if (other.information != null)
				return false;
		} else if (!information.equals(other.information))
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
				+ artikelBezeichnung + ", preis=" + preis + ", information="
				+ information + "]";
	}
}