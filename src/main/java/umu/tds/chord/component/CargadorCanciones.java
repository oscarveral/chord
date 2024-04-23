package umu.tds.chord.component;

import java.util.Optional;
import java.util.Vector;

/**
 * Cargador de canciones de la aplicación a partir de ficheros XML.
 */
public enum CargadorCanciones implements BuscadorCanciones{

	INSTANCE;

	private Optional<String> archivoCanciones;
	private Vector<CancionesListener> listeners;
	
	private CargadorCanciones() {
		archivoCanciones = Optional.empty();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @implNote El formato de los ficheros debe ser XML.
	 */
	@Override
	public void setArchivoCanciones(String fichero) {
		archivoCanciones = Optional.ofNullable(fichero);
		
		if (!archivoCanciones.isPresent()) return;
		
		Canciones canciones = MapperCancionesXMLtoJava
								.cargarCanciones(archivoCanciones.get());
		
		if (canciones == null) return;
		
		CancionesEvent e = new CancionesEvent(this, canciones);
		notificarCargaCanciones(e);
	}

	@Override
	public synchronized void addCancionesListener(CancionesListener l) {
		listeners.addElement(l);
	}

	@Override
	public synchronized void removeCancionesListener(CancionesListener l) {
		listeners.removeElement(l);
	}
	
	private synchronized void notificarCargaCanciones(CancionesEvent e) {
		listeners.forEach(l -> l.nuevasCanciones(e));
	}
}
