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
	private final Set<String> styles;
	private final String allStyles;
	
	private SongRepository() {
		songs = new HashSet<Song>();
		styles = new HashSet<String>();
		allStyles = "Todos";
		
		styles.add(allStyles);
		
		DAOFactory.getInstance()
			.getSongDAO()
			.recoverAll()
			.forEach(s -> {
				songs.add(s);
				styles.add(s.getStyle());
			});
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
	public boolean addSong(String name, String author, String path, String sty) 
	{
		
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
		
		styles.add(sty);
		songs.add(song);
		return true;
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
	public boolean removeSong(Song song) {
		// Comprobar que la canción está en el repositorio.
		if (song == null || !songs.contains(song)) return false;
		
		// Eliminación de persistencia.
		boolean persistence = DAOFactory.getInstance()
								.getSongDAO()
								.delete(song.asMut());
		
		// Eliminación de memoria.
		if (persistence) {
			songs.remove(song);
			
			// Eliminar el estilo de la lista si no quedan canciones del mismo.
			String style = song.getStyle();
			if (!songs.stream().anyMatch(s -> s.getStyle().equals(style)))
				styles.remove(style);
		}
		
		return persistence;
	}
	
	/**
	 * Función utilizada para la búsqueda de canciones mediante filtros.
	 * 
	 * @param name Nombre de la canción. Una cadena vacía "" se corresponde con
	 * cualquier nombre de canción.
	 * @param author Autor. Una cadena vacía "" se corresponde con todos los 
	 * autores.
	 * @param sty Estilo de la canción. El comodín no filtrará ninguna.
	 * 
	 * @return Lista mutable de canciones encontrada que respeta los filtros
	 * especificados.
	 */
	public List<Song> getSearch(String name, String author, String sty) {
		return songs.stream()
				.filter(song -> song.getName().contains(name))
				.filter(song -> song.getAuthor().contains(author))
				.filter(song -> {
					if (!sty.equals(allStyles))
						return song.getStyle().equals(sty);
					return true;
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * Retorna el set de estilos de las canciones del repositorio.
	 * 
	 * @return Set de estilos de las canciones.
	 */
	public Set<String> getStyles() {
		return Collections.unmodifiableSet(styles);
	}
	
	/**
	 * Función para obtener el estilo comodín utilizado en las búsquedas
	 * @return
	 */
	public String getStyleWildcard() {
		return allStyles;
	}
}
