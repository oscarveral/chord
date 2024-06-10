package umu.tds.chord.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import umu.tds.chord.component.BuscadorCanciones;
import umu.tds.chord.component.Canciones;
import umu.tds.chord.component.CargadorCanciones;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.PlaylistFactory;
import umu.tds.chord.model.Song;
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

	private static final String adminUser = "admin";
	private static final String pdfTitle = "Chord. Resumen del usuario ";
	
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
	public boolean register(String username, String password, Date birthday) {
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
			currentUser.ifPresent(u -> {
				UserStatusEvent e = new UserStatusEvent(this, u);
				userStatusListeners.forEach(l -> l.onUserLogin(e));
			});
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
			if (updated) {
				currentUser = Optional.empty();
				UserStatusEvent e = new UserStatusEvent(this, null);
				userStatusListeners.forEach(l -> l.onUserLogout(e));
				Player.INSTANCE.clearState();
			}
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
			if (removed) {
				currentUser = Optional.empty();
				UserStatusEvent e = new UserStatusEvent(this, null);
				userStatusListeners.forEach(l -> l.onUserLogout(e));
				Player.INSTANCE.clearState();
			}
		});
		return currentUser.isEmpty();
	}
	
	/**
	 * Método para cambiar el estado premium del usuario actual.
	 */
	public void togglePremium() {
		currentUser.ifPresent(u -> {
			u.asMut().setPremium(!u.isPremium());
			UserStatusEvent e = new UserStatusEvent(this, u);
			userStatusListeners.forEach(l -> l.onUserMetadataChange(e));
		});
	}
	
	/**
	 * Método para cambiar el estado de favorito de una canción para el usuario
	 * actual.
	 * 
	 * @param s Canción para la que se desea cambiar el estado de favorito.
	 */
	public void toggleFavourite(Song s) {
		currentUser.ifPresent(u -> {
			boolean isFavourite = u.getFavouriteSongs().contains(s);
			
			if (isFavourite) u.asMut().removeFavouriteSong(s);
			else u.asMut().addFavouriteSong(s);
			
			UserStatusEvent e = new UserStatusEvent(this, u);
			
			// Avisar a interesados.
			userStatusListeners.forEach(l -> 
				l.onFavouriteSongsUpdate(e)
			);
		});
	}
	
	/**
	 * Añade una canción a la lista de recientes del usuario.
	 * 
	 * @param s Canción que se desea añadir.
	 */
	protected void addRecentSong(Song s) {
		currentUser.ifPresent(u -> {
			u.asMut().addRecentSong(s);
			UserStatusEvent e = new UserStatusEvent(this, u);
			userStatusListeners.forEach(l -> l.onRecentSongsUpdate(e));
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
	public void searchSongs(Optional<String> n, Optional<String> a, Optional<String> s, boolean f) {
		// No se permiten búsquedas sin una sesión abierta.
		if (!currentUser.isPresent()) return;
		
		// Buscar y eliminar las que no coincidan con el filtro de favorito.
		List<Song> searched = new ArrayList<>(SongRepository.INSTANCE.getSearch(n, a, s));
		// Si se buscan favoritas eliminar las no favoritas.
		if (f) searched.removeIf(song -> 
			!currentUser.get().getFavouriteSongs().contains(song)
		);
				
		// Pasar la infomración a los escuchadores interesados.
		SongStatusEvent e = new SongStatusEvent(this);
		e.setSongs(searched);
		songStatusListeners.forEach(l -> l.onSongSearch(e));
	}
	
	/**
	 * Elimina la selección actual de canciones del repositorio.
	 */
	public void removeSongs(List<Song> list, String password) {
		
		boolean canDelete = currentUser.isPresent() && 
							currentUser.get().getUserName().equals(adminUser) && 
							currentUser.get().checkPassword(password);
		
		SongStatusEvent e = new SongStatusEvent(this);
		
		if (!canDelete) {
			e.setFailed(true);
			songStatusListeners.forEach(l -> l.onSongDelete(e));
			return;
		}
				
		// Eliminar canciones.
		list.forEach(s -> {
			if (SongRepository.INSTANCE.removeSong(s))
				e.addSong(s);
		});
		
		songStatusListeners.forEach(l -> l.onSongDelete(e));
		
		// Reenviar canciones favoritas. Han podido cambiar.
		currentUser.ifPresent(u -> {
			UserStatusEvent ev = new UserStatusEvent(this, u);
			userStatusListeners.forEach(l -> 
				l.onFavouriteSongsUpdate(ev)
			);
		});
	}
	
	/**
	 * Método para indicar al controlador que realice un envío inicial de datos
	 * necesarios.
	 */
	public void ready() {
		SongStatusEvent e = new SongStatusEvent(this);
		SongRepository.INSTANCE.getSongs().forEach(e::addSong);
		songStatusListeners.forEach(l -> l.onSongLoad(e));
	}
	
	/**
	 * Método para crear una playlists para el usuario actual.
	 * 
	 * @param name Nombre de la playlists.
	 * @param description Descripción de la playlist.
	 */
	public void createPlaylist(String name, String description) {
		currentUser.ifPresent(u -> {
			PlaylistFactory.createPlaylist(name, description).ifPresent(p -> {
				u.asMut().addPlaylist(p);
				UserStatusEvent e = new UserStatusEvent(this, u);
				userStatusListeners.forEach(l -> l.onPlaylistsListUpdate(e));
			});
			UserRepository.INSTANCE.updateUser(u);
		});
	}
	
	/**
	 * Método para eliminar una playlists del usuario.
	 * 
	 * @param p Playlist que se desea eliminar.
	 */
	public void removePlaylist(Playlist p) {
		currentUser.ifPresent(u -> {
			if(u.asMut().removePlaylist(p)) {
				UserStatusEvent e = new UserStatusEvent(this, u);
				userStatusListeners.forEach(l -> l.onPlaylistsListUpdate(e));
				UserRepository.INSTANCE.updateUser(u);
			}
		});
	}
	
	/**
	 * Añade las canciones de la lista proporcionada a la playlist proporcionada
	 * si esta pertenece al usuario con la sesión actual.
	 * 
	 * @param p Playlists a la que se añadirán las canciones.
	 * @param l Lista de canciones que se desean añadir.
	 */
	public void addSongsPlaylist(Playlist p, List<Song> l) {
		currentUser.ifPresent(u -> {
			if (u.getPlaylists().contains(p)) {
				l.stream().forEach(s -> p.asMut().addSong(s));
				UserStatusEvent e = new UserStatusEvent(this, u);
				userStatusListeners.forEach(li -> li.onPlaylistsListUpdate(e));
				UserRepository.INSTANCE.updateUser(u);
			}
		});
	}
	
	/**
	 * Elimina las canciones de la lista proporcionada a la playlist proporcionada
	 * si esta pertenece al usuario con la sesión actual.
	 * 
	 * @param p Playlists de la que se eliminarán las canciones.
	 * @param l Lista de canciones que se desean eliminar.
	 */
	public void removeSongsPlaylist(Playlist p, List<Song> l) {
		currentUser.ifPresent(u -> {
			if (u.getPlaylists().contains(p)) {
				l.stream().forEach(s -> p.asMut().removeSong(s));
				UserStatusEvent e = new UserStatusEvent(this, u);
				userStatusListeners.forEach(li -> li.onPlaylistsListUpdate(e));
				UserRepository.INSTANCE.updateUser(u);
			}
		});	
	}
	
	/**
	 * Genera un PDF resumen con las playlist y canciones del usuario.
	 * 
	 * @param path Ruta donde se creará el fichero PDF.
	 */
	public void genPDF(String path) {
		currentUser.ifPresent(u -> {
			if (!u.isPremium()) return;
			try {
				FileOutputStream fichero = new FileOutputStream(path + "/" + u.getUserName() + ".pdf");
				Document documento = new Document();
				PdfWriter.getInstance(documento, fichero);
				documento.open();
				documento.addCreationDate();
				documento.addAuthor(u.getUserName());
				documento.addCreator(u.getUserName());
				documento.addTitle(pdfTitle + u.getUserName());
				documento.add(new Paragraph(pdfTitle + u.getUserName()));
				com.itextpdf.text.List playlistList = new com.itextpdf.text.List();
				u.getPlaylists().forEach(p -> {
					ListItem pla = new ListItem(new Paragraph("Playlist: " + p.getName()));
					com.itextpdf.text.List lista = new com.itextpdf.text.List(true);
					p.getSongs().forEach(s -> {
						ListItem i = new ListItem(s.getName() + " - " + s.getAuthor() + " - " + s.getStyle());
						i.setAlignment(Element.ALIGN_JUSTIFIED);
						lista.add(i);
					});
					pla.add(lista);
					playlistList.add(pla);			
				});
				documento.add(playlistList);
				documento.close();
			} catch (Exception e) {
				return;
			}
		});
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
		// Se crea un evento de estado de canciones.
		SongStatusEvent e = new SongStatusEvent(this);
		
		// No hacer nada en fallo.
		if (c.isEmpty()) {
			e.setFailed(true);
			songStatusListeners.forEach(l -> l.onSongLoad(e));
			return;
		}
		// Añadir las canciones al repositorio.
		Canciones canciones = c.get();
		canciones.getCancion().forEach(s -> {
			String name = s.getTitulo();
			String author = s.getInterprete();
			String url = s.getURL();
			String style = s.getEstilo();
			SongRepository.INSTANCE.addSong(name, author, url, style).ifPresent(e::addSong);
		});
		
		// Envío del evento.
		songStatusListeners.forEach(l -> l.onSongLoad(e));
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
