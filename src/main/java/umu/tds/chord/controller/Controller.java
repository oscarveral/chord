package umu.tds.chord.controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.component.BuscadorCanciones;
import umu.tds.chord.component.Canciones;
import umu.tds.chord.component.CargadorCanciones;
import umu.tds.chord.model.SongRepository;
import umu.tds.chord.model.User;
import umu.tds.chord.model.UserRepository;

/**
 * Controlador para la lógica de negocio. Expone la API que utilizará la
 * interfaz de la aplicación para interactuar con los elementos del modelo de
 * negocio. Dado que no se permite que la interfaz modifique el estado de dichos
 * elementos directamente, los métodos que lo pertiten se exponen a través de
 * este controlador.
 *
 * Se utilizarán Listeners para que la interfaz pueda ser notificada de cambios
 * en el estado del los elementos del modelo producidos por llamadas a métodos
 * en este controlador.
 */
public enum Controller {

	INSTANCE;

	private BuscadorCanciones buscadorCanciones;
	
	private Optional<User> currentUser;

	private Set<SongStatusListener> songStatusListeners;
	private Set<UserStatusListener> userStatusListeners;

	private Controller() {
		registerCancionesListener();

		currentUser = Optional.empty();
		
		userStatusListeners = new HashSet<>();
		songStatusListeners = new HashSet<>();
	}
	
	// ---------- Métodos del controlador. ----------
	
	/**
	 * Método para registrar un nuevo usuario en la aplicación.
	 * 
	 * @param username Nombre del nuevo usuario.
	 * @param password Contraseña del nuevo usuario.
	 * @param birthday Cumpleaños del nuevo usuario.
	 * 
	 * @return {@code true} si hubo éxito en el registro del usuario. {@code 
	 * false} en cualquier otro caso.
	 */
	public boolean register(String username, String password, LocalDate birthday) {
		return UserRepository.INSTANCE.addUser(username, password, birthday);
	}
	
	/**
	 * Método para iniciar sesión en el controlador como un determinado usuario.
	 * 
	 * @param username Nombre de usuario.
	 * @param password Contraseña del usuario.
	 * 
	 * @return {@code true} si no había un usuario con sesion iniciada y hubo 
	 * éxito iniciando sesión con el usuario especificado. {@code false} si ya 
	 * había un usuario con sesión iniciada o en cualquier otro caso de error.
	 */
	public boolean login(String username, String password) {
		if (currentUser.isEmpty()) {
			currentUser = UserRepository.INSTANCE.getUser(username, password);
			return currentUser.isPresent();
		}
		return false;
	}
	
	/**
	 * Método para cerrar la sesión del usuario actual del controlador, 
	 * actualizando toda su información en persistencia.
	 * 
	 * @return {@code true} si no había usuario con sesión iniciada o se pudo 
	 * cerrar la sesión de forma satisfactoria. {@code false} si hubo algún 
	 * error cerrando la sesión.
	 * 
	 * @implNote Un fallo de cierre de sesión abortará todo el proceso, 
	 * manteniendo la sesión iniciada.
	 */
	public boolean logout() {
		currentUser.ifPresent(u -> {
			boolean updated = UserRepository.INSTANCE.updateUser(u);
			if (updated) currentUser = Optional.empty();
		});
		return currentUser.isEmpty();
	}
	
	/**
	 * Método para eliminar la cuenta del usuario con la sesión iniciada 
	 * actualmente. 
	 * 
	 * @return {@code true} si no había usuario con sesión iniciada o se pudo
	 * cerrar la sesión y eliminar el usuario de forma satisfactoria. {@code 
	 * false} si hubo algún error durante la eliminación.
	 * 
	 * @implNote Un fallo de eliminación abortará todo el proceso, manteniendo
	 * la sesión iniciada.
	 */
	public boolean remove() {
		currentUser.ifPresent(u -> {
			boolean removed = UserRepository.INSTANCE.removeUser(u);
			if (removed) currentUser = Optional.empty();
		});
		return currentUser.isEmpty();
	}

	// ---------- Cargador de canciones. ----------

	/**
	 * Método para iniciar la carga de canciones a partir de un fichero.
	 *
	 * @param fichero Fichero que describe las canciones que cargar.
	 */
	public void cargarCanciones(String fichero) {
		buscadorCanciones.setArchivoCanciones(fichero);
	}

	/**
	 * Función de procesamiento de canciones. Convierte un objeto {@link Canciones}
	 * en las canciones del modelo para luego añadirlas al repositorio de canciones
	 * {@link SongRepository}
	 *
	 * @param c Opcional con el objeto {@link Canciones}.
	 */
	private void processSongData(Optional<Canciones> c) {
		// No hacer nada en fallo.
		if (c.isEmpty()) {
			return;
		}

		// Añadir las canciones al repositorio.
		Canciones canciones = c.get();
		canciones.getCancion().forEach(s -> {
			String name = s.getTitulo();
			String author = s.getInterprete();
			String url = s.getURL();
			String style = s.getEstilo();
			SongRepository.INSTANCE.addSong(name, author, url, style);
		});
	}

	/**
	 * Establecer el buscador de canciones utilizado y añadir listener para el
	 * permitir el procesamiento de las nuevas canciones para añadirlas al
	 * repositorio.
	 */
	private void registerCancionesListener() {
		buscadorCanciones = CargadorCanciones.INSTANCE;
		buscadorCanciones.addCancionesListener(e -> processSongData(e.getCanciones()));
	}
	
	// ---------- Listeners. ----------

	/**
	 * Registra un listener de estado de canciones en el controlador.
	 *
	 * @param l Listener que se desea registrar.
	 */
	public void registerSongStatusListener(SongStatusListener l) {
		songStatusListeners.add(l);
	}

	/**
	 * Registra un listener de estado de usuario en el controlador.
	 *
	 * @param l Listener que se desea registrar.
	 */
	public void registerUserStatusListener(UserStatusListener l) {
		userStatusListeners.add(l);
	}

	/**
	 * Elimina un listener de estado de usuario del controlador.
	 *
	 * @param l Listener que se desea eliminar.
	 */
	public void removeSongStatusListener(SongStatusListener l) {
		songStatusListeners.remove(l);
	}

	/**
	 * Elimina un listener de estado de usuario del controlador.
	 *
	 * @param l Listener que se desea eliminar.
	 */
	public void removeUserStatusListener(UserStatusListener l) {
		userStatusListeners.remove(l);
	}
	
	// ---------- Depuración. ----------
	
	/**
	 * @apiNote ALERTA. Método de depuración.
	 * 
	 * Fuerza un reseteo del estado del controlador, vaciando las listas con los
	 * listeners de eventos y quitando el usuario actual de forma forzada
	 * actualizando sus datos en el caso de que hubiese cambios.
	 */
	public void clearControllerState() {
		currentUser.ifPresent(u -> {
			UserRepository.INSTANCE.updateUser(u);
			currentUser = Optional.empty();
		});
		songStatusListeners.clear();
		userStatusListeners.clear();
	}
}
