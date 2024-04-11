package umu.tds.chord.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio de usuarios. Utilizado para la creación, obtención y 
 * actualización de los datos de los usuarios.
 */
public enum UserRepository {

	INSTANCE;
	
	private final Map<String, User> users;
	
	private UserRepository() {
		users = new HashMap<String, User>();
		/*
		DAOFactory.getInstance()
			.getUserDAO()
			.recoverAll()
			.forEach(u -> usuarios.put(u.getUserName(), u));
		*/
	}
	
	/**
	 * Añade un usuario al repositorio.
	 * 
	 * @param username Nombre de usuario.
	 * @param password Contraseña deseada en texto plano.
	 * @param birthday Cumpleaños del usuario.
	 * 
	 * @return {@code false} Si el usuario ya estaba registrado o username es 
	 * {@code null}. {@code true} en otro caso.
	 */
	public boolean addUser(String username, String password, LocalDate birthday) {
		
		// El usuario no debe estar registrado.
		if (username == null || users.containsKey(username)) return false;
				
		User user = new User.Builder(username)
					.password(password)
					.birthday(birthday)
					.build()
					.get();
		
		/*
		// Persistencia.
		boolean persistence = DAOFactory.getInstance()
								.getUserDAO()
								.register(user);
				
		// Fallo de registro en persistencia.
		if (!persistence) return false;
		*/
		
		// Registrar al usuario en memoria.
		users.put(user.getUserName(), user);
			
		return true;
	}
	
	/**
	 * Obtiene el usuario especificado.
	 * 
	 * @param username Nombre del usuario especificado.
	 * @param password Contraseña del usuario especificado.
	 * 
	 * @return Devuelve el usuario si username y password son no nulas, el 
	 * usuario estaba registrado y la contraseña coincide con la suya.
	 */
	public Optional<User> getUser(String username, String password) {
		
		// El usuario debe estar registrado.
		if (username == null || !users.containsKey(username)) 
			return Optional.empty();
		
		// La contraseña debe coincidir.
		if (password == null || !users.get(username).checkPassword(password)) 
			return Optional.empty();
				
		// Éxito
		return Optional.of(users.get(username));
	}
	
	/**
	 * Actualiza los datos del usuario proporcionado, escribiendolos mediante
	 * el servicio de persistencia.
	 * 
	 * @param u Usuario del que se desea actualizar la información. Se espera 
	 * que u haya sido obtenido mediante
	 * {@link UserRepository#getUser(String, String)}.
	 * 
	 * @return {@code false} Si el usuario es {@code null}, no estaba 
	 * registrado o existe alguna inconsitencia entre el mapeo del nombre del 
	 * usuario proporcionado y el objeto usuario proprocionado. {@code true} 
	 * en otro caso.
	 */
	public boolean updateUser(User u) {
		
		// Comprobar si este usuario está registrado.
		if (u == null || !users.containsKey(u.getUserName())) return false;
		// Doble comprobacion. La instancia de usuario asociada es la misma.
		if (!users.get(u.getUserName()).equals(u)) return false;
		
		// Dado que la obtención de usuarios devuelve referencias. 
		// Se espera que la actualización de datos las reciba, por tanto, dicha
		// referencia se encuentra en el mapa de usuarios y ya está actualizada.
		// Solo hace falta actualizar la información del usuario en 
		// persistencia.
	
		/*
		// Actualización en persistencia.
		boolean persistence = DAOFactory.getInstance()
								.getUserDAO()
								.modify(u);
				
		// Fallo de actualización en persistencia da lugar a eliminación del 
		// usuario del mapa en memoria
		if (!persistence) {
			usuarios.remove(u.getUserName());
		}
		
		return persistence;
		*/
		return true;
	}
}
