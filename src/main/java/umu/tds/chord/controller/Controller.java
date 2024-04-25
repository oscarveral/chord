package umu.tds.chord.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.component.BuscadorCanciones;
import umu.tds.chord.component.Canciones;
import umu.tds.chord.component.CancionesEvent;
import umu.tds.chord.component.CancionesListener;
import umu.tds.chord.component.CargadorCanciones;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.SongRepository;
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
	
	private static final String adminUser = "admin";
	
	private Optional<User> currentUser;
	private List<Song> selectedSongs;
	
	private Set<UserStatusListener> userStatusListeners;
	private Set<SongStatusListener> songStatusListeners;
	
	private BuscadorCanciones buscadorCanciones;
	
	private Controller() {
		currentUser = Optional.empty();
		selectedSongs = new ArrayList<Song>();
		
		userStatusListeners = new HashSet<UserStatusListener>();
		songStatusListeners = new HashSet<SongStatusListener>();
		
		// Establecer el buscador utilizado y añadir listener
		// para el procesamineto.
		buscadorCanciones = CargadorCanciones.INSTANCE;
		buscadorCanciones.addCancionesListener(new CancionesListener() {
			
			@Override
			public void nuevasCanciones(CancionesEvent e) {
				processSongData(e.getCanciones());
			}
		});
	}
	
	private void processSongData(Optional<Canciones> c) {
		// Notificar de fallo si lo hubo.
		if(!c.isPresent()) {
			songStatusListeners.forEach(l -> l.onSongLoadFailure());
			return;
		}
		// Añadir las canciones al repositorio.
		Canciones canciones = c.get();	
		canciones.getCancion().forEach(s -> {
			String name = s.getTitulo();
			String author = s.getInterprete();
			String url = s.getURL();
			String style = s.getEstilo();
			SongRepository.INSTANCE
				.addSong(name, author, url, style);
		});
		// Reenviar los datos de las canciones.
		sendSongData();
	}
	
	private void sendSongData() {
		// Función que envía información de las canciones a los 
		// listeners interesados.
		songStatusListeners.forEach(l -> {
			l.onSongList(
				SongRepository.INSTANCE.getSongs()
			);
			l.onStyleList(
				SongRepository.INSTANCE.getStyles(),
				SongRepository.INSTANCE.getStyleWildcard()
			);
			l.onSongSelection(
				Collections.unmodifiableList(selectedSongs)
			);
		});
	}
	
	/**
	 * Función utilizada por la interfaz para indicar al controlador que está
	 * lista para funcionar y cargar datos.
	 */
	public void ready() {
		sendSongData();
	}
	
	/**
	 * Función para el registro de nuevos usuarios.
	 * 
	 * @param username Nombre de usuario del nuevo usuario.
	 * @param pass Contraseña del nuevo usuario.
	 * @param birthday Cumpleaños del nuevo usuario.
	 * 
	 */
	public void register(String username, String pass, LocalDate birthday) {
		boolean res = UserRepository.INSTANCE.addUser(username, pass, birthday);
		userStatusListeners.forEach(l -> l.onRegister(res));
	}
	
	/**
	 * Método para el inicio de sesión.
	 * 
	 * @param username Nombre de usuario.
	 * @param password Contraseña en claro del usuario.
	 * 
	 */
	public void login(String username, String password) {
		// No se puede hacer login si ya se está logeado.
		if (currentUser.isPresent())
			userStatusListeners.forEach(l -> l.onLogin(Optional.empty()));
		
		// Recuperación del usuario y notificación.
		currentUser = UserRepository.INSTANCE.getUser(username, password);
		
		// Notificación de resultados
		if (currentUser.isPresent())
			userStatusListeners.forEach(l -> l.onLogin(currentUser));
		else if (currentUser.isEmpty())
			userStatusListeners.forEach(l -> l.onLogin(Optional.empty()));
	}
	
	/**
	 * Cierra la sesión del usuario actual.
	 */
	public void logout() {
		// Si hay un usuario actual, actualizar su información.
		currentUser.ifPresent(u -> {
			UserRepository.INSTANCE.updateUser(u);
			userStatusListeners.forEach(l -> l.onLogout());
		});
		currentUser = Optional.empty();
	}
	
	/**
	 * Cierra la sesión del usuario actual y elimina su cuenta.
	 * 
	 * @return {@code true} si se eliminó la cuenta del usuario de forma 
	 * exitosa.
	 */
	public boolean remove() {
		// Si no hay usuario actual retornamos.
		if(!currentUser.isPresent()) return false;
		// Obtenemos el usuario actual y forzamos un cierre de sesión.
		User u = currentUser.get();
		logout();
		// Se elimina la cuenta del repositorio.
		return UserRepository.INSTANCE.removeUser(u);
	}
	
	/**
	 * Invierte el estado premium del usuario actual si lo hay.
	 */
	public void togglePremium() {
		// Invertir el estado premium si hay usuario actual.
		currentUser.ifPresent(u -> {
			boolean premium = !u.isPremium();
			u.asMut().setPremium(premium);
			// Informar a los listeners del cambio.
			userStatusListeners.forEach(l -> l.onPremiumChange(premium));
		});
	}
	
	/**
	 * Cambia el estado de favorito de la canción dada.
	 * 
	 * @param s Canción para la que se desea cambiar el estado de favorito.
	 */
	public void toggleFavourite(Song s) {
		currentUser.ifPresent(u -> {
			boolean isFavourite = u.getFavouriteSongs().contains(s);
			// Modificar la lista de favoritos.
			if (isFavourite) u.asMut().removeFavouriteSong(s);
			else u.asMut().addFavouriteSong(s);
			// Avisar a interesados.
			userStatusListeners.forEach(l -> 
				l.onFavouritesChange(u.getFavouriteSongs())
			);
		});
	}
	
	/**
	 * Realiza una búsqueda de canciones a partir de los filtros proporcionados.
	 * 
	 * @param n Nombre de la canción.
	 * @param a Autor de la canción.
	 * @param f El usuario la ha marcado como favorita.
	 * @param s Estilo de la canción.
	 */
	public void searchSongs(String n, String a, boolean f, String s) {
		// No se permiten búsquedas sin una sesión abierta.
		if (!currentUser.isPresent()) return;
		
		// Buscar y eliminar las que no coincidan con el filtro de favorito.
		List<Song> searched = SongRepository.INSTANCE.getSearch(n, a, s);
		// Si se buscan favoritas eliminar las no favoritas.
		if (f) searched.removeIf(song -> 
			!currentUser.get().getFavouriteSongs().contains(song)
		);
				
		// Pasar la infomración a los escuchadores interesados.
		songStatusListeners.forEach(l -> {
			l.onSongSearch(searched, f);
		});
	}
	
	/**
	 * Establece la selección actual de canciones sobre las que se podrán 
	 * realizar operaciones.
	 * 
	 * @param s Lista de canciones que se desea que sea la selección actual.
	 */
	public void selectSongs(List<Song> list) {
		// Cargar nueva selección.
		selectedSongs.clear();
		selectedSongs.addAll(list);
		// Avisar a los interesados.
		songStatusListeners.forEach(l -> 
			l.onSongSelection(Collections.unmodifiableList(selectedSongs))
		);
	}
	
	/**
	 * Elimina la selección actual de canciones del repositorio.
	 */
	public void removeSelectedSongs(String password) {
		
		boolean canDelete = currentUser.isPresent() && 
							currentUser.get().getUserName().equals(adminUser) && 
							currentUser.get().checkPassword(password);
		
		if (!canDelete) {
			songStatusListeners.forEach(l -> l.onSongDeleteFailure());
			return;
		}
				
		// Eliminar canciones.
		selectedSongs.forEach(s -> {
			SongRepository.INSTANCE.removeSong(s);
		});
		// Limpiar la selección.
		selectedSongs.clear();
		// Reenviar datos de canciones actualizados.
		sendSongData();
		// Reenviar canciones favoritas. Han podido cambiar.
		currentUser.ifPresent(u -> {
			userStatusListeners.forEach(l -> 
				l.onFavouritesChange(u.getFavouriteSongs())
			);
		});
	}
	
	/**
	 * Método para iniciar la carga de canciones a partir de un fichero.
	 * 
	 * @param fichero Fichero que describe las canciones que cargar.
	 */
	public void cargarCanciones(String fichero) {
		buscadorCanciones.setArchivoCanciones(fichero);
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
	public void removeUserStatusListener(UserStatusListener l) {
		userStatusListeners.remove(l);
	}
	
	/**
	 * Registra un listener de estado de canciones en el controlador.
	 * 
	 * @param l Listener que se desea registrar.
	 */
	public void registerSongStatusListener(SongStatusListener l) {
		songStatusListeners.add(l);
	}
	
	/**
	 * Elimina un listener de estado de usuario del controlador.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removeSongStatusListener(SongStatusListener l) {
		songStatusListeners.remove(l);
	}
}
