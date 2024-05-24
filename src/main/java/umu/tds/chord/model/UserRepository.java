package umu.tds.chord.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import umu.tds.chord.dao.DAOFactory;

/**
 * Repositorio de usuarios. Utilizado para la creación, obtención y
 * actualización de los datos de los usuarios. Véase {@link User}.
 */
public enum UserRepository {

	INSTANCE;

	private final Map<String, User> users;

	private UserRepository() {
		users = new HashMap<>();

		DAOFactory.getInstance().getUserDAO().recoverAll().forEach(u -> users.put(u.getUserName(), u));
	}

	/**
	 * Añade un usuario al repositorio.
	 *
	 * @param username Nombre de usuario.
	 * @param password Contraseña deseada en texto plano.
	 * @param birthday Cumpleaños del usuario.
	 *
	 * @return {@code false} Si el usuario ya estaba registrado o username es
	 *         {@code null}. {@code true} en otro caso.
	 */
	public boolean addUser(String username, String password, Date birthday) {

		// El usuario no debe estar registrado. Validación de datos.
		if (username == null || username.isBlank() || username.isEmpty() || password.isEmpty()
				|| users.containsKey(username)) {
			return false;
		}

		Optional<User> optional = new User.Builder(username).password(password).birthday(birthday).build();		
		if (optional.isEmpty()) return false;
		
		// Persistencia.
		User user = optional.get();
		boolean result = DAOFactory.getInstance().getUserDAO().register(user.asMut());
		// Registro en persistencia.
		if (result) {
			users.put(user.getUserName(), user);
		}
		return result;
	}

	/**
	 * Obtiene el usuario especificado. Véase {@link User}.
	 *
	 * @param username Nombre del usuario especificado.
	 * @param password Contraseña del usuario especificado.
	 *
	 * @return Devuelve el usuario si username y password son no nulas, el usuario
	 *         estaba registrado y la contraseña coincide con la suya.
	 */
	public Optional<User> getUser(String username, String password) {

		// El usuario debe estar registrado.
		// La contraseña debe coincidir.
		if (username == null || !users.containsKey(username) || password == null
				|| !users.get(username).checkPassword(password)) {
			return Optional.empty();
		}

		// Éxito
		return Optional.of(users.get(username));
	}

	/**
	 * Método para obtener la lista completa de usuarios.
	 *
	 * @return Lista no modificable de usuarios.
	 */
	protected Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	/**
	 * Elimina el usuario y su información asociada.
	 *
	 * @param u Usuario que se desea eliminar.
	 * @return {@code true} si el usuario existía en el repositorio y pudo ser
	 *         eliminado.
	 */
	public boolean removeUser(User u) {

		// Comprobar si este usuario está registrado.
		// Doble comprobacion. La instancia de usuario asociada es la misma.
		if (u == null || !users.containsKey(u.getUserName()) || !users.get(u.getUserName()).equals(u)) {
			return false;
		}

		// Eliminación de persistencia.
		boolean persistence = DAOFactory.getInstance().getUserDAO().delete(u.asMut());

		// Si hubo exito en la eliminación de persistencia se quita del mapa.
		if (persistence) {
			users.remove(u.getUserName());
		}

		return persistence;
	}

	/**
	 * Actualiza los datos del usuario proporcionado, escribiendolos mediante el
	 * servicio de persistencia.
	 *
	 * @param u Usuario del que se desea actualizar la información. Se espera que u
	 *          haya sido obtenido mediante
	 *          {@link UserRepository#getUser(String, String)}. Véase {@link User}.
	 *
	 * @return {@code false} Si el usuario es {@code null}, no estaba registrado o
	 *         existe alguna inconsitencia entre el mapeo del nombre del usuario
	 *         proporcionado y el objeto usuario proprocionado. {@code true} en otro
	 *         caso.
	 */
	public boolean updateUser(User u) {

		// Comprobar si este usuario está registrado.
		// Doble comprobacion. La instancia de usuario asociada es la misma.
		if (u == null || !users.containsKey(u.getUserName()) || !users.get(u.getUserName()).equals(u)) {
			return false;
		}

		// Dado que la obtención de usuarios devuelve referencias.
		// Se espera que la actualización de datos las reciba, por tanto, dicha
		// referencia se encuentra en el mapa de usuarios y ya está actualizada.
		// Solo hace falta actualizar la información del usuario en
		// persistencia.

		// Actualización en persistencia.
		boolean persistence = DAOFactory.getInstance().getUserDAO().modify(u.asMut());

		// Fallo de actualización en persistencia da lugar a eliminación del
		// usuario del mapa en memoria
		if (!persistence) {
			users.remove(u.getUserName());
		}

		return persistence;
	}
}
