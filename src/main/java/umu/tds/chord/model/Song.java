package umu.tds.chord.model;

import java.util.Optional;

import com.google.common.base.Objects;

/**
 * Clase que representa a las canciones dentro de la aplicación. Mantienen
 * almacenada la ruta al fichero con dicha canción para que pueda ser localizada
 * y reproducida.
 */
public abstract sealed class Song implements Mutable<Song.Internal> {

	/**
	 * Constructor de canciones.
	 */
	public static final class Builder {

		private String author;
		private String name;
		private String path;
		private String style;
		private int reproducciones;

		/**
		 * Crea un nuevo builder de canciones estableciendo el nombre de la canción. Se
		 * deberá establecer obligatoriamente autor, path y estilo.
		 *
		 * @param name Nombre de la canción.
		 */
		public Builder(String name) {
			this.name = name;
			this.author = null;
			this.path = null;
			this.style = null;
			this.reproducciones = 0;
		}

		/**
		 * Establece el autor de la canción.
		 *
		 * @param author Autor de la canción.
		 *
		 * @return Intancia actual del builder.
		 */
		public Builder author(String author) {
			this.author = author;
			return this;
		}

		/**
		 * Construye una nueva canción a partir de los datos actuales.
		 *
		 * @return Un opcional vacío si no se ha proporcionado autor, path o estilo. Un
		 *         opcional con la canción creada en otro caso.
		 */
		public Optional<Song> build() {
			if (!validate()) {
				return Optional.empty();
			}

			return Optional.of(new Song.Internal(this));
		}

		/**
		 * Establece la ruta al fichero de la canción.
		 *
		 * @param path Ruta al fichero de la canción.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder path(String path) {
			this.path = path;
			return this;
		}

		/**
		 * Establece el estilo de la canción.
		 *
		 * @param style Estilo de la canción.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder style(String style) {
			this.style = style;
			return this;
		}
		
		/**
		 * Establece las reproducciones de la canción.
		 * 
		 * @param reproducciones Cantidad de reproducciones.
		 * 
		 * @return Instancia actual del builder.
		 */
		public Builder reproducciones(int reproducciones) {
			this.reproducciones = reproducciones;
			return this;
		}

		private boolean validate() {
			// Forzar a establecer los 3 parámtros.
			return (this.author != null && this.path != null && this.style != null);
		}
	}

	/**
	 * Clase de representación interna de una canción. Expone métodos que permiten
	 * mutar el estado de la canción. Se exponen también los métodos necesarios para
	 * la persistencia {@link Persistent}.
	 */
	public static final class Internal extends Song implements Persistent {

		private int id;
		private boolean isRegistered;

		/**
		 * Constructor de canciones inmutables.
		 *
		 * @param builder Constructor de canciones con los datos de la canción.
		 */
		private Internal(Song.Builder builder) {
			super(builder);

			this.id = 0;
			this.isRegistered = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getId() {
			return id;
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
		 * Incrementa en 1 la cantidad de reproducciones de la canción.
		 */
		public void addReproduccion() {
			super.reproducciones += 1;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	private final String author;
	private final String name;
	private final String path;
	private final String style;
	private int reproducciones;

	/**
	 * Constructor de canciones inmutables.
	 *
	 * @param builder Constructor de canciones con los datos de la canción.
	 */
	private Song(Song.Builder builder) {
		this.name = builder.name;
		this.author = builder.author;
		this.path = builder.path;
		this.style = builder.style;
		this.reproducciones = builder.reproducciones;
	}

	/**
	 * Conversión explicita de canción a su versión mutable. Permitirá acceder a los
	 * métodos que mutan los datos.
	 *
	 * @return Vista mutable de la canción.
	 */
	@Override
	public Song.Internal asMut() {
		return (Song.Internal) this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		Song song = (Song) obj;

		// Uso del helper de Google.
		return Objects.equal(this.name, song.name) && Objects.equal(this.author, song.author)
				&& Objects.equal(this.path, song.path) && Objects.equal(this.style, song.style);
	}

	/**
	 * Método para obtener el autor de la canción.
	 *
	 * @return Autor de la canción.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Método para obtener el nombre de la canción.
	 *
	 * @return Nombre de la canción.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Método para obtener la ruta del fichero de la canción.
	 *
	 * @return Ruta donde se encuentra el fichero de la canción.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Método para obtener el estilo musical de la canción.
	 *
	 * @return Estilo musical de la canción.
	 */
	public String getStyle() {
		return style;
	}

	@Override
	public int hashCode() {
		// Helper de la libreria de Google.
		return Objects.hashCode(this.name, this.author, this.path, this.style);
	}
	
	/**
	 * Obtiene la cantidad de reproducciones de la canción.
	 * 
	 * @return Reproducciones de la canción.
	 */
	public int getReproducciones() {
		return reproducciones;
	}
}
