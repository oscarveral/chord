package umu.tds.chord.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Panel usado para mostar la lista de playlist actual del usuario.
 */
public class PlaylistsPanel extends JPanel{

	private static final long serialVersionUID = 8430914823843086279L;

	private static final String panelTitle = "Playlists";
	
	/**
	 * Constructor por defecto.
	 */
	public PlaylistsPanel() {
		super();
		// Sólo se llama al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		setBorder(BorderFactory.createTitledBorder
				(BorderFactory.createLineBorder(Color.BLACK), panelTitle));
	}
}
