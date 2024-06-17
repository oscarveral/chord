package umu.tds.chord.model;

import java.util.Optional;

/**
 * Factoría de creación de playlists.
 */
public final class PlaylistFactory {

	/**
	 * Método de creación de playlists. Realiza validación de los datos.
	 *
	 * @param name        Nombre de la playlist.
	 * @param description Descripción de la playlist.
	 *
	 * @return Opcional con la Playlist creada o un opcional vacío si name o
	 *         description son nulos, vacíos o están en blanco.
	 */
	public static Optional<Playlist> createPlaylist(String name, String description) {

		if (name == null || name.isBlank() || name.isEmpty() || description == null || description.isBlank()
				|| description.isEmpty()) {
			return Optional.empty();
		}

		return new Playlist.Builder(name).description(description).build();
	}

	/**
	 * Actualiza una playlist validando los datos proporcionados.
	 * 
	 * @param p    Playlist que se desea actualizar.
	 * 
	 * @param name Nuevo nombre de la playlist.
	 * @param desc Nueva descripción de la playlist.
	 * 
	 * @return Resultado de la operación.
	 */
	public static boolean updatePlaylist(Playlist p, String name, String desc) {
		if (p == null || name == null || desc == null || name.isBlank() || name.isEmpty() || desc.isBlank()
				|| desc.isEmpty())
			return false;

		p.asMut().setName(name);
		p.asMut().setDescription(desc);

		return true;
	}

	/**
	 * Crea un clon de la playlist dada.
	 * 
	 * @param p Playlisy que se desea clonar.
	 * 
	 * @return Clon de la playlist.
	 * 
	 * @implNote El clon retornado no está registrado en persistencia.
	 */
	public static Optional<Playlist> clonePlaylist(Playlist p) {
		return Optional.ofNullable(
				new Playlist.Builder(p.getName()).description(p.getDescription()).songs(p.getSongs()).build().get());
	}

}
