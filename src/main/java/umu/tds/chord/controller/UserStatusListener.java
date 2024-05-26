package umu.tds.chord.controller;

import java.util.EventListener;

/**
 * Interfaz listener para poder suscribirse a eventos producidos por el
 * controlador en relación al usuario actual.
 */
public interface UserStatusListener extends EventListener {

	/**
	 * Evento emitido al iniciar sesión el usuario.
	 * 
	 * @param e Evento de estado de usuario.
	 */
	public default void onUserLogin(UserStatusEvent e) {}
	
	/**
	 * Evento emitido cuando se produce un cambio de los datos principales
	 * asociados al usuario.
	 * 
	 * @param e Evento de estado de usuario.
	 */
	public default void onUserMetadataChange(UserStatusEvent e) {}
	
	/**
	 * Evento emitido cuando se ha producido un cambio en la lista de playlists
	 * del usuario.
	 * 
	 * @param e Evento de estado del usuario.
	 */
	public default void onPlaylistsListUpdate(UserStatusEvent e) {}
	
}