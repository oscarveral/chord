package umu.tds.chord.model;

/**
 * Interfaz funcional que permite exponer una clase inmutable como una mutable
 * con el objetivo de poder acceder a métodos que modifiquen su estado no 
 * disponibles en la clase original.
 * 
 * @param <T> Clase que representará la versión mutable de la clase 
 * implementadora.
 */
public interface Mutable<T> {

	/**
	 * Método para obtener una representación mutable de la clase 
	 * inplementadora.
	 * 
	 * @return Instancia de la clase que representa la versión mutable de la
	 * clase implementadora.
	 */
	public T asMut();
}
