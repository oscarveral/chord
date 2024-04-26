package umu.tds.chord.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.component.BuscadorCanciones;
import umu.tds.chord.component.Canciones;
import umu.tds.chord.component.CancionesEvent;
import umu.tds.chord.component.CancionesListener;
import umu.tds.chord.component.CargadorCanciones;
import umu.tds.chord.model.SongRepository;

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
	
	private Set<SongStatusListener> songStatusListeners;
	private Set<UserStatusListener> userStatusListeners;

	private Controller() {

		userStatusListeners = new HashSet<>();
		songStatusListeners = new HashSet<>();

		registerCancionesListener();
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
		buscadorCanciones.addCancionesListener(new CancionesListener() {

			@Override
			public void nuevasCanciones(CancionesEvent e) {
				processSongData(e.getCanciones());
			}
		});
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
}
