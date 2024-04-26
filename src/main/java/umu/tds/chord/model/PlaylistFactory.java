package umu.tds.chord.model;

import java.util.Optional;

/**
 * Factoría de creación de playlists.
 */
public final class PlaylistFactory {
	
	/**
	 * Método de creación de playlists. Realiza validación de los datos.
	 * 
	 * @param name Nombre de la playlist.
	 * @param description Descripción de la playlist.
	 * 
	 * @return Opcional con la Playlist creada o un opcional vacío si name o 
	 * description son nulos, vacíos o están en blanco.
	 */
	public static Optional<Playlist> createPlaylist(String name, String description) {
		
		if (name == null || name.isBlank() || name.isEmpty() || description == null || description.isBlank()
				|| description.isEmpty())
			return Optional.empty();
		
		return new Playlist.Builder(name).description(description).build();
	}

}
