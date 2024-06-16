package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;

public class PlaylistInfoPanel extends JPanel {

	private static final long serialVersionUID = 9194288762248609924L;
	private static final String title = "Playlist";
	private static final String namePreText = "Nombre: ";
	private static final String placeHolder = "No seleccionado";
	private static final String descPreText = "Descripción.";
	private static final String deleteText = "Quitar canciones seleccionadas";
	
	private JLabel name;
	private ResponsiveTextArea desc;
	private PlaylistSongList songs;
	
	public PlaylistInfoPanel() {
		setLayout(new GridBagLayout());
		
		inititalizeName();
		initializeDescPre();
		initializeDesc();
		initializeSongs();
		initializeDeleteSongs();
		
		registerControllerListener();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
		
		StateManager.INSTANCE.setCallbackPlaylistInfo(this);
	}
	
	private void inititalizeName() {
		name = new JLabel(namePreText + placeHolder);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 5, 10);
		
		add(name, c);
	}
	
	private void initializeDescPre() {
		JLabel descPre = new JLabel(descPreText);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 10);
		
		add(descPre, c);
	}
	
	private void initializeDesc() {
		desc = new ResponsiveTextArea(descPreText);
		desc.setText(placeHolder);
		desc.setOpaque(false);
		desc.setEditable(false);
		desc.setForeground(Color.BLACK);
		desc.setRows(4);
		desc.setColumns(30);
		
		JScrollPane j = new JScrollPane(desc);
		j.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		j.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 0.3;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 10);
		
		add(j, c);
	}
	
	private void initializeSongs() {
		songs = new PlaylistSongList();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0.7;
		c.insets = new Insets(5, 10, 5, 10);
		
		add(songs, c);
	}
	
	private void initializeDeleteSongs() {
		ResponsiveButton deleteSongs = new ResponsiveButton(deleteText);
		deleteSongs.addActionListener(e -> deleteSongs());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 10, 10);
		
		add(deleteSongs, c);
	}
	
	private void deleteSongs() {
		StateManager.INSTANCE.removeSelectedSongsFromSelectedPlaylist();
	}
	
	public void setSelectedPlaylist(Playlist p) {
		name.setText(namePreText + p.getName());
		desc.setText(p.getDescription());
		songs.setSongList(p.getSongs());
	}
	
	public void clearSelectedPlaylist() {
		name.setText(namePreText + placeHolder);
		desc.setText(placeHolder);
		songs.setSongList(new ArrayList<>());
	}
	
	private void registerControllerListener() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogin(UserStatusEvent e) {
				clearSelectedPlaylist();
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				onUserLogin(e);
			}
		});
	}
}
