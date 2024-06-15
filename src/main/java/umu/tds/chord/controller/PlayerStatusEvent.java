package umu.tds.chord.controller;

import java.util.EventObject;
import java.util.Optional;

import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;

/**
 * Evento de escucha de canciones.
 */
public class PlayerStatusEvent extends EventObject {

	private static final long serialVersionUID = -6460593865001084008L;

	private Optional<Song> song;
	private Optional<Playlist> playlist;
	
	/**
	 * Constructor del evento.
	 * 
	 * @param source Origen del evento.
	 */
	public PlayerStatusEvent(Object source) {
		super(source);
		this.song = Optional.empty();
		this.playlist = Optional.empty();
	}
	
	/**
	 * Establece la canción actual del evento de reproducción.
	 * 
	 * @param s Canción que se está reproduciendo.
	 */
	public void setSong(Song s) {
		song = Optional.ofNullable(s);
	}
	
	/**
	 * Obtiene la canción en reproducción del evento.
	 * 
	 * @return Canción que se está reproduciendo.s
	 */
	public Optional<Song> getSong() {
		return song;
	}
	
	/**
	 * Establece la playlist en reproducción del evento.
	 * 
	 * @param p Playlist que se está reproduciendo.
	 */
	public void setPlaylist(Playlist p) {
		playlist = Optional.ofNullable(p);
	}
	
	/**
	 * Obtiene la playlist en reproducción del evento.
	 * 
	 * @return Playlisy que se está reproduciendo.
	 */
	public Optional<Playlist> getPlaylist() {
		return playlist;
	}
}
