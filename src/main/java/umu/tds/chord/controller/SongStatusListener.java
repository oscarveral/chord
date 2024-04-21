package umu.tds.chord.controller;

import java.util.EventListener;
import java.util.List;

import umu.tds.chord.model.Song;

/**
 * Interfaz para los escuchadores de eventos relacionados con las canciones.
 */
public interface SongStatusListener extends EventListener{
	
	/**
	 * Método emitido cuando se ha producido una búsqueda de canciones.
	 * 
	 * @param searched Lista con canciones resultado de la búsqueda.
	 */
	default public void onSongSearch(List<Song> searched) {
	};

}
