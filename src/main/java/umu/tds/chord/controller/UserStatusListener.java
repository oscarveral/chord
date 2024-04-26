package umu.tds.chord.controller;

import java.util.EventListener;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.User;

/**
 * Interfaz listener para poder suscribirse a eventos producidos por el 
 * controlador en relación al usuario actual.
 */
public interface UserStatusListener extends EventListener{
	
	/**
	 * Método de notificación de inicio de sesión de un usuario. Se proporciona
	 * el usuario que ha iniciado sesión para permitir el acceso a toda la 
	 * información del mismo.
	 * 
	 * @param u Usuario que ha iniciado sesión si hubo exito un opcional vacío
	 * por defecto.
	 */
	public default void onLogin(Optional<User> u) {
	};

	/**
	 * Método de notificación de cierre de sesión del usuario actual.
	 */
	public default void onLogout() {
	};
	
	/**
	 * Método emitido en los registros de nuevos usuarios.
	 * 
	 * @param success Resultado del proceso de registro.
	 */
	public default void onRegister(boolean success) {
	};

	/**
	 * Método de notificaicón de cambio en el estado premium del usuario
	 * actual.
	 * 
	 * @param premium Nuevo estado premium del usuario.
	 */
	public default void onPremiumChange(boolean premium) {
	};
	
	/**
	 * Método emitido cuando la lista de favoritos del usuario ha cambiado.
	 * 
	 * @param favourites Nuevo set de canciones favoritas.
	 */
	public default void onFavouritesChange(Set<Song> favourites) {
	};
	
	/**
	 * Método emitido cuando cambia la lista de canciones recientes del usuario.
	 * 
	 * @param recent Lista actual de canciones recientes.
	 */
	public default void onRecentSongChange(List<Song> recent) {
	};
	
	/**
	 * Método emitido cuando cambia la lista de playlist del usuario.
	 * 
	 * @param playlists Lista de playlists actual del usuario.
	 */
	public default void onPlaylistListChange(List<Playlist> playlists) {
	};
	
	/**
	 * Método emitido cuando se ha seleccionado un aplaylist del usuario.
	 * 
	 * @param playlist Playlist seleccionada.
	 */
	public default void onPlaylistSelection(Optional<Playlist> playlist) {
	};
}