package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.ui.StateManager.UIEvents;
import umu.tds.chord.utils.ImageScaler;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 7852940161121222281L;
	private static final int iconSize = 20;
	private static final String buttonMenuTitle = "Música";
	private static final String searchButtonText = "Buscar";
	private static final String playlistManagementButtonText = "Gestionar Playlists";
	private static final String recentSongsButtonText = "Canciones Recientes";
	private static final String playlistsButtonText = "Mis Playlists";
	private static final String searchButtonIconPath = "/images/search_button_icon.png";
	private static final String playlistManagementButtonIconPath = "/images/playlist_edit_icon.png";
	private static final String recentSongsButtonIconPath = "/images/recent_songs_icon.png";
	private static final String playlistsButtonIconPath = "/images/playlist_icon.png";
	private static final String masReproducidasText = "Canciones más reproducidas";
	private static final String masReproducidasIconPath = "/images/most.png";
	
	private ResponsiveButton masReproducidas;
	
	public ButtonPanel() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		initializeSearchButton();
		initializePlaylistManagementButton();
		initializeRecentSongsButton();
		initializePlaylistsButton();
		initializePlaylistsPanel();
		initializeMasReproducidas();
		
		registerControllerListener();
		
		setBorder(BorderFactory.createTitledBorder
				(BorderFactory.createLineBorder(Color.BLACK), buttonMenuTitle));
	}
	
	// ---------- Interfaz. ----------
	
	private void initializeSearchButton() {

		ImageIcon icon = ImageScaler.loadImageIcon(searchButtonIconPath, iconSize, iconSize);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;

		ResponsiveButton searchButton = new ResponsiveButton(searchButtonText, icon);
		searchButton.setHorizontalAlignment(SwingConstants.LEFT);
		searchButton.addActionListener(e -> StateManager.INSTANCE.triggerEvent(UIEvents.SONG_SEARCH));

		add(searchButton, constraints);
	}

	private void initializePlaylistManagementButton() {
		ImageIcon icon = ImageScaler.loadImageIcon(playlistManagementButtonIconPath, iconSize, iconSize);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;

		ResponsiveButton playlistManagementButton = new ResponsiveButton(playlistManagementButtonText, icon);
		playlistManagementButton.setHorizontalAlignment(SwingConstants.LEFT);
		playlistManagementButton.addActionListener(e -> StateManager.INSTANCE.triggerEvent(UIEvents.PLAYLSITS_MNGMT));

		add(playlistManagementButton, constraints);
	}

	private void initializeRecentSongsButton() {
		ImageIcon icon = ImageScaler.loadImageIcon(recentSongsButtonIconPath, iconSize, iconSize);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;

		ResponsiveButton recentSongsButton = new ResponsiveButton(recentSongsButtonText, icon);
		recentSongsButton.setHorizontalAlignment(SwingConstants.LEFT);
		recentSongsButton.addActionListener(e -> StateManager.INSTANCE.triggerEvent(UIEvents.RECENT_SONGS));

		add(recentSongsButton, constraints);
	}

	private void initializePlaylistsButton() {
		ImageIcon icon = ImageScaler.loadImageIcon(playlistsButtonIconPath, iconSize, iconSize);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;

		ResponsiveButton playlistsButton = new ResponsiveButton(playlistsButtonText, icon);
		playlistsButton.setHorizontalAlignment(SwingConstants.LEFT);
		playlistsButton.addActionListener(e -> StateManager.INSTANCE.triggerEvent(UIEvents.PLAYLIST));

		add(playlistsButton, constraints);
	}

	private void initializePlaylistsPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(35, 10, 10, 10);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_END;

		PlaylistPanel playlistsPanel = new PlaylistPanel();

		add(playlistsPanel, constraints);
	}
	
	private void initializeMasReproducidas() {
		ImageIcon icon = ImageScaler.loadImageIcon(masReproducidasIconPath, iconSize, iconSize);

		masReproducidas = new ResponsiveButton(masReproducidasText, icon);
		masReproducidas.addActionListener(e -> StateManager.INSTANCE.triggerEvent(UIEvents.MAS_REPRODUCIDAS));
		
		GridBagConstraints masReproducidasC = new GridBagConstraints();
		masReproducidasC.gridx = 0;
		masReproducidasC.gridy = 4;
		masReproducidasC.insets = new Insets(0, 10, 0, 10);
		masReproducidasC.fill = GridBagConstraints.HORIZONTAL;
		masReproducidasC.anchor = GridBagConstraints.PAGE_START;
		
		add(masReproducidas, masReproducidasC);
	}
	
	private void registerControllerListener() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
		
			@Override
			public void onUserLogin(UserStatusEvent e) {
				e.getUser().ifPresent(u -> {
					masReproducidas.setEnabled(false);
					masReproducidas.setFocusable(false);
					if (u.isPremium()) {
						masReproducidas.setEnabled(true);
						masReproducidas.setFocusable(true);
					}
				});
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				onUserLogin(e);
			}
			
			@Override
			public void onUserMetadataChange(UserStatusEvent e) {
				onUserLogin(e);
			}
		});
	}
}
