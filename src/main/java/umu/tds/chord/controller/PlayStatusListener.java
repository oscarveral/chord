package umu.tds.chord.controller;

import java.util.EventListener;

/**
 * Interfaz para los eventos de escucha de canciones.
 */
public interface PlayStatusListener extends EventListener {
	
	/**
	 * Emitido cuando se reproduce una canción.
	 * 
	 * @param e Evento de reproducción de canción.
	 */
	public default void onSongReproduction(SongPlayEvent e) {}

}
