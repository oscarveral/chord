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
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.User;

/**
 * Panel usado para mostar la lista de playlist actual del usuario.
 */
final public class PlaylistsPanel extends JPanel{

	private static final long serialVersionUID = 8430914823843086279L;

	private static final String panelTitle = "Playlists";
	
	private JList<Playlist> lista;
	private PlaylistListModel datos;
	
	/**
	 * Constructor por defecto.
	 */
	public PlaylistsPanel() {
		super();
		// Sólo se llama al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		datos = new PlaylistListModel();
		lista = new JList<Playlist>(datos);
		
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setBackground(new Color(0, 0, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane(lista);
		scrollPane.setBorder(new EmptyBorder(10,10,10,10));
		
		add(scrollPane, BorderLayout.CENTER);
		
		setBorder(BorderFactory.createTitledBorder
				(BorderFactory.createLineBorder(Color.BLACK), panelTitle));
		
		registerControllerListeners();
	}
	
	// -------- Interacción con el controlador --------

	private void registerControllerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() 
		{
			// En el inicio de sesión se debe cargar la lista actual de 
			// favoritos del usuario.
			@Override
			public void onLogin(User u) {
				datos.setPlaylistList(u.getPlaylists());
			}
		});
	
	}
	
	// -------- Modelo de datos de la lista --------
	
	private final class PlaylistListModel extends AbstractListModel<Playlist> {

		private static final long serialVersionUID = -1207031221759711316L;

		private List<Playlist> playlists;
		
		public PlaylistListModel() {
			playlists = new ArrayList<Playlist>();
		}
		
		@Override
		public int getSize() {
			return playlists.size();
		}

		@Override
		public Playlist getElementAt(int index) {
			return playlists.get(index);
		}
		
		public void setPlaylistList(List<Playlist> playlists) {
			this.playlists.clear();
			this.playlists.addAll(playlists);
			fireContentsChanged(this, 0, playlists.size());
		}
		
	}
}
