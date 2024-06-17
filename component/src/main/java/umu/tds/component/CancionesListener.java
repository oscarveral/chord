package umu.tds.component;

import java.util.EventListener;

/**
 * Listener que permite la escucha de eventos de carga de canciones.
 */
public interface CancionesListener extends EventListener {

	/**
	 * Método emitido cuando se ha producido la carga de una nueva lista de
	 * canciones.
	 *
	 * @param e Evento producido.
	 */
	public void nuevasCanciones(CancionesEvent e);

}
