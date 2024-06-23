package umu.tds.chord.controller;

import java.io.File;
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
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.PlaylistFactory;
import umu.tds.chord.model.Song;

public enum Player {

	INSTANCE;

	public static final String EXTENSION = ".mp3";
	
	private static final String virtualName = "Búsqueda";
	private static final String tempPath = System.getProperty("java.io.tmpdir");
	private static final String remoteProtocol = "http";
	

	private Deque<Song> cola;
	private Deque<Song> log;

	private Optional<Song> currentSong;
	private Optional<Playlist> playlist;
	private Optional<MediaPlayer> reproductor;

	private Set<PlayerStatusListener> playStatusListeners;

	private boolean randomMode;

	private Player() {
		cola = new ArrayDeque<>();
		log = new ArrayDeque<>();

		playlist = Optional.empty();
		currentSong = Optional.empty();
		reproductor = Optional.empty();
		playStatusListeners = new HashSet<>();
		randomMode = false;

		Platform.startup(() -> {
		});
	}

	private void notifyState() {
		PlayerStatusEvent e = buildNotification();
		playStatusListeners.forEach(l -> l.onSongReproduction(e));
	}

	private void notifyProgress() {
		PlayerStatusEvent e = buildNotification();
		playStatusListeners.forEach(l -> l.onSongProgress(e));
	}

	private PlayerStatusEvent buildNotification() {
		PlayerStatusEvent e = new PlayerStatusEvent(this);
		currentSong.ifPresent(e::setSong);
		playlist.ifPresent(e::setPlaylist);
		reproductor.ifPresent(r -> e.setProgress(getProgress()));
		return e;
	}

	private double getProgress() {
		double res = 0.0;
		if (reproductor.isPresent() && reproductor.get().getStatus() != Status.UNKNOWN) {
			Duration current = reproductor.get().currentTimeProperty().getValue();
			Duration total = reproductor.get().getMedia().getDuration();
			res = current.toMillis() / total.toMillis();
		}
		return res;
	}

	private void loadSong() {
		// Desechado del reproductor antiguo.
		reproductor.ifPresent(MediaPlayer::dispose);
		reproductor = Optional.empty();
		// Si se ha especificado canción, se debe cargar.
		currentSong.ifPresent(s -> {
			try {
				// Crear el medio de reproducción.
				Optional<Media> media = createSongMedia(s);
				if (media.isEmpty()) return;
				
				// Se añade el nuevo reproductor.
				reproductor = Optional.of(new MediaPlayer(media.get()));
				reproductor.ifPresent(r -> {
					r.setOnEndOfMedia(this::siguiente);
					r.currentTimeProperty().addListener(o -> notifyProgress());
				});
			} catch (Exception e1) {
			}
		});
	}
	
	private Optional<Media> createSongMedia(Song s) {
		// Si la canción es local no hace falta descargarla
		if (!s.getPath().startsWith(remoteProtocol)) {
			File f = new File(s.getPath());
			return Optional.ofNullable(new Media(f.toURI().toString()));
		}
			
		try {
			// Crear la ruta a la canción.
			Path mp3 = Path.of(tempPath, String.valueOf(Integer.toUnsignedLong(s.hashCode())) + EXTENSION);
			// Descarga de la canción si no existe.
			if (!Files.exists(mp3)) {
				mp3 = Files.createFile(mp3);
				URL uri = new URL(s.getPath());
				InputStream stream = uri.openStream();
				Files.copy(stream, mp3, StandardCopyOption.REPLACE_EXISTING);
			}
			// Crear el medio que se reproducirá.
			return Optional.ofNullable(new Media(mp3.toFile().toURI().toString()));
		}
		catch (Exception e) {		
			return Optional.empty();
		}	
	}

	private void fillCola() {
		playlist.ifPresent(p -> {
			int size = p.getSongs().size();
			if (size == 0)
				return;
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
		playlist.ifPresent(play -> play.getSongs().forEach(s -> cola.pollLast()));
	}

	private void endReproduction() {
		currentSong = Optional.empty();
		reproductor.ifPresent(MediaPlayer::dispose);
		reproductor = Optional.empty();
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
	 * Reproduce una canción dada con prioridad sobre lo que suena actualmente. Este
	 * método no agrega la canción que suena actualmente al historial de
	 * reproducción canciones del reproductor.
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
	 * Reproduce la canción dada con prioridad asegurando que la canción que estaba
	 * sonando anteriormente se agrega al historial de reproducción de canciones del
	 * reproductor.
	 * 
	 * @param s Canción que se desea reproducir.
	 */
	public void pushReproduce(Song s) {
		currentSong.ifPresent(song -> log.addLast(song));
		reproduce(s);
	}

	/**
	 * Establece la playlist dada como playlis que se debe reproducir y agrega sus
	 * canciones a la cola actual de reproducción.
	 * 
	 * @param p Playlist que se desea reproducir.
	 */
	public void loadPlaylist(Playlist p) {
		// Se quitan los datos de la playlist anterior y se cargan los nuevos.
		clearCola();
		playlist = Optional.ofNullable(PlaylistFactory.clonePlaylist(p).get());
		fillCola();
		// Si no hay nada sonado debe sonar la siguiente canción.
		if (reproductor.isEmpty())
			siguiente();
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
	 * Reproduce la próxima canción en la cola o la próxima canción de la playlist
	 * si la cola estaba vacía.
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
	 * Para la reproducción de la canción actual empezando desde el principio dicha
	 * canción cuando se continue la reproducción.
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
		if (reproductor.isEmpty())
			siguiente();
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
	 * Cambia la reproducción al progreso especificado.
	 * 
	 * @param progress Progreso de la canción deseado.
	 */
	public void setReproductionProgress(double progress) {
		reproductor.ifPresent(r -> {
			Duration total = r.getMedia().getDuration();
			Duration newDuration = new Duration(progress * total.toMillis());
			r.seek(newDuration);
		});
	}

	/**
	 * Registra un listener de estado de reproducción.
	 * 
	 * @param l Listener que se desea registrar.
	 */
	public void registerPlayStatusListener(PlayerStatusListener l) {
		playStatusListeners.add(l);
	}

	/**
	 * Elimina un listener de estado de reporducción.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removePlayStatusListener(PlayerStatusListener l) {
		playStatusListeners.remove(l);
	}
}
