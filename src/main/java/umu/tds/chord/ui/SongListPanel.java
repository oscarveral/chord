package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.PlayStatusListener;
import umu.tds.chord.controller.Player;
import umu.tds.chord.controller.PlayerStatusEvent;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Song;

public class SongListPanel extends JPanel {

	private static final long serialVersionUID = 1281782779628796147L;

	public SongListPanel() {
		BorderLayout layout = new BorderLayout();
		layout.setVgap(10);
		setLayout(layout);
		initializeSongTable();
	}
	
	private void initializeSongTable() {
		SongTableModel datos = new SongTableModel();
		JTable songTable = new JTable(datos);

		songTable.setAutoCreateRowSorter(true);
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
						int index = songTable.getRowSorter().convertRowIndexToModel(i);
						Song s = datos.getList().get(index);
						StateManager.INSTANCE.addSelectedSong(s);
					}

			}
		});
		songTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
					StateManager.INSTANCE.reproduceFirstSelectedSong();
				}
			}
		});
		
		// Lo incluimos todo dentro de un panel deslizable.
		JScrollPane scrollPane = new JScrollPane(songTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				songTable.clearSelection();
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {}
		});
		
		add(scrollPane, BorderLayout.CENTER);
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
					e.getUser().ifPresent(u -> {
						setFavourites(u.getFavouriteSongs());
					});
				}

				@Override
				public void onFavouriteSongsUpdate(UserStatusEvent e) {
					onUserLogin(e);
				}

				@Override
				public void onUserLogout(UserStatusEvent e) {
					clearData();
				}
			});

			Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {

				@Override
				public void onSongLoad(SongStatusEvent e) {
					if (!e.isFailed()) {
						songs.addAll(e.getSongs());
					}
					Collections.sort(songs, (a, b) -> a.getReproducciones() - b.getReproducciones());					
					fireTableDataChanged();
				}

				@Override
				public void onSongDelete(SongStatusEvent e) {
					if (!e.isFailed())
						songs.removeAll(e.getSongs());
					fireTableDataChanged();
				}
			});
			
			Player.INSTANCE.registerPlayStatusListener(new PlayStatusListener() {
				@Override
				public void onSongReproduction(PlayerStatusEvent e) {
					Collections.sort(songs, (a, b) -> a.getReproducciones() - b.getReproducciones());
					fireTableDataChanged();
				}
			});
		}

		private void toggleFavourite(Song s) {
			Controller.INSTANCE.toggleFavourite(s);
		}
	}
}
