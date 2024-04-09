package umu.tds.chord.model;

/**
 * Clase que representa los diferentes estilos musicales que pueden tener las
 * canciones.
 */
public enum Style {
		
	POP("Pop"),
	ROCK("Rock"),
	JAZZ("Jazz"),
	LATIN("Latin"),
	CLASICAL("Clasical"),
	
	/**
	 * Estilo comodín.
	 */
	TODOS("Todos");
	
	// ----------------
	
	private final String name;

	private Style(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
