package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PlaylistManagementPanel extends JPanel {

	private static final long serialVersionUID = -5512808820258902555L;
	private static final String title = "Gestión de playlists";
	
	public PlaylistManagementPanel() {
		setLayout(new BorderLayout());
		
		initializeCreation();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
	}
	
	private void initializeCreation() {
		PlaylistFormPanel creation = new PlaylistFormPanel();
		add(creation, BorderLayout.CENTER);
	}
}
