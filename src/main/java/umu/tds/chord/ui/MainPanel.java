package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = -6647430035952199870L;
	private static final String invisibleTag = "invisible";
	private static final String searchTag = "search";
	private static final String mngmntTag=  "mngmt";
	private static final String recentSongsTag = "recent";
	private static final String playlistInfoTag = "pinfo";
 
	private SongLoaderButton luz;
	
	private UserInfoPanel userInfo;
	private ButtonPanel buttons;
	
	private JPanel centerContainer;
	private CardLayout centerLayout;
	private SearchContainer searchPanel;
	private PlaylistManagementPanel playlistMngmt;
	private RecentSongsPanel recentSongsPanel;
	private PlaylistInfoPanel playlistInfo;
	private ReproductionPanel reproduction;
	
	public MainPanel() {	
		BorderLayout layout = new BorderLayout();
		
		layout.setHgap(10);
		setLayout(layout);
		
		initializeCenterContainer();
		initializeButtons();
		initializeNorth();
		initializeReproduction();
		registerControllerListener();
								
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		StateManager.INSTANCE.setCallbackMainPanel(this);
	}
	
	private void initializeCenterContainer() {
		searchPanel = new SearchContainer();
		playlistMngmt = new PlaylistManagementPanel();
		recentSongsPanel = new RecentSongsPanel();
		playlistInfo = new PlaylistInfoPanel();
		
		centerLayout = new CardLayout();
		centerContainer = new JPanel(centerLayout);
		centerContainer.setFocusable(true);
		centerContainer.add(new JPanel(), invisibleTag);
		centerContainer.add(searchPanel, searchTag);
		centerContainer.add(playlistMngmt, mngmntTag);
		centerContainer.add(recentSongsPanel, recentSongsTag);
		centerContainer.add(playlistInfo, playlistInfoTag);
		centerLayout.show(centerContainer, invisibleTag);
		
		add(centerContainer, BorderLayout.CENTER);
	}
	
	private void initializeButtons() {
		buttons = new ButtonPanel();
		add(buttons, BorderLayout.LINE_START);
	}
	
	private void initializeNorth() {
		userInfo = new UserInfoPanel();
		luz = new SongLoaderButton();
		
		JPanel north = new JPanel(new GridBagLayout());
	
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		c1.weightx = 1.0;
		c1.weighty = 1.0;
		c1.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 1;
		c2.fill= GridBagConstraints.BOTH;
		c2.insets = new Insets(10, 10, 10, 5);
		
		north.add(userInfo, c1);
		north.add(luz, c2);
		
		add(north, BorderLayout.PAGE_START);
	}
	
	public void showSearch() {
		centerLayout.show(centerContainer, searchTag);
	}
	
	public void showPlaylistsMngmt() {
		centerLayout.show(centerContainer, mngmntTag);
	}
	
	public void showRecentSongs() {
		centerLayout.show(centerContainer, recentSongsTag);
	}
	
	public void showPlaylist() {
		centerLayout.show(centerContainer, playlistInfoTag);
	}
	
	private void initializeReproduction() {
		reproduction = new ReproductionPanel();
		add(reproduction, BorderLayout.SOUTH);
	}
	
	private void registerControllerListener() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogout(UserStatusEvent e) {
				centerLayout.show(centerContainer, invisibleTag);
			}
		});
	}
}