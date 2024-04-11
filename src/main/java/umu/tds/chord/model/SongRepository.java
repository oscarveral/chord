package umu.tds.chord.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Repositorio de canciones. Utilizado para el registro y recuperación de 
 * canciones. Dado que las canciones son inmutables no se ofrecen métodos de
 * actualización. Véase {@link Song}.
 */
public enum SongRepository {

	INSTANCE;
	
	private final Set<Song> songs;
	
	private SongRepository() {
		songs = new HashSet<Song>();
		
		/*
		DAOFactory.getInstance()
			.getSongDAO()
			.recoverAll()
			.forEach(s -> songs.add(u));
		*/
	}
	
	/**
	 * Método para registrar una canción en el repositorio.
	 * 
	 * @param name Nombre de la canción.
	 * @param author Autor de la canción.
	 * @param path Ruta al fichero de la canción.
	 * @param sty Estilo musical de la canción.
	 * 
	 * @return {@code false} si la canción ya estaba registrada o ha ocurrido
	 * un fallo con el servidio de persistencia. {@code true} en cualquier otro
	 * caso.
	 */
	public boolean addSong(String name, String author, String path, Style sty) {
		
		// Creación de la canción.
		Song song = new Song.Builder(name)
						.author(author)
						.path(path)
						.style(sty)
						.build()
						.get();
		
		// Comprobación de existencia.
		if (songs.contains(song))
			return false;
		
		/*
		// Persistencia.
		boolean persistence = DAOFactory.getInstance()
								.getSongDAO()
								.register(song);
				
		// Fallo de registro en persistencia.
		if (!persistence) return false;
		*/
		
		return songs.add(song);
	}
	
	/**
	 * Método para obtener un conjunto no modificable de las canciones del 
	 * repositorio. Véase {@link Song}.
	 * 
	 * @return Conjunto no modificable con todas las canciones del repositorio.
	 */
	public Set<Song> getSongs() {
		return Collections.unmodifiableSet(this.songs);
	}
}
