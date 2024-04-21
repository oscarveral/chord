package umu.tds.chord.controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.model.User;
import umu.tds.chord.model.UserRepository;

/**
 * Controlador para la lógica de negocio. Expone la API que utilizará la 
 * interfaz de la aplicación para interactuar con los elementos del modelo de
 * negocio. Dado que no se permite que la interfaz modifique el estado de dichos
 * elementos directamente, los métodos que lo pertiten se exponen a través 
 * de este controlador.
 * 
 * Se utilizarán Listeners para que la interfaz pueda ser notificada de cambios
 * en el estado del los elementos del modelo producidos por llamadas a métodos
 * en este controlador.
 */
public enum Controller {

	INSTANCE;
	
	private Optional<User> current;
	private Set<UserStatusListener> listeners;
	
	private Controller() {
		current = Optional.empty();
		listeners = new HashSet<UserStatusListener>();
	}
	
	/**
	 * Función para el registro de nuevos usuarios.
	 * 
	 * @param username Nombre de usuario del nuevo usuario.
	 * @param pass Contraseña del nuevo usuario.
	 * @param birthday Cumpleaños del nuevo usuario.
	 * 
	 * @return {@code false} si existe ya un usuario registrado con el nombre
	 * de usuario proporcionado.
	 */
	public boolean register(String username, String pass, LocalDate birthday) {
		return UserRepository.INSTANCE.addUser(username, pass, birthday);
	}
	
	/**
	 * Método para el inicio de sesión.
	 * 
	 * @param username Nombre de usuario.
	 * @param password Contraseña en claro del usuario.
	 * 
	 * @return {@code true} si se ha iniciado sesión de forma exitosa con los 
	 * datos proporcionados. {@code false} en cualquier otro caso.
	 */
	public boolean login(String username, String password) {
		// No se puede hacer login si ya se está logeado.
		if (current.isPresent())
			return false;
		
		// Recuperación del usuario y notificación.
		current = UserRepository.INSTANCE.getUser(username, password);
		current.ifPresent(u -> {
			listeners.forEach(l -> l.onLogin(u));
		});
		
		// Retornar el resultado de la recuperación.
		return current.isPresent();
	}
	
	/**
	 * Cierra la sesión del usuario actual.
	 */
	public void logout() {
		// Si hay un usuario actual, actualizar su información.
		current.ifPresent(u -> {
			UserRepository.INSTANCE.updateUser(u);
			listeners.forEach(l -> l.onLogout());
		});
		current = Optional.empty();
	}
	
	/**
	 * Cierra la sesión del usuario actual y elimina su cuenta.
	 * 
	 * @return {@code true} si se eliminó la cuenta del usuario de forma 
	 * exitosa.
	 */
	public boolean remove() {
		// Si no hay usuario actual retornamos.
		if(!current.isPresent()) return false;
		// Obtenemos el usuario actual y forzamos un cierre de sesión.
		User u = current.get();
		logout();
		// Se elimina la cuenta del repositorio.
		return UserRepository.INSTANCE.removeUser(u);
	}
	
	/**
	 * Invierte el estado premium del usuario actual si lo hay.
	 */
	public void togglePremium() {
		// Invertir el estado premium si hay usuario actual.
		current.ifPresent(u -> {
			boolean premium = !u.isPremium();
			u.asMut().setPremium(premium);
			// Informar a los listeners del cambio.
			listeners.forEach(l -> l.onPremiumChange(premium));
		});
	}
	
	/**
	 * Registra un listener de estado de usuario en el controlador.
	 * 
	 * @param l Listener que se desea registrar.
	 */
	public void registerUserStatusListener(UserStatusListener l) {
		listeners.add(l);
	}
	
	/**
	 * Elimina un listener de estado de usuario del controlador.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removeUserStatusListener(UserStatusListener l) {
		listeners.remove(l);
	}
}
