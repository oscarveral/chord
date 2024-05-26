package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;

public class PlaylistPanel extends JPanel {

	private static final long serialVersionUID = -2343473370166284221L;
	private static final String panelTitle = "Playlists";
	private static final int scrollPaneBorder = 10;

	private PlaylistListModel datos;
	private JList<Playlist> lista;

	public PlaylistPanel() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		initializeDatos();
		initializeLista();
		initializeScrollPane();

		registerControllerListeners();

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), panelTitle));
	}

	// -------- Interfaz. --------

	private void initializeDatos() {
		datos = new PlaylistListModel();
	}

	private void initializeLista() {
		lista = new JList<>(datos);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setOpaque(false);

	}

	private void initializeScrollPane() {
		JScrollPane scrollPane = new JScrollPane(lista);
		scrollPane.setBorder(new EmptyBorder(scrollPaneBorder, scrollPaneBorder, scrollPaneBorder, scrollPaneBorder));
		add(scrollPane, BorderLayout.CENTER);
	}

	// -------- Interacción con el controlador. --------

	private void registerControllerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {

			// En el inicio de sesión se debe cargar la lista de playlists.
			@Override
			public void onUserLogin(UserStatusEvent e) {
				datos.setList(e.getUser().getPlaylists());
			}
			
			@Override
			public void onPlaylistsListUpdate(UserStatusEvent e) {
				datos.setList(e.getUser().getPlaylists());
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				datos.clearList();
			}
			
		});

	}

	// -------- Modelo de datos de la lista. --------

	private final class PlaylistListModel extends AbstractListModel<Playlist> {

		private static final long serialVersionUID = -1207031221759711316L;

		private List<Playlist> playlists;

		public PlaylistListModel() {
			playlists = new ArrayList<>();
		}

		@Override
		public int getSize() {
			return playlists.size();
		}

		@Override
		public Playlist getElementAt(int index) {
			return playlists.get(index);
		}

		public void setList(List<Playlist> playlists) {
			this.playlists.clear();
			this.playlists.addAll(playlists);
			fireContentsChanged(this, 0, this.playlists.size());
		}
		
		public void clearList() {
			this.playlists.clear();
			fireContentsChanged(this, 0, this.playlists.size());
		}

	}
}
