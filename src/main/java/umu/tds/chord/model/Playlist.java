package umu.tds.chord.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase abstracta que representa una playlist. Expone sólo métodos de lectura
 * de datos.
 */
public sealed abstract class Playlist permits Playlist.Internal {
	
	private final String name;
	private final String description;
	private final List<Song> songs;
	
	/**
	 * Constructor general de playlist.
	 * 
	 * @param name Nombre que tendrá la playlist.
	 * @param description Descripción de la playlist.
	 * @param songs Lista de las canciones que contiene. Véase {@link Song}.
	 */
	private Playlist(String name, String description, List<Song> songs) {
		this.name = name;
		this.description = description;
		this.songs = songs;
	}
	
	/**
	 * Constructor simple de playlists. Se inicia con la lista de canciones
	 * vacía.
	 * 
	 * @param name Nombre de la playlist.
	 * @param description Descripción de la playlist.
	 */
	private Playlist(String name, String description) {
		this(name, description, new ArrayList<Song>());
	}
	
	/**
	 * Método para obtener el nombre de la playlist.
	 * 
	 * @return Nombre de la playlist.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Método para obtener la descripción de la playlist.
	 * 
	 * @return Descripción de la playlist.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Método para obtener una lista no modificable de canciones de la playlist.
	 * 
	 * @apiNote Recuérdese que {@link Song} es una clase inmutable.
	 * 
	 * @return Lista no modificable de las canciones de la playlist.
	 */
	public List<Song> getSongs() {
		return Collections.unmodifiableList(songs);
	}
	
	/**
	 * Clase de representación interna de una playlist. Expone métodos que 
	 * permiten mutar el estado de la playlist.
	 */
	public final static class Internal extends Playlist{
		
		/**
		 * Constructor general de playlist.
		 * 
		 * @param name Nombre que tendrá la playlist.
		 * @param description Descripción de la playlist.
		 * @param songs Lista de las canciones que contiene. Véase {@link Song}.
		 */
		public Internal(String name, String description, List<Song> songs) {
			super(name, description, songs);
		}
		
		/**
		 * Constructor simple de playlists. Se inicia con la lista de canciones
		 * vacía.
		 * 
		 * @param name Nombre de la playlist.
		 * @param description Descripción de la playlist.
		 */
		public Internal(String name, String description) {
			super(name, description);
		}
			
		/**
		 * Método para añadir una canción a la lista en la posición deseada.
		 * 
		 * @param song Canción que se desea añadir.
		 */
		public void add(int index, Song song) {
			super.songs.add(index, song);
		}
		
		/**
		 * Método para eliminar una canción de la lista según el índice dado.
		 * 
		 * @param index Indice de la canción de la lista que se desea eliminar.
		 * 
		 * @return Canción eliminada de la lista.
		 */
		public Song remove(int index) {
			return super.songs.remove(index);
		}
		
		/**
		 * Método para obtener una canción de la lista según el índice dado.
		 * 
		 * @param index Índice de la canción que se desea obtener.
		 * 
		 * @return Canción de la lista correspondiente al índice dado.
		 */
		public Song get(int index) {
			return super.songs.get(index);
		}
	}
}