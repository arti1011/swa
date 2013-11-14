package de.shop.kundenverwaltung.domain;

import static de.shop.kundenverwaltung.domain.AbstractKunde.PRIVATKUNDE;
import static javax.persistence.FetchType.EAGER;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen Zimmermann</a>
 */
@Entity
@Inheritance
@DiscriminatorValue(PRIVATKUNDE)
@Cacheable
@XmlRootElement
public class Privatkunde extends AbstractKunde {
	private static final long serialVersionUID = -1783340753647408724L;
	
	private static final String PREFIX = "Privatkunde.";
	public static final String FIND_BY_GESCHLECHT = PREFIX + "findByGeschlecht";
	public static final String PARAM_GESCHLECHT = "geschlecht";
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_hobby",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
                     uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "hobby" }))
	@Column(table = "kunde_hobby", name = "hobby", length = 2, nullable = false)
	private Set<HobbyType> hobbies;
	
	public Privatkunde() {
		super();
	}
	
	public Privatkunde(String nachname, String vorname, String email, Date seit) {
		super(nachname, vorname, email, seit);
	}
	
	@Override
	public void setValues(AbstractKunde k) {
		super.setValues(k);
		
		if (!k.getClass().equals(Privatkunde.class)) {
			return;
		}
		
		final Privatkunde pk = (Privatkunde) k;
		hobbies = pk.hobbies;
	}
	
	public Set<HobbyType> getHobbies() {
		if (hobbies == null) {
			return null;
		}
		return Collections.unmodifiableSet(hobbies);
	}
	
	public void setHobbies(Set<HobbyType> hobbies) {
		if (this.hobbies == null) {
			this.hobbies = hobbies;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.hobbies.clear();
		if (hobbies != null) {
			this.hobbies.addAll(hobbies);
		}
	}

	@Override
	public String toString() {
		return "Privatkunde [" + super.toString() + ", hobbies=" + hobbies + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// Fuer Validierung an der Benutzeroberflaeche
		final Privatkunde neuesObjekt = Privatkunde.class.cast(super.clone());
		
		neuesObjekt.hobbies = hobbies;
		
		return neuesObjekt;
	}
}
