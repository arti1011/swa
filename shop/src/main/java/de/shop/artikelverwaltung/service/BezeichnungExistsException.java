package de.shop.artikelverwaltung.service;

/**
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen Zimmermann</a>
 */
public class BezeichnungExistsException extends AbstractArtikelServiceException {
	private static final long serialVersionUID = 4867667611097919943L;

	private static final String MESSAGE_KEY = "artikel.bezeichnungExists";
	private final String bezeichnung;

	public BezeichnungExistsException(String bezeichnung) {
		super("Die Bezeichnung " + bezeichnung + " existiert bereits");
		this.bezeichnung = bezeichnung;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
