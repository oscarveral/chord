package umu.tds.chord.controller;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.PlaylistFactory;
import umu.tds.chord.model.Song;

public enum Player {

	INSTANCE;
		
	private static final String virtualName = "Búsqueda";
	
	private Deque<Song> cola;
	private Deque<Song> log;

	private Optional<Song> currentSong;
	private Optional<Playlist> playlist;
	private Optional<MediaPlayer> reproductor;
	
	private Set<PlayStatusListener> playStatusListeners;
	
	private boolean randomMode;
	
	private Player() {
		cola = new ArrayDeque<>();
		log = new ArrayDeque<>();

		playlist = Optional.empty();
		currentSong = Optional.empty();
		reproductor = Optional.empty();
		playStatusListeners = new HashSet<>();
		randomMode = false;

		Platform.startup(() -> {});
	}
	
	private void notifyState() {
		// Notificar con canción y playlist actual.
		PlayerStatusEvent e = new PlayerStatusEvent(this);
		currentSong.ifPresent(e::setSong);
		playlist.ifPresent(e::setPlaylist);
		playStatusListeners.forEach(l -> l.onSongReproduction(e));
	}
	
	private void loadSong() {
		// Desechado del reproductor antiguo.
		reproductor.ifPresent(MediaPlayer::dispose);
		reproductor = Optional.empty();
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
				reproductor = Optional.of(new MediaPlayer(media));
				reproductor.ifPresent(p -> p.setOnEndOfMedia(this::siguiente));
			} 
			catch (Exception e1) {}
		});
	}
	
	private void fillCola() {
		playlist.ifPresent(p -> {
			int size = p.getSongs().size();
			if (size == 0) return;
			// En modo aleatorio se randomiza el orden.
			if (randomMode) {
				List<Song> tmp = new ArrayList<>(p.getSongs());
				tmp.sort((s1, s2) -> ThreadLocalRandom.current().nextInt());
				tmp.forEach(s -> cola.addLast(s));
				return;
			}
			// Por defecto se añaden todas las canciones.
			p.getSongs().forEach(s -> cola.addLast(s));
		});
	}
	
	private void clearCola() {
		// Limpiar la cola de la playlist actual implica eliminar desde el final
		// una canción por cada una que tenga la playlist.
		playlist.ifPresent(play -> {
			play.getSongs().forEach(s -> cola.pollLast());
		});
	}
	
	private void endReproduction() {
		currentSong = Optional.empty();
		reproductor.ifPresent(MediaPlayer::dispose);
		notifyState();
	}
	
	protected void clearState() {
		cola.clear();
		playlist = Optional.empty();
		currentSong = Optional.empty();
		reproductor.ifPresent(MediaPlayer::dispose);
		reproductor = Optional.empty();
		randomMode = false;
		log.clear();
		notifyState();
	}
		
	/**
	 * Reproduce una canción dada con prioridad sobre lo que suena actualmente.
	 * 
	 * @param s Canción que se desea reproducir.
	 */
	public void reproduce(Song s) {
		currentSong = Optional.ofNullable(s);
		loadSong();
		reproductor.ifPresent(p -> {
			p.play();
			Controller.INSTANCE.incrementSongReproduction(s);
			Controller.INSTANCE.addRecentSong(s);
		});
		notifyState();
	}
	
	/**
	 * Establece la playlist dada como playlis que se debe reproducir y agrega
	 * sus canciones a la cola actual de reproducción.
	 * 
	 * @param p Playlist que se desea reproducir.
	 */
	public void loadPlaylist(Playlist p) {
		// Se quitan los datos de la playlist anterior y se cargan los nuevos.
		clearCola();
		playlist = Optional.ofNullable(PlaylistFactory.clonePlaylist(p).get());	
		fillCola();
		// Si no hay nada sonado debe sonar la siguiente canción.
		if (reproductor.isEmpty()) siguiente();
		notifyState();
	}
	
	/**
	 * Reproduce una lista de canciones como una playlist virtual cuyo nombre es
	 * {@link Player#virtualName}.
	 * 
	 * @param l Lista de canciones que reproducir.
	 */
	public void loadPlaylisy(List<Song> l) {
		PlaylistFactory.createPlaylist(virtualName, virtualName).ifPresent(p -> {
			l.forEach(s -> p.asMut().addSong(s));
			loadPlaylist(p);
		});
	}
	
	/**
	 * Pausa la reproducción de la canción actual.
	 */
	public void pausar() {
		reproductor.ifPresent(MediaPlayer::pause);
	}
	
	/**
	 * Reanuda la reproducción de la canción actual.
	 */
	public void reanudar() {
		reproductor.ifPresent(MediaPlayer::play);
	}
	
	/**
	 * Reproduce la próxima canción en la cola o la próxima canción de la 
	 * playlist si la cola estaba vacía.
	 */
	public void siguiente() {
		currentSong.ifPresent(s -> log.addLast(s));
		if (cola.isEmpty()) {
			fillCola();
		}
		reproduce(cola.pollFirst());
	}	
	/**
	 * Reproduce la canción anterior de la playlist.
	 */
	public void anterior() {
		currentSong.ifPresent(s -> cola.addFirst(s));
		if (log.isEmpty()) {
			endReproduction();	
			return;
		}
		reproduce(log.pollLast());
	}
	
	/**
	 * Para la reproducción de la canción actual empezando desde el principio
	 * dicha canción cuando se continue la reproducción.
	 */
	public void stop() {
		reproductor.ifPresent(MediaPlayer::stop);
	}
	
	/**
	 * Añade la lista de canciones proporcionadas a la cola.
	 * 
	 * @param l Lista de canciones que se desean añardir a la cola.
	 */
	public void addCola(List<Song> l) {
		l.forEach(s -> cola.addFirst(s));
		if (reproductor.isEmpty()) siguiente();
		notifyState();
	}
	
	/**
	 * Establece el modo aleatorio de reproducción.
	 * 
	 * @param random Nuevo modo aleatorio.
	 */
	public void setRandomMode(boolean random) {
		// Refrescar los datos de la playlist en la cola.
		clearCola();
		randomMode = random;
		fillCola();
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
}
