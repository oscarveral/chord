package umu.tds.chord.component;

/**
 * Interfaz que deben implementar las clases que ofrecerán la funcionalidad de
 * carga de canciones.
 */
public interface BuscadorCanciones {

	/**
	 * Método que añade un listener para escuchar los eventos de carga de canciones.
	 *
	 * @param l Listener que se desea añadir.
	 */
	public void addCancionesListener(CancionesListener l);

	/**
	 * Método que elimina un listener de escucha de los eventos de carga de
	 * canciones.
	 *
	 * @param l Listener que se desea eliminar.
	 */
	public void removeCancionesListener(CancionesListener l);

	/**
	 * Método que produce una carga de nuevas canciones a partir de un fichero.
	 *
	 * @param fichero Fechero que describe las canciones que se deben cargar en
	 *                memoria.
	 */
	public void setArchivoCanciones(String fichero);
}
