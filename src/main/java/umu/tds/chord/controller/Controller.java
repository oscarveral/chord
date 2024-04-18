package umu.tds.chord.controller;

import java.util.Optional;

import umu.tds.chord.model.User;
import umu.tds.chord.model.UserRepository;

/**
 * Controlador para la lógica de negocio. Expone la API que utilizará la 
 * interfaz de la aplicación para interactuar con los elementos del modelo de
 * negocio. Dado que no se permite que la interfaz modifique el estado de dichos
 * elementos directamente, los métodos que lo pertiten se exponen a través 
 * de este controlador.
 */
public enum Controller {

	INSTANCE;
	
	private Optional<User> current;

	private Controller() {
		current = Optional.empty();
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
		
		// Recuperación del usuario.
		current = UserRepository.INSTANCE.getUser(username, password);
		
		// Retornar el resultado de la recuperación.
		return current.isPresent();
	}
	
	/**
	 * Cierra la sesión del usuario actual.
	 */
	public void logout() {
		// Si hay un usuario actual, actualizar su información.
		current.ifPresent(u ->
			UserRepository.INSTANCE.updateUser(u)
		);	
		current = Optional.empty();
	}
	
	/**
	 * Obtiene el usuario actual de la aplicación.
	 * 
	 * @return Opcional con el usuario actual o un opcional vacío si no se había
	 * iniciado sesión con ningún usuario.
	 */
	public Optional<User> getUser() {
		return current;
	}
}
