package umu.tds.chord.component;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

/**
 * Cargador de canciones de la aplicación a partir de ficheros XML.
 */
public enum CargadorCanciones implements BuscadorCanciones {

	INSTANCE;

	private Optional<String> archivoCanciones;
	private List<CancionesListener> listeners;

	private CargadorCanciones() {
		archivoCanciones = Optional.empty();
		listeners = new Vector<>();
	}

	@Override
	public synchronized void addCancionesListener(CancionesListener l) {
		listeners.add(l);
	}

	private synchronized void notificarCargaCanciones(CancionesEvent e) {
		listeners.forEach(l -> l.nuevasCanciones(e));
	}

	@Override
	public synchronized void removeCancionesListener(CancionesListener l) {
		listeners.remove(l);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implNote El formato de los ficheros debe ser XML.
	 */
	@Override
	public void setArchivoCanciones(String fichero) {
		archivoCanciones = Optional.ofNullable(fichero);

		if (!archivoCanciones.isPresent()) {
			return;
		}

		Canciones canciones = MapperCancionesXMLtoJava.cargarCanciones(archivoCanciones.get());
		CancionesEvent e = new CancionesEvent(this, canciones);
		notificarCargaCanciones(e);
	}
}
