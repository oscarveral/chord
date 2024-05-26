package umu.tds.chord.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import umu.tds.chord.model.Song;

public class SongListPanel {
	// -------- Modelo de datos de la tabla --------

	private final class SongTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 9064674061310096123L;

		private String[] columnNames = { "Título", "Intérprete", "Estilo", "Favorito" };

		private List<Song> songs;

		private SongTableModel() {
			this.songs = new ArrayList<Song>();
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
				return currentFavourites.contains(s);
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
				// Se solicita modificar el favorito de la canción.
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

		/**
		 * Establece una nueva lista de canciones para mostrar.
		 * 
		 * @param songs Lista de canciones que mostrar.
		 */
		public void setSongList(List<Song> songs) {
			this.songs.clear();
			this.songs.addAll(songs);
			fireTableDataChanged();
		}

		public List<Song> getSongList() {
			return songs;
		}

		/**
		 * Forzar actualización de la tabla.
		 */
		public void upadte() {
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int index) {
			return columnNames[index];
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
	}
}
