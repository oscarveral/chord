package umu.tds.chord.component;

import java.util.EventObject;

/**
 * Evento que representa la carga de una nueva lista de canciones.
 */
public final class CancionesEvent extends EventObject {
	
	private static final long serialVersionUID = 2550233816129020392L;
	
	private Canciones canciones;
	
	/**
	 * Contructor del evento.
	 * 
	 * @param fuente Fuente del evento.
	 * @param canciones Lista de canciones cargadas.
	 */
	public CancionesEvent(Object fuente, Canciones canciones) {
		super(fuente);
		this.canciones = canciones;
	}
	
	/**
	 * Método par aobtener las canciones del evento.
	 * 
	 * @return canciones que se han cargado.
	 */
	public Canciones getCanciones() {
		return canciones;
	}
}