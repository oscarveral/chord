package umu.tds.chord.model;

import java.util.Optional;

import umu.tds.chord.dao.Persistent;

/**
 * Clase que representa a las canciones dentro de la aplicación. Mantienen 
 * almacenada la ruta al fichero con dicha canción para que pueda ser 
 * localizada y reproducida.
 */
public sealed abstract class Song permits Song.Internal {

	private final String name;
	private final String author;
	private final String path;
	private final Style style;
	
	/**
	 * Constructor de canciones.
	 */
	public final static class Builder {
		
		private String name;
		private String author;
		private String path;
		private Style style;
	
		/**
		 * Crea un nuevo builder de canciones estableciendo el nombre de la
		 * canción. Se deberá establecer obligatoriamente autor, path y estilo.
		 * 
		 * @param name Nombre de la canción.
		 */
		public Builder(String name) {
			this.name = name;
			this.author = null;
			this.path = null;
			this.style = null;
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
		 * Establece el estilo de la canción. Véase {@link Style}.
		 * 
		 * @param style Estilo de la canción.
		 * 
		 * @return Instancia actual del builder.
		 */
		public Builder style(Style style) {
			this.style = style;
			return this;
		}
		
		private boolean validate() {
			// Forzar a establecer los 3 parámtros.
			if (this.author == null)
				return false;
			if (this.path == null)
				return false;
			if (this.style == null)
				return false;
			
			return true;
		}
		
		/**
		 * Construye una nueva canción a partir de los datos actuales.
		 * 
		 * @return Un opcional vacío si no se ha proporcionado autor, path o
		 * estilo. Un opcional con la canción creada en otro caso.
		 */
		public Optional<Song> build() {
			if (!validate())
				return Optional.empty();
			
			return Optional.of(new Song.Internal(this));
		}
	}
	
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
	 * Método para obtener el autor de la canción.
	 * 
	 * @return Autor de la canción.
	 */
	public String getAuthor() {
		return author;
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
	 * @return Estilo musical de la canción. Véase {@link Style}.
	 */
	public Style getStyle() {
		return style;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (this.getClass() != obj.getClass())
			return false;
		
		Song song = (Song) obj;
		
		if (this.name != song.name)
			return false;
		if (this.author != song.author)
			return false;
		if (this.path != song.path)
			return false;
		if (this.style != song.style)
			return false;
		
		return true;
	}
	
	/**
	 * Conversión explicita de canción a su versión mutable. Permitirá acceder
	 * a los métodos que mutan los datos.
	 * 
	 * @return Vista mutable de la canción.
	 */
	public Song.Internal asMut() {
		return (Song.Internal) this;
	}
	
	/**
	 * Clase de representación interna de una canción. Expone métodos que 
	 * permiten mutar el estado de la canción. Se exponen también los métodos
	 * necesarios para la persistencia {@link Persistent}.
	 */
	public final static class Internal extends Song implements Persistent {
		
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
			if (isRegistered())
				return;
			
			this.id = id;
			this.isRegistered = true;
		}
	}
}
