package umu.tds.chord.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.utils.ImageScaler;

/**
 * Panel de utilidad que contiene los botones que permiten mostrar de forma 
 * condicional diferentes interfaces para acceder a toda la funcionalidad de
 * la aplicación. Se compone de:
 * 	- Botón de búsqueda (siempre).
 * 	- Botón de gestión de playlists (siempre).
 * 	- Botón de canciones recientes (siempre).
 * 	- Botón de playlists (siempre).
 * 	- Lista de playlists (según contexto).
 */
final public class UtilityPanel extends JPanel{

private static final long serialVersionUID = 6080459262898778835L;
	
	private static final int iconSize = 20;
	
	private static final String buttonMenuTitle = "Música";
	
	private static final String searchButtonText = "Buscar";
	private static final String playlistManagementButtonText = 
			"Gestionar Playlists";
	private static final String recentSongsButtonText = "Canciones Recientes";
	private static final String playlistsButtonText = "Mis Playlists";
	
	private static final String searchButtonIconPath = 
			"/images/search_button_icon.png";
	private static final String playlistManagementButtonIconPath = 
			"/images/playlist_edit_icon.png";
	private static final String recentSongsButtonIconPath =
			"/images/recent_songs_icon.png";
	private static final String playlistsButtonIconPath = 
			"/images/playlist_icon.png";
	
	private static final String invisibleTag = "invisible";
	private static final String playlistsTag = "playlists";
	
	private JButton searchButton;
	private JButton playlistManagementButton;
	private JButton recentSongsButton;
	private JButton playlistsButton;
	
	private PlaylistsPanel playlistsPanel;
	private JPanel playlistPanelContainer;
	private CardLayout playlistaPanelLayout;
	
	// Listeners para los eventos que pueda producir este panel.
	private Set<InterfaceEventListener> listeners;
	
	/**
	 * Constructor por defecto.
	 */
	public UtilityPanel() {
		super();
		// Sólo se llama al método de inicialización.
		initialize();
	}
	
	private void initialize() {

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		initializeSearchButton();
		initializePlaylistManagementButton();
		initializeRecentSongsButton();
		initializePlaylistsButton();
		initializePlaylistsPanel();
		
		registerControllerListener();
				
		listeners = new HashSet<InterfaceEventListener>();
									
		setBorder(BorderFactory.createTitledBorder
				(BorderFactory.createLineBorder(Color.BLACK), buttonMenuTitle));
	}
	
	private void initializeSearchButton() {
		
		ImageIcon icon = ImageScaler.loadImageIcon
				(searchButtonIconPath, iconSize, iconSize);
		
		Action action = new AbstractAction() {

			private static final long serialVersionUID = 574278004862101935L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Hide the playlist panel if we are going to search.
				playlistaPanelLayout.show(playlistPanelContainer, invisibleTag);
				trigger(InterfaceEvent.SEARCH_PANEL_REQUEST);
			}
		};
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		
		searchButton = new JButton(searchButtonText, icon);
		searchButton.setHorizontalAlignment(SwingConstants.LEFT);
		searchButton.addActionListener(action);
		searchButton.getActionMap().put(searchButtonText, action);
		searchButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), searchButtonText);
		
		add(searchButton, constraints);
	}
	
	private void initializePlaylistManagementButton() {
		ImageIcon icon = ImageScaler.loadImageIcon
				(playlistManagementButtonIconPath, iconSize, iconSize);

		Action action = new AbstractAction() {

			private static final long serialVersionUID = -452089722081318813L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// If managing playlists, the playlists panel needs to be shown.
				playlistaPanelLayout.show(playlistPanelContainer, playlistsTag);
				trigger(InterfaceEvent.PLAYLIST_MNGMT_PANEL_REQUEST);
			}
		};
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		
		playlistManagementButton = new JButton
			(playlistManagementButtonText, icon);
		playlistManagementButton.setHorizontalAlignment(SwingConstants.LEFT);
		playlistManagementButton.addActionListener(action);
		playlistManagementButton.getActionMap().put
			(playlistManagementButtonText, action);
		playlistManagementButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
			playlistManagementButtonText);
		
		add(playlistManagementButton, constraints);
	}
	
	private void initializeRecentSongsButton() {
		
		ImageIcon icon = ImageScaler.loadImageIcon
				(recentSongsButtonIconPath, iconSize, iconSize);

		Action action = new AbstractAction() {

			private static final long serialVersionUID = -5640501624540705713L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Hide playlists panel if we requested recent songs panel.
				playlistaPanelLayout.show(playlistPanelContainer, invisibleTag);
				trigger(InterfaceEvent.RECENT_SONGS_PANEL_REQUEST);
			}
		};
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		
		recentSongsButton = new JButton(recentSongsButtonText, icon);
		recentSongsButton.setHorizontalAlignment(SwingConstants.LEFT);
		recentSongsButton.addActionListener(action);
		recentSongsButton.getActionMap().put(recentSongsButtonText, action);
		recentSongsButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
			recentSongsButtonText);
		
		add(recentSongsButton, constraints);
	}
	
	private void initializePlaylistsButton() {
		
		ImageIcon icon = ImageScaler.loadImageIcon
				(playlistsButtonIconPath, iconSize, iconSize);
		
		Action action = new AbstractAction() {

			private static final long serialVersionUID = 8682964382685045723L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Playlists panel show if playlists panel requested.
				playlistaPanelLayout.show(playlistPanelContainer, playlistsTag);
				trigger(InterfaceEvent.PLAYLISTS_PANEL_REQUEST);
			}
		};
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		
		playlistsButton = new JButton(playlistsButtonText, icon);
		playlistsButton.setHorizontalAlignment(SwingConstants.LEFT);
		playlistsButton.addActionListener(action);
		playlistsButton.getActionMap().put(playlistsButtonText, action);
		playlistsButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), playlistsButtonText);
		
		add(playlistsButton, constraints);
	}
	
	private void initializePlaylistsPanel() {
		GridBagConstraints constraints =  new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(35, 10, 10, 10);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_END;
		
		playlistaPanelLayout = new CardLayout();
		playlistPanelContainer = new JPanel(playlistaPanelLayout);
		playlistsPanel = new PlaylistsPanel();
		playlistPanelContainer.add(new JPanel(), invisibleTag);
		playlistPanelContainer.add(playlistsPanel, playlistsTag);
		
		add(playlistPanelContainer, constraints);
	}
	
	private void trigger(InterfaceEvent e) {
		// Emitir un evento para todos los listeners.
		listeners.forEach(l -> l.onEvent(e));
	}
	
	/**
	 * Añade un listener para los eventos producidos por este panel.
	 * 
	 * @param l Listener que se desea añadir.
	 */
	public void addInterfaceEventListener(InterfaceEventListener l) {
		listeners.add(l);
	}
	
	/**
	 * Elimina un listener de los eventos del panel.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removeInterfaceEventListener(InterfaceEventListener l) {
		listeners.remove(l);
	}
	
	// -------- Interacción con el controlador --------
	
	private void registerControllerListener() {
		// Escuchar eventos para establecer la interfaz de forma acorde.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener()
		{			
			@Override
			public void onLogout() {
				// Sólo ocultar el panel de playlists al cerrar sesión.
				playlistaPanelLayout
					.show(playlistPanelContainer, invisibleTag);
			}
		});
	}
}
