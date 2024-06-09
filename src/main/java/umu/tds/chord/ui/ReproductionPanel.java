package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.PlayStatusListener;
import umu.tds.chord.controller.Player;
import umu.tds.chord.controller.SongPlayEvent;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.utils.ImageScaler;

public class ReproductionPanel extends JPanel {
	
	private static final long serialVersionUID = -7414105266109756758L;
	private static final String addColaIconPath = "/images/queue.png";
	private static final String reproPlaylistIconPath = "/images/playlist.png";
	private static final String playIconPath = "/images/play.png";
	private static final String pauseIconPath = "/images/pause.png";
	private static final String stopIconPath = "/images/stop.png";
	private static final String nextIconPath = "/images/next.png";
	private static final String lastIconPath = "/images/previous.png";
	private static final String currentSongTemplate = "No se está reproduciendo nada";
	private static final String title = "Reproductor";
	private static final int iconSize = 20;
	
	private ResponsiveButton addCola;
	private ResponsiveButton reproPalylist;
	private ResponsiveButton play;
	private ResponsiveButton pause;
	private ResponsiveButton stop;
	private ResponsiveButton next;
	private ResponsiveButton last;
	private JLabel currentSong;
	private JProgressBar progreso;
	
	private JPanel container;
	private JPanel center;
	
	public ReproductionPanel() {
	
		setLayout(new GridBagLayout());
		
		container = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(container, c);
	
		container.setLayout(new BorderLayout());
		
		initializeWest();
		initializeEast();
		initializeCenter();
		
		registerControllerListener();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
		
	}
	
	private void initializeWest() {
		Icon i = ImageScaler.loadImageIcon(addColaIconPath, iconSize, iconSize);
		addCola = new ResponsiveButton(i);
		addCola.addActionListener(e -> StateManager.INSTANCE.addSelelectedSongsToQueue());
		
		JPanel co = new JPanel();
		co.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.insets = new Insets(5, 5, 10, 0);
		
		co.add(addCola, c);
		
		container.add(co, BorderLayout.WEST);
	}
	
	private void initializeEast() {
		Icon i = ImageScaler.loadImageIcon(reproPlaylistIconPath, iconSize, iconSize);
		reproPalylist = new ResponsiveButton(i);
		reproPalylist.addActionListener(e -> StateManager.INSTANCE.reproduceSelectedPlaylist());
		
		JPanel co = new JPanel();
		co.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.insets = new Insets(5, 0, 10, 5);
		
		co.add(reproPalylist, c);
		
		container.add(co, BorderLayout.EAST);
	}
	
	private void initializeCenter() {
		center = new JPanel();
		center.setLayout(new GridBagLayout());
		
		initializePlay();
		initializePause();
		initializeStop();
		initializeNext();
		initializeLast();
		initializeCurrentSong();
		initializeProgreso();
		
		container.add(center, BorderLayout.CENTER);
	}
	
	private void initializePlay() {
		Icon i = ImageScaler.loadImageIcon(playIconPath, iconSize, iconSize);
		play = new ResponsiveButton(i);
		play.addActionListener(e -> Player.INSTANCE.reanudar());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(play, c);
	}
	
	private void initializePause() {
		Icon i = ImageScaler.loadImageIcon(pauseIconPath, iconSize, iconSize);
		pause = new ResponsiveButton(i);
		pause.addActionListener(e -> Player.INSTANCE.pausar());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(pause, c);
	}
	
	private void initializeStop() {
		Icon i = ImageScaler.loadImageIcon(stopIconPath, iconSize, iconSize);
		stop = new ResponsiveButton(i);
		stop.addActionListener(e -> Player.INSTANCE.stop());
	
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(stop, c);
	}
	
	private void initializeNext() {
		Icon i = ImageScaler.loadImageIcon(nextIconPath, iconSize, iconSize);
		next = new ResponsiveButton(i);
		next.addActionListener(e -> Player.INSTANCE.siguiente());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(next, c);
	}
	
	private void initializeLast() {
		Icon i = ImageScaler.loadImageIcon(lastIconPath, iconSize, iconSize);
		last = new ResponsiveButton(i);
		last.addActionListener(e -> Player.INSTANCE.anterior());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(last, c);
	}
	
	private void initializeCurrentSong() {
		currentSong = new JLabel(currentSongTemplate);
	
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 5;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;
		
		center.add(currentSong, c);
	}
	
	private void initializeProgreso() {
		progreso = new JProgressBar();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 5;
		c.insets = new Insets(5, 0, 10, 0);
		c.fill = GridBagConstraints.BOTH;
		
		center.add(progreso, c);
	}
	
	private void registerControllerListener() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
		
			@Override
			public void onUserLogin(UserStatusEvent e) {
				currentSong.setText(currentSongTemplate);
				progreso.setValue(0);
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				onUserLogin(e);
			}
		});
		
		Player.INSTANCE.registerPlayStatusListener(new PlayStatusListener() {
		
			@Override
			public void onSongReproduction(SongPlayEvent e) {
				currentSong.setText(currentSongTemplate);
				e.getSong().ifPresent(s -> {
					currentSong.setText(s.getName() + " - " + s.getAuthor() + " - " + s.getStyle());
				});
				
			}
		});
	}
}
