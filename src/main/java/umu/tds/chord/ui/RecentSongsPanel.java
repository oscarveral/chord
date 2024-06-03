package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Song;

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
		SongTableModel datos = new SongTableModel();
		JTable songTable = new JTable(datos);

		songTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		songTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				// Evitar doble selección.
				if (e.getValueIsAdjusting())
					return;

				// Obtenemos las canciones seleccionadas.
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();

				StateManager.INSTANCE.clearSelectedSongs();

				if (!lsm.isSelectionEmpty())
					for (int i : lsm.getSelectedIndices()) {
						Song s = datos.getList().get(i);
						StateManager.INSTANCE.addSelectedSong(s);
					}

			}
		});

		// Lo incluimos todo dentro de un panel deslizable.
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

	// -------- Modelo de datos de la tabla --------

	private final class SongTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 9064674061310096123L;

		private static final String[] columnNames = { "Título", "Intérprete", "Estilo", "Favorito" };

		private List<Song> songs;
		private Set<Song> favourites;

		private SongTableModel() {
			this.songs = new ArrayList<>();
			this.favourites = new HashSet<>();

			registerControllerListener();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Song s = songs.get(row);
			switch (col) {
			case 0:
				return s.getName();
			case 1:
				return s.getAuthor();
			case 2:
				return s.getStyle();
			case 3:
				return favourites.contains(s);
			default:
				throw new IllegalArgumentException("Unexpected value: " + col);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 3:
				return Boolean.class;
			default:
				return String.class;

			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 3) {
				Song s = songs.get(rowIndex);
				toggleFavourite(s);
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 3;
		}

		@Override
		public int getRowCount() {
			return songs.size();
		}

		@Override
		public String getColumnName(int index) {
			return columnNames[index];
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		public List<Song> getList() {
			return this.songs;
		}

		public void setList(List<Song> songs) {
			this.songs.clear();
			this.songs.addAll(songs);
			fireTableDataChanged();
		}

		public void setFavourites(Set<Song> favourites) {
			this.favourites.clear();
			this.favourites.addAll(favourites);
			fireTableDataChanged();
		}

		public void clearData() {
			this.songs.clear();
			this.favourites.clear();
			fireTableDataChanged();
		}

		// -------- Interacción con el controlador --------

		private void registerControllerListener() {
			Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {

				@Override
				public void onUserLogin(UserStatusEvent e) {
					onRecentSongsUpdate(e);
					onFavouriteSongsUpdate(e);
				}

				@Override
				public void onFavouriteSongsUpdate(UserStatusEvent e) {
					e.getUser().ifPresent(u -> 
						setFavourites(u.getFavouriteSongs())
					);
				}
				
				@Override
				public void onRecentSongsUpdate(UserStatusEvent e) {
					e.getUser().ifPresent(u -> 
						setList(u.getRecentSongs())
					);
				}

				@Override
				public void onUserLogout(UserStatusEvent e) {
					clearData();
				}
			});
		}

		private void toggleFavourite(Song s) {
			Controller.INSTANCE.toggleFavourite(s);
		}
	}
	
}
