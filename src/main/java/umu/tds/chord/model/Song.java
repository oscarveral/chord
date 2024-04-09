package umu.tds.chord.model;

/**
 * Clase de objetos inmutables que representa a las canciones dentro de nuestra 
 * aplicación. Mantienen almacenada la ruta al fichero con dicha canción para 
 * que pueda ser localizada y reproducida.
 */
public final class Song {

	private final String name;
	private final String author;
	private final String path;
	private final Style style;
	
	/**
	 * Constructor de canciones inmutables.
	 * 
	 * @param name Nombre de la canción.
	 * @param author Autor de la canción.
	 * @param path Ruta donde se encuentra el fichero de la canción.
	 * @param style Estilo musical de la canción. Véase {@link Style}.
	 */
	public Song(String name, String author, String path, Style style) {
		this.name = name;
		this.author = author;
		this.path = path;
		this.style = style;
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
}
