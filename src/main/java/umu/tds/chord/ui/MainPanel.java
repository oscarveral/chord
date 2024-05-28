package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = -6647430035952199870L;
	private static final String invisibleTag = "invisible";
	private static final String searchTag = "search";
	private static final String recentSongsTab = "rececent";

	private SongLoaderButton luz;
	
	private UserInfoPanel userInfo;
	private ButtonPanel buttons;
	
	private JPanel centerContainer;
	private CardLayout centerLayout;
	private SearchContainer searchPanel;
	private RecentSongsPanel recentSongsPanel;
	
	public MainPanel() {	
		BorderLayout layout = new BorderLayout();
		
		layout.setHgap(10);
		setLayout(layout);
		
		initializeCenterContainer();
		initializeButtons();
		initializeNorth();
						
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		StateManager.INSTANCE.setCallbackMainPanel(this);
	}
	
	private void initializeCenterContainer() {
		searchPanel = new SearchContainer();
		recentSongsPanel = new RecentSongsPanel();
		
		centerLayout = new CardLayout();
		centerContainer = new JPanel(centerLayout);
		centerContainer.setFocusable(true);
		centerContainer.add(new JPanel(), invisibleTag);
		centerContainer.add(searchPanel, searchTag);
		centerContainer.add(recentSongsPanel, recentSongsTab);
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
		c1.gridy = 0;
		c1.weightx = 1.0;
		c1.weighty = 1.0;
		c1.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
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
		centerLayout.show(centerContainer, invisibleTag);
	}
	
	public void showRecentSongs() {
		centerLayout.show(centerContainer, recentSongsTab);
	}
	
	public void showPlaylist() {
		centerLayout.show(centerContainer, invisibleTag);
	}
}