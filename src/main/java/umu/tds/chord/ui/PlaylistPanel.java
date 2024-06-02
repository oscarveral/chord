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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;

public class PlaylistPanel extends JPanel {

	private static final long serialVersionUID = -2343473370166284221L;
	private static final String panelTitle = "Playlists";
	private static final int scrollPaneBorder = 10;

	public PlaylistPanel() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		initializeLista();

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), panelTitle));
	}

	// -------- Interfaz. --------


	private void initializeLista() {
		PlaylistListModel datos = new PlaylistListModel();
		JList<Playlist> lista = new JList<>(datos);
		
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setOpaque(false);
		lista.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Evitar doble selección.
				if (e.getValueIsAdjusting()) return;
				Playlist p = lista.getSelectedValue();
				StateManager.INSTANCE.setSelectedPlaylist(p);
			}
		});
		
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onPlaylistsListUpdate(UserStatusEvent e) {
				lista.clearSelection();
			}
			
			@Override
			public void onUserLogin(UserStatusEvent e) {
				onPlaylistsListUpdate(e);
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				onPlaylistsListUpdate(e);
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(lista);
		scrollPane.setBorder(new EmptyBorder(scrollPaneBorder, scrollPaneBorder, scrollPaneBorder, scrollPaneBorder));
		add(scrollPane, BorderLayout.CENTER);
	}

	// -------- Modelo de datos de la lista. --------

	private final class PlaylistListModel extends AbstractListModel<Playlist> {

		private static final long serialVersionUID = -1207031221759711316L;

		private List<Playlist> playlists;

		public PlaylistListModel() {
			playlists = new ArrayList<>();
			
			registerControllerListeners();
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

		// -------- Interacción con el controlador. --------

		private void registerControllerListeners() {
			Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {

				// En el inicio de sesión se debe cargar la lista de playlists.
				@Override
				public void onUserLogin(UserStatusEvent e) {
					e.getUser().ifPresent(u -> {
						setList(u.getPlaylists());
					});
				}

				@Override
				public void onPlaylistsListUpdate(UserStatusEvent e) {
					onUserLogin(e);
				}

				@Override
				public void onUserLogout(UserStatusEvent e) {
					clearList();
				}

			});

		}

	}
}
