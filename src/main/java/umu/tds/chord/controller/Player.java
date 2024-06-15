package umu.tds.chord.controller;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.PlaylistFactory;
import umu.tds.chord.model.Song;

public enum Player {

	INSTANCE;
		
	private static final String virtualName = "Búsqueda";
	
	private Queue<Song> cola;
	private Optional<Song> currentSong;
	private int index;
	private Optional<Playlist> playlist;
	private Optional<MediaPlayer> player;

	private Set<PlayStatusListener> playStatusListeners;
	
	
	private Player() {
		cola = new ArrayDeque<>();
		playlist = Optional.empty();
		currentSong = Optional.empty();
		player = Optional.empty();
		playStatusListeners = new HashSet<>();

		Platform.startup(() -> {});
	}
	
	/**
	 * Añade la lista de canciones proporcionadas a la cola.
	 * 
	 * @param l Lista de canciones que se desean añardir a la cola.
	 */
	public void addCola(List<Song> l) {
		cola.addAll(l);
	}
	
	/**
	 * Reproduce una canción dada con prioridad sobre lo que suena actualmente.
	 * 
	 * @param s Canción que se desea reproducir.
	 */
	public void reproducePriority(Song s) {
		currentSong = Optional.ofNullable(s);
		loadCurrentSong();
		player.ifPresent(MediaPlayer::play);
	}
	
	/**
	 * Carga una canción dada estableciendo el reproductor de canciones.
	 * Realiza toda la gestión de estado de reproducción.
	 */
	private void loadCurrentSong() {
		// Desechado del reproductor antiguo.
		player.ifPresent(MediaPlayer::dispose);
		// Si se ha especificado canción, se debe cargar.
		currentSong.ifPresent(s -> {
			try {
				// Crear la ruta a la canción.
				Path mp3 = Path.of(System.getProperty("java.io.tmpdir"), String.valueOf(Integer.toUnsignedLong(s.hashCode())) + ".mp3");
				// Descarga de la canción si no existe.
				if (!Files.exists(mp3)) {
					mp3 = Files.createFile(mp3);
					URL uri = new URL(s.getPath());
					InputStream stream = uri.openStream();
					Files.copy(stream, mp3, StandardCopyOption.REPLACE_EXISTING);
				}
				// Crear el medio que se reproducirá.
				Media media = new Media(mp3.toFile().toURI().toString());
				// Se añade el nuevo reproductor.
				player = Optional.of(new MediaPlayer(media));
				player.ifPresent(p -> p.setOnEndOfMedia(this::siguiente));
				// Actualizar estado de controladores.
				Controller.INSTANCE.incrementSongReproduction(s);
				Controller.INSTANCE.addRecentSong(s);
			} 
			catch (Exception e1) {
				// En caso de fallo opcional vacío.
				player = Optional.empty();
				currentSong = Optional.empty();
			}
		});
		
		// Notificar el estado de reproducción de forma incondicional.
		notifyState();
	}
	
	/**
	 * Notificación de actualización de estado de reproducción de canciones.
	 */
	private void notifyState() {
		// Notificar con canción y playlist actual.
		PlayerStatusEvent e = new PlayerStatusEvent(this);
		currentSong.ifPresent(e::setSong);
		playlist.ifPresent(e::setPlaylist);
		playStatusListeners.forEach(l -> l.onSongReproduction(e));
	}
	
	/**
	 * Establece la playlist dada como playlis que se debe reproducir y empieza
	 * la reproducción de sus canciones desde el principio.
	 * 
	 * @param p Playlist que se desea reproducir.
	 */
	public void play(Playlist p) {
		index = 0;
		playlist = Optional.ofNullable(p);
		if (isValidPlaylist()) {
			adjustIndex();
			currentSong = Optional.of(playlist.get().getSong(index));
			loadCurrentSong();
			player.ifPresent(MediaPlayer::play);
		}
	}
	
	/**
	 * Pausa la reproducción de la canción actual.
	 */
	public void pausar() {
		player.ifPresent(MediaPlayer::pause);
	}
	
	/**
	 * Reanuda la reproducción de la canción actual.
	 */
	public void reanudar() {
		player.ifPresent(MediaPlayer::play);
	}
	
	/**
	 * Reproduce la próxima canción en la cola o la próxima canción de la 
	 * playlist si la cola estaba vacía.
	 */
	public void siguiente() {
		currentSong = Optional.empty();
		// Si la cola está vacía reproduce la siguiente de la playlist actual.
		if (cola.isEmpty()) {
			playlist.ifPresentOrElse(p -> {
				if (isValidPlaylist()) {
					index += 1;
					adjustIndex();
					currentSong = Optional.of(p.getSong(index));
					loadCurrentSong();
					player.ifPresent(MediaPlayer::play);
				}
			}, this::endReproduction);			
		}
		// Si la cola no está vacía se reproduce de la cola.
		else {
			currentSong = Optional.of(cola.poll());
			loadCurrentSong();
			player.ifPresent(MediaPlayer::play);
		}
	}
	
	/**
	 * Reproduce la canción anterior de la playlist.
	 */
	public void anterior() {
		currentSong = Optional.empty();
		// Si la cola está vacía reproduce la anterior de la playlist actual.
		if (isValidPlaylist()) {
			index -= 1;
			adjustIndex();
			currentSong = Optional.of(playlist.get().getSong(index));
			loadCurrentSong();
			player.ifPresent(MediaPlayer::play);
		}		
	}
	
	private void adjustIndex() {
		int size = playlist.get().getSongs().size();
		index = index >= size ? 0 : index;
		index = index < 0 ? size -1 : index;
	}
	
	private boolean isValidPlaylist() {
		if (playlist.isEmpty()) return false;
		int size = playlist.get().getSongs().size();
		return size != 0;
	}
	
	/**
	 * Para la reproducción de la canción actual empezando desde el principio
	 * dicha canción cuando se continue la reproducción.
	 */
	public void stop() {
		player.ifPresent(MediaPlayer::stop);
	}
	
	/**
	 * Resetea el estado del reproductor.
	 */
	protected void clearState() {
		cola.clear();
		playlist = Optional.empty();
		currentSong = Optional.empty();
		player = Optional.empty();
	}
	
	/**
	 * Registra un listener de estado de reproducción.
	 * 
	 * @param l Listener que se desea registrar.
	 */
	public void registerPlayStatusListener(PlayStatusListener l) {
		playStatusListeners.add(l);
	}
	
	/**
	 * Elimina un listener de estado de reporducción.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removePlayStatusListener(PlayStatusListener l) {
		playStatusListeners.remove(l);
	}
	
	/**
	 * Establece el estado del reproductor al finalizar la reproducción.
	 */
	private void endReproduction() {
		currentSong = Optional.empty();
		player.ifPresent(MediaPlayer::dispose);
		notifyState();
	}
	
	/**
	 * Reproduce una lista de canciones como una playlist virtual cuyo nombre es
	 * {@link Player#virtualName}.
	 * 
	 * @param l Lista de canciones que reproducir.
	 */
	public void playVirtualPlaylist(List<Song> l) {
		PlaylistFactory.createPlaylist(virtualName, virtualName).ifPresent(p -> {
			l.forEach(s -> p.asMut().addSong(s));
			play(p);
		});
	}
}
