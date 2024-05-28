package umu.tds.chord.controller;

import java.util.EventListener;

/**
 * Interfaz para los escuchadores de eventos relacionados con las canciones.
 */
public interface SongStatusListener extends EventListener {

	/**
	 * Emitido cuando se realiza una carga de canciones.
	 * 
	 * @param e Evento de estado de canciones.
	 */
	public default void onSongLoad(SongStatusEvent e) {}
	
	/**
	 * Emitido cuando se realiza una eliminación de canciones.
	 * 
	 * @param e Evento de estado de canciones.
	 */
	public default void onSongDelete(SongStatusEvent e) {}
	
	/**
	 * Emitido cuando se hace una búsqueda de canciones.
	 * 
	 * @param e Evento de estado de canciones.
	 */
	public default void onSongSearch(SongStatusEvent e) {}
}
