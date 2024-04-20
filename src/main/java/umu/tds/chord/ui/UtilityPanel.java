package umu.tds.chord.ui;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

public class UtilityPanel extends JPanel{

	// Listeners para los eventos que pueda producir este panel.
	private Set<InterfaceEventListener> listeners = new HashSet<InterfaceEventListener>();
	
	/**
	 * Añade un listener para los eventos producidos por este panel.
	 * 
	 * @param l Listener que se desea añadir.
	 */
	public void addInterfaceEventListener(InterfaceEventListener l) {
		listeners.add(l);
	}
	
	/**
	 * Elimina un listener de los eventos del panel.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removeInterfaceEventListener(InterfaceEventListener l) {
		listeners.remove(l);
	}
}
