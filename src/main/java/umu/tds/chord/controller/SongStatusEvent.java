package umu.tds.chord.controller;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import umu.tds.chord.model.Song;

/**
 * Clase que representa el estado de los eventos producidos que tienen que 
 * ver con las canciones.
 */
public class SongStatusEvent extends EventObject{

	private static final long serialVersionUID = 7708870295969293430L;
	
	private List<Song> songs;
	private boolean failed;
	
	/**
	 * Constructor por defecto.
	 * 
	 * @param source Origen del evento.
	 */
	public SongStatusEvent(Object source) {
		super(source);
		
		this.songs = new ArrayList<>();
		this.failed = false;
	}
	
	/**
	 * Obtiene la lista de canciones del evento.
	 * 
	 * @return Lista de canciones del evento.
	 */
	public List<Song> getSongs() {
		return songs;
	}
	
	/**
	 * Añade una canción a la lista de canciones del evento.
	 * 
	 * @param song Canción que se desea añadir.
	 */
	protected void addSong(Song song) {
		songs.add(song);
	}
	
	/**
	 * Indica que se ha producido un fallo.
	 * 
	 * @return Estado de fallo del evento.
	 */
	public boolean isFailed() {
		return failed;
	}
	
	/**
	 * Establece el estado de fallo del evento.
	 * 
	 * @param failed Nuevo estado de fallo del evento.
	 */
	protected void setFailed(boolean failed) {
		this.failed = failed;
	}
}
