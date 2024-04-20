package umu.tds.chord.ui;

/**
 * Interfaz funcional que proporciona la funcionalida necesaria para que los 
 * elementos de la interfaz puedan escuchar eventos producidos por otros
 * elementos de la propia interfaz.
 */
public interface InterfaceEventListener {
	
	/**
	 * Método utilizado por un emisor para enviar un evento.
	 * 
	 * @param e Evento que se desea emitir. Véase {@link InterfaceEvent}.
	 */
	public void onEvent(InterfaceEvent e);
}
