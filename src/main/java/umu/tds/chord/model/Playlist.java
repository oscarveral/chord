package umu.tds.chord.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Objects;

/**
 * Clase abstracta que representa una playlist. Expone sólo métodos de lectura
 * de datos.
 */
public abstract sealed class Playlist implements Mutable<Playlist.Internal> {

	/**
	 * Clase constructora de playlists.
	 */
	public static final class Builder {

		private String description;
		private String name;
		private List<Song> songs;

		/**
		 * Crea un nuevo builder a partir del nombre de la playlist. Por defecto la
		 * descripción será {@code null} y la lista de canciones estará vacia. Se
		 * obligará a dar una descripción a la lista.
		 *
		 * @param name Nombre de la playlist.
		 */
		public Builder(String name) {
			this.name = name;
			this.description = null;
			this.songs = new ArrayList<>();
		}

		/**
		 * Construye un playlist a partir de la información actual.
		 *
		 * @return Playlist construida o un opcional vacío si no se ha proporcionado una
		 *         descripción.
		 */
		public Optional<Playlist> build() {
			if (!validate()) {
				return Optional.empty();
			}

			return Optional.of(new Playlist.Internal(this));
		}

		/**
		 * Establece la descripción de la playlist.
		 *
		 * @param description Descripción de la playlist.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Establece la lista de canciones de la playlist.
		 *
		 * @param songs Lista de canciones de la playlist.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder songs(List<Song> songs) {
			this.songs = new ArrayList<>(songs);
			return this;
		}

		private boolean validate() {
			// Forzar a que la descripción no sea nula.
			return this.description != null;
		}
	}

	/**
	 * Clase de representación interna de una playlist. Expone métodos que permiten
	 * mutar el estado de la playlist. Se exponen también los métodos necesarios
	 * para la persistencia {@link Persistent}.
	 */
	public static final class Internal extends Playlist implements Persistent {

		private int id;
		private boolean isRegistered;

		/**
		 * Constructor de playlists.
		 *
		 * @param builder Builder de playlists que contiene la información establecida
		 *                para la nueva playlist.
		 */
		private Internal(Playlist.Builder builder) {
			super(builder);

			this.id = 0;
			this.isRegistered = false;
		}

		/**
		 * Método para añadir una canción a la lista en la posición deseada.
		 *
		 * @param song Canción que se desea añadir.
		 */
		public boolean addSong(Song song) {
			return super.songs.add(song);
		}

		@Override
		public boolean equals(Object obj) {

			if (obj == null) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}

			Internal playlist = (Internal) obj;

			// Librería helper de Google
			return Objects.equal(this.id, playlist.id);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getId() {
			return id;
		}

		@Override
		public int hashCode() {
			// Librería helper de Google
			return Objects.hashCode(this.id);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isRegistered() {
			return isRegistered;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void registerId(int id) {
			if (isRegistered()) {
				return;
			}

			this.id = id;
			this.isRegistered = true;
		}

		/**
		 * Método para eliminar una canción de la lista según el índice dado.
		 *
		 * @param song Canción de la lista que se desea eliminar.
		 *
		 * @return Canción eliminada de la lista.
		 */
		public boolean removeSong(Song song) {
			return super.songs.remove(song);
		}

		/**
		 * Elimina de la playlist todas las instancias de la canción dada.
		 * 
		 * @param song Canción que se desea purgar de la playlist.
		 * 
		 * @return Resultado de la operación.
		 */
		public boolean removeAll(Song song) {
			return super.songs.removeIf(s -> s.equals(song));
		}

		/**
		 * Establece el nombre de la playlist.
		 * 
		 * @param name Nuevo nombre de la playlist.
		 */
		public void setName(String name) {
			super.name = name;
		}

		/**
		 * Establece la descripción de la playlist.
		 * 
		 * @param desciption Nueva descripción de la playlist.
		 */
		public void setDescription(String desciption) {
			super.description = desciption;
		}
	}

	private String description;
	private String name;
	private final List<Song> songs;

	/**
	 * Constructor de playlists.
	 *
	 * @param builder Builder de playlists que contiene la información establecida
	 *                para la nueva playlist.
	 */
	private Playlist(Playlist.Builder builder) {
		this.name = builder.name;
		this.description = builder.description;
		this.songs = builder.songs;
	}

	/**
	 * Conversión explicita de playlist a su versión mutable. Permitirá acceder a
	 * los métodos que mutan los datos.
	 *
	 * @return Vista mutable de la playlist.
	 */
	@Override
	public Playlist.Internal asMut() {
		return (Playlist.Internal) this;
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
	 * Método para obtener el nombre de la playlist.
	 *
	 * @return Nombre de la playlist.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Método para obtener una canción de la lista según el índice dado.
	 *
	 * @param index Índice de la canción que se desea obtener.
	 *
	 * @return Canción de la lista correspondiente al índice dado.
	 */
	public Song getSong(int index) {
		return songs.get(index);
	}

	/**
	 * Método para obtener una lista no modificable de canciones de la playlist.
	 *
	 * @return Lista no modificable de las canciones de la playlist.
	 */
	public List<Song> getSongs() {
		return Collections.unmodifiableList(songs);
	}

	/**
	 * Comprueba si la playlist está vacía.
	 * 
	 * @return Estado de vacío de la playlist.
	 */
	public boolean isEmpty() {
		return songs.isEmpty();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		return this.asMut().equals(obj);
	}

	@Override
	public int hashCode() {
		return this.asMut().hashCode();
	}
}