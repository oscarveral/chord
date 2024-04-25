package umu.tds.chord.controller;

import java.util.EventListener;
import java.util.List;
import java.util.Set;

import umu.tds.chord.model.Song;

/**
 * Interfaz para los escuchadores de eventos relacionados con las canciones.
 */
public interface SongStatusListener extends EventListener{
	
	/**
	 * Método emitido cuando se ha producido una búsqueda de canciones.
	 * 
	 * @param searched Lista con canciones resultado de la búsqueda.
	 * @param f Indica si se realizó una búsqueda de favoritos.
	 */
	default public void onSongSearch(List<Song> searched, boolean f) {
	}
	
	/**
	 * Método emitido cuando ha cambiado la lista de estilos de las canciones.
	 * 
	 * @param styles Lista de los estilos actuales.
	 * @param wildcard Estilo comodín actual. Deberá estar presente en styles.
	 */
	default public void onStyleList(Set<String> styles, String wildcard) {
	}
	
	/**
	 * Método emitido cuando se ha seleccionado una lista de canciones.
	 * 
	 * @param songs Lista de canciones que se ha seleccionado.
	 */
	default public void onSongSelection(List<Song> songs) {
	}
	
	/**
	 * Método emitido cuando ha cambiado la lista de canciones.
	 * 
	 * @param songs Conjunto nuevo de canciones.
	 */
	default public void onSongList(Set<Song> songs) {
	}
	
	/**
	 * Método emitido si ocurre un fallo de carga de canciones nuevas.
	 */
	default public void onSongLoadFailure() {
	}
	
	/**
	 * Método emitido en los fallos de eliminación de canciones.
	 */
	default public void onSongDeleteFailure() {
	}
}
