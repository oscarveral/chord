package umu.tds.chord.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.dao.DAOFactory;

/**
 * Repositorio de canciones. Utilizado para el registro y recuperación de
 * canciones. Dado que las canciones son inmutables no se ofrecen métodos de
 * actualización. Véase {@link Song}.
 */
public enum SongRepository {

	INSTANCE;

	public static final String ALL_STYLES = "Todos";
	private static final String emptyFilter = "";
	
	private final Set<Song> songs;
	private final Set<String> styles;

	private SongRepository() {
		songs = new HashSet<>();
		styles = new HashSet<>();
		//ALL_STYLES = "Todos";

		styles.add(ALL_STYLES);

		DAOFactory.getInstance().getSongDAO().recoverAll().forEach(s -> {
			songs.add(s);
			styles.add(s.getStyle());
		});
	}

	/**
	 * Método para registrar una canción en el repositorio.
	 *
	 * @param name   Nombre de la canción.
	 * @param author Autor de la canción.
	 * @param path   Ruta al fichero de la canción.
	 * @param sty    Estilo musical de la canción.
	 *
	 * @return {@code false} si la canción ya estaba registrada o ha ocurrido un
	 *         fallo con el servidio de persistencia. {@code true} en cualquier otro
	 *         caso.
	 */
	public Optional<Song> addSong(String name, String author, String path, String sty) {
		if (name.isBlank() || name.isEmpty() || author.isBlank() || author.isEmpty() || path.isBlank() || path.isEmpty()
				|| sty.isBlank() || sty.isEmpty()) {
			return Optional.empty();
		}

		// Creación de la canción.
		Song song = new Song.Builder(name).author(author).path(path).style(sty).build().get();

		// Comprobación de duplicidad de la canción.
		if (songs.contains(song)) {
			return Optional.empty();
		}

		// Persistencia.
		boolean persistence = DAOFactory.getInstance().getSongDAO().register(song.asMut());
		
		// Fallo de registro en persistencia.
		if (!persistence) {
			return Optional.empty();
		}

		styles.add(sty);
		songs.add(song);
		return Optional.of(song);
	}

	/**
	 * Función utilizada para la búsqueda de canciones mediante filtros.
	 *
	 * @param name   Nombre de la canción. Una cadena vacía "" se corresponde con
	 *               cualquier nombre de canción.
	 * @param author Autor. Una cadena vacía "" se corresponde con todos los
	 *               autores.
	 * @param sty    Estilo de la canción. El comodín no filtrará ninguna.
	 *
	 * @return Lista mutable de canciones encontrada que respeta los filtros
	 *         especificados.
	 *
	 * @implNote Cualquier parámetro nulo provocará la devolución de una lista
	 *           vacía.
	 */
	public List<Song> getSearch(Optional<String> n, Optional<String> a, Optional<String> s) {

		String name = n.isPresent() ? n.get() : emptyFilter;
		String author = a.isPresent() ? a.get() : emptyFilter;
		String sty = s.isPresent() ? s.get() : ALL_STYLES;
		
		return songs.stream().filter(song -> song.getName().toLowerCase().contains(name.toLowerCase()))
				.filter(song -> song.getAuthor().toLowerCase().contains(author.toLowerCase())).filter(song -> {
					if (!sty.equals(ALL_STYLES)) {
						return song.getStyle().equals(sty);
					}
					return true;
				}).toList();
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
	 * Comprueba la existencia en el repositorio de una canción determinada.
	 * 
	 * @param name Nombre de la canción.
	 * @param author Autor de la canción.
	 * @param path Ruta de la canción.
	 * @param style Estilo de la canción.
	 * 
	 * @return {@code true} si existe la canción especificada.
	 */
	public boolean existSong(String name, String author, String path, String style) {
		Song song = new Song.Builder(name).author(author).path(path).style(style).build().get();
		return songs.stream().anyMatch(s -> s.equals(song));
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
	 * Comprueba la existencia en el repositorio de un estilo determinado.
	 * 
	 * @param style Estilo que se desea consultar.
	 * 
	 * @return {@code true} si existe el estilo especificado.
	 */
	public boolean existStyle(String style) {
		return styles.contains(style);
	}
	
	/**
	 * Función para obtener el estilo comodín utilizado en las búsquedas
	 *
	 * @return
	 */
	public String getStyleWildcard() {
		return ALL_STYLES;
	}

	/**
	 * Elimina la canción y su información aosicada. Se mantiene la consistencia con
	 * los datos de los usuarios, eliminandose de sus playlists y de sus listas de
	 * canciones favoritas.
	 *
	 * @param s Canción que se desea eliminar.
	 *
	 * @return {@code true} si la canción estaba en el repositorio y se ha podido
	 *         eliminar.
	 */
	public boolean removeSong(Song song) {
		// Comprobar que la canción está en el repositorio.
		if (song == null || !songs.contains(song)) {
			return false;
		}

		// Eliminación de persistencia.
		// Uso la versión de la canción del repositorio ya que se asegura que 
		// tiene un id de persistencia asociado. Como Song hace override de 
		// equals sería posible pasar como parámetro una canción igual pero no 
		// registrada en persistencia. Habilitamos la posibilidad de
		// realizar la construcción de canciones nuevas fuera del repositorio.
		song = songs.stream().filter(song::equals).findFirst().get();
		boolean persistence = DAOFactory.getInstance().getSongDAO().delete(song.asMut());
		
		// Eliminación de memoria.
		if (persistence) {
			songs.remove(song);

			// Eliminar referencias a la canción de todos los usuarios.
			removeSongFromUsers(song);

			// Eliminar el estilo de la lista si no quedan canciones del mismo.
			String style = song.getStyle();
			if (songs.stream().noneMatch(s -> s.getStyle().equals(style))) {
				styles.remove(style);
			}
		}
		
		
		return persistence;
	}

	private void removeSongFromUsers(Song s) {
		// Método de sincronización de las canciones de los usuarios.
		UserRepository.INSTANCE.getUsers().stream().map(Mutable::asMut).forEach(u -> {
			// Para cada usuario eliminamos la canción de favoritos y
			// recientes.
			u.removeFavouriteSong(s);
			u.removeRecentSong(s);
			u.getPlaylists().stream().map(Mutable::asMut)
					// Eliminamos la canción de todas las playlists.
					.forEach(p -> p.removeAll(s));
			// Actualización de cada usuario.
			UserRepository.INSTANCE.updateUser(u);
		});
	}
	
	/**
	 * Método para actualizar los valores de una canción en persistencia.
	 * 
	 * @param s Canción que se desea actualizar.
	 * 
	 * @return Resultado de la operación.
	 */
	public boolean updateSong(Song s) {
		if (s == null || !songs.contains(s)) return false;
		boolean persistence  = DAOFactory.getInstance().getSongDAO().modify(s.asMut());
		if (!persistence) {
			removeSongFromUsers(s);
			removeSong(s);
		}
		return persistence;
	}
 	
	// ---------- Depuración. ----------
	
	/**
	 * @apiNote ALERTA. Método de depuración.
	 * 
	 * Fuerza un reseteo del estado del repositorio de canciones. Se eliminarán
	 * todas las canciones de persistencia.
	 */
	public void clearSonRepositoryState() {
		// Quitar todas las canciones.
		songs.forEach(s -> {
			removeSongFromUsers(s);
			DAOFactory.getInstance().getSongDAO().delete(s.asMut());
		});
		songs.clear();
		styles.clear();
		styles.add(ALL_STYLES);
	}
	
	
}
