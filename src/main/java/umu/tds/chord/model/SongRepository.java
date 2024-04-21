package umu.tds.chord.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import umu.tds.chord.dao.DAOFactory;

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
		
		DAOFactory.getInstance()
			.getSongDAO()
			.recoverAll()
			.forEach(s -> songs.add(s));
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
		
		// Comprobación de duplicidad de la canción.
		if (songs.contains(song))
			return false;
		
		// Persistencia.
		boolean persistence = DAOFactory.getInstance()
								.getSongDAO()
								.register(song.asMut());
				
		// Fallo de registro en persistencia.
		if (!persistence) return false;
		
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
	
	/**
	 * Elimina la canción y su información aosicada.
	 * 
	 * @param s Canción que se desea eliminar.
	 * 
	 * @return {@code true} si la canción estaba en el repositorio y se ha
	 * podido eliminar.
	 */
	public boolean removeSong(Song s) {
		// Comprobar que la canción está en el repositorio.
		if (s == null || !songs.contains(s)) return false;
		
		// Eliminación de persistencia.
		boolean persistence = DAOFactory.getInstance()
								.getSongDAO()
								.delete(s.asMut());
		
		// Eliminación de memoria.
		if (persistence)
			songs.remove(s);
		
		return persistence;
	}
	
	/**
	 * Función utilizada para la búsqueda de canciones mediante filtros.
	 * 
	 * @param name Nombre de la canción. Una cadena vacía "" se corresponde con
	 * cualquier nombre de canción.
	 * @param author Autor. Una cadena vacía "" se corresponde con todos los autores.
	 * @param s Estilo de la canción. {@link Style#TODOS} se corresponde con 
	 * todos los estilos.
	 * 
	 * @return Lista mutable de canciones encontrada que respeta los filtros
	 * especificados.
	 */
	public List<Song> getSearch(String name, String author, Style s) {
		return songs.stream()
				.filter(song -> song.getName().contains(name))
				.filter(song -> song.getAuthor().contains(author))
				.filter(song -> {
					if (!s.equals(Style.TODOS))
						return song.getStyle().equals(s);
					return true;
				})
				.collect(Collectors.toList());
	}
}
