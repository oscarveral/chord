package umu.tds.chord.controller;

import umu.tds.chord.model.User;

/**
 * Interfaz listener para poder suscribirse a eventos producidos por el 
 * controlador en relación al usuario actual.
 */
public interface UserStatusListener {
	
	/**
	 * Método de notificación de inicio de sesión de un usuario. Se proporciona
	 * el usuario que ha iniciado sesión para permitir el acceso a toda la 
	 * información del mismo.
	 * 
	 * @param u Usuario que ha iniciado sesión.
	 */
	public default void onLogin(User u) {
	};

	/**
	 * Método de notificación de cierre de sesión del usuario actual.
	 */
	public default void onLogout() {
	};

	/**
	 * Método de notificaicón de cambio en el estado premium del usuario
	 * actual.
	 * 
	 * @param premium Nuevo estado premium del usuario.
	 */
	public default void onPremiumChange(boolean premium) {
	};
}