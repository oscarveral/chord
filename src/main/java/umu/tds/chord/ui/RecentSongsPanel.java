package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import umu.tds.chord.ui.SongTable.Mode;

public class RecentSongsPanel extends JPanel {

	private static final long serialVersionUID = -4486884398599339204L;
	private static final String title = "Canciones recientes";
	private static final String addText = "Añadir canciones seleccionadas a la playlist seleccionada";
	
	public RecentSongsPanel() {
		setLayout(new GridBagLayout());
		
		initializeSongTable();
		initializeAddSongsButton();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
	}
	
	private void initializeSongTable() {
		SongTable songTable = new SongTable(Mode.RECENT);
		
		JScrollPane scrollPane = new JScrollPane(songTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 7, 10);
		
		add(scrollPane, c);
	}
	
	private void initializeAddSongsButton() {
		ResponsiveButton addSongs = new ResponsiveButton(addText);
		addSongs.addActionListener(e -> addSongs());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 10, 10);
		
		add(addSongs ,c);
	}
	
	private void addSongs() {
		StateManager.INSTANCE.addSelectedSongsToSelectedPlaylist();
	}	
}
