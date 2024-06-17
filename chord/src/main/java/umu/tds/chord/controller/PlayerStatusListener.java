package umu.tds.chord.controller;

import java.util.EventListener;

/**
 * Interfaz para los eventos de escucha de canciones.
 */
public interface PlayerStatusListener extends EventListener {

	/**
	 * Emitido cuando se inicia la reproducción de una canción.
	 * 
	 * @param e Evento de reproducción de canción.
	 */
	public default void onSongReproduction(PlayerStatusEvent e) {
	}

	/**
	 * Emitido periodicamente durante la reproducción de una canción para permitir
	 * llevar la cuenta del progreso de la canción actual.
	 * 
	 * @param e Evento de reprodución de una canción.
	 */
	public default void onSongProgress(PlayerStatusEvent e) {
	}
}
