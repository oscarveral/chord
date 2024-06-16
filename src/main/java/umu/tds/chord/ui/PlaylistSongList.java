package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import umu.tds.chord.model.Song;
import umu.tds.chord.ui.SongTable.Mode;

public class PlaylistSongList extends JPanel {
	
	private static final long serialVersionUID = 1281782779628796147L;

	private SongTable songTable;
	
	public PlaylistSongList() {
		BorderLayout layout = new BorderLayout();
		layout.setVgap(10);
		setLayout(layout);
		initializeSongTable();
	}
	
	private void initializeSongTable() {
		songTable = new SongTable(Mode.STANDARD);
		
		JScrollPane scrollPane = new JScrollPane(songTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void setSongList(List<Song> l) {
		songTable.loadSongList(l);
	}
}
