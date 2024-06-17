package umu.tds.chord.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.AbstractTableModel;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.PlayerStatusListener;
import umu.tds.chord.controller.Player;
import umu.tds.chord.controller.PlayerStatusEvent;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Song;

public class SongTable extends JTable {

	private static final long serialVersionUID = 7439318313367847560L;

	private SongTableModel data;

	public enum Mode {
		STANDARD, EXTENDED, SEARCH, RECENT,
	}

	public SongTable(Mode mode) {
		data = new SongTableModel();
		setModel(data);
		initializeInteractivity();

		switch (mode) {
		case STANDARD:
			data.setUpStandardMode();
			break;
		case EXTENDED:
			data.setUpExtendedMode();
			break;
		case SEARCH:
			data.setUpSearchMode();
			break;
		case RECENT:
			data.setUpRecentMode();
			break;
		default:
			break;
		}
	}

	private void initializeInteractivity() {
		setAutoCreateRowSorter(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		getSelectionModel().addListSelectionListener(e -> {

			// Evitar doble selección.
			if (e.getValueIsAdjusting())
				return;

			// Obtenemos las canciones seleccionadas.
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			StateManager.INSTANCE.clearSelectedSongs();

			if (!lsm.isSelectionEmpty())
				for (int i : lsm.getSelectedIndices()) {
					int index = getRowSorter().convertRowIndexToModel(i);
					Song s = ((SongTableModel) getModel()).getList().get(index);
					StateManager.INSTANCE.addSelectedSong(s);
				}

		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
					StateManager.INSTANCE.reproduceFirstSelectedSong();
				}
			}
		});
		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				StateManager.INSTANCE.clearSelectedSongs();
				clearSelection();
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
			}
		});
	}

	public void loadSongList(List<Song> songs) {
		data.setList(songs);
	}

	private final class SongTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 7161104567245200626L;
		private static final String[] columnNames = { "Título", "Intérprete", "Estilo", "Favorito", "Reproducciones" };

		private List<Song> songs;
		private Set<Song> favourites;

		private SongTableModel() {
			this.songs = new ArrayList<>();
			this.favourites = new HashSet<>();
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
			case 4:
				return s.getReproducciones();
			default:
				throw new IllegalArgumentException("Unexpected value: " + col);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 3:
				return Boolean.class;
			case 4:
				return Integer.class;
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

		private void toggleFavourite(Song s) {
			Controller.INSTANCE.toggleFavourite(s);
		}

		private void setUpExtendedMode() {
			Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {

				@Override
				public void onUserLogin(UserStatusEvent e) {
					e.getUser().ifPresent(u -> setFavourites(u.getFavouriteSongs()));
				}

				@Override
				public void onFavouriteSongsUpdate(UserStatusEvent e) {
					onUserLogin(e);
				}

				@Override
				public void onUserLogout(UserStatusEvent e) {
					getRowSorter().setSortKeys(null);
				}
			});

			Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {

				@Override
				public void onSongLoad(SongStatusEvent e) {
					if (!e.isFailed()) {
						songs.addAll(e.getSongs());
					}
					fireTableDataChanged();
				}

				@Override
				public void onSongDelete(SongStatusEvent e) {
					if (!e.isFailed())
						songs.removeAll(e.getSongs());
					fireTableDataChanged();
				}
			});

			Player.INSTANCE.registerPlayStatusListener(new PlayerStatusListener() {
				@Override
				public void onSongReproduction(PlayerStatusEvent e) {
					fireTableDataChanged();
				}
			});
		}

		private void setUpSearchMode() {
			removeExtendedColumns();
			Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
				@Override
				public void onSongLoad(SongStatusEvent e) {
					if (!e.isFailed()) {
						songs.addAll(e.getSongs());
					}
					fireTableDataChanged();
				}

				@Override
				public void onSongDelete(SongStatusEvent e) {
					if (!e.isFailed())
						songs.removeAll(e.getSongs());
					fireTableDataChanged();
				}

				@Override
				public void onSongSearch(SongStatusEvent e) {
					if (!e.isFailed())
						setList(e.getSongs());
				}
			});

			Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
				@Override
				public void onUserLogin(UserStatusEvent e) {
					e.getUser().ifPresent(u -> setFavourites(u.getFavouriteSongs()));
				}

				@Override
				public void onFavouriteSongsUpdate(UserStatusEvent e) {
					onUserLogin(e);
				}

				@Override
				public void onUserLogout(UserStatusEvent e) {
					getRowSorter().setSortKeys(null);
				}
			});
		}

		private void setUpRecentMode() {
			removeExtendedColumns();
			Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
				@Override
				public void onUserLogin(UserStatusEvent e) {
					e.getUser().ifPresent(u -> {
						setList(u.getRecentSongs());
						setFavourites(u.getFavouriteSongs());
					});
				}

				@Override
				public void onRecentSongsUpdate(UserStatusEvent e) {
					onUserLogin(e);
				}

				@Override
				public void onFavouriteSongsUpdate(UserStatusEvent e) {
					onUserLogin(e);
				}

				@Override
				public void onUserLogout(UserStatusEvent e) {
					getRowSorter().setSortKeys(null);
					clearData();
				}
			});
			Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
				@Override
				public void onSongDelete(SongStatusEvent e) {
					if (!e.isFailed())
						songs.removeAll(e.getSongs());
					fireTableDataChanged();
				}
			});
		}

		private void setUpStandardMode() {
			removeExtendedColumns();
			Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {

				@Override
				public void onUserLogin(UserStatusEvent e) {
					e.getUser().ifPresent(u -> setFavourites(u.getFavouriteSongs()));
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
		}

		private void removeExtendedColumns() {
			removeColumn(getColumnModel().getColumn(4));
		}
	}
}
