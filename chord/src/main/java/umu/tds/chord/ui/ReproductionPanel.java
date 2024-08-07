package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.PlayerStatusListener;
import umu.tds.chord.controller.Player;
import umu.tds.chord.controller.PlayerStatusEvent;
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
	private static final String randomIconPath = "/images/random.png";
	private static final String currentSongTemplate = "No se está reproduciendo nada";
	private static final String currentPlaylistTemplate = "No se está reproduciendo de ninguna playlist.";
	private static final String title = "Reproductor";
	private static final int iconSize = 20;
	private static final int progressScaleFactor = 10000;

	private ResponsiveToggleButton random;
	private JLabel currentSong;
	private JLabel currentPlaylist;
	private JPanel container;
	private JPanel center;
	private JSlider progress;

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
		ResponsiveButton addCola = new ResponsiveButton(i);
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
		ResponsiveButton reproPalylist = new ResponsiveButton(i);
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
		initializeRandom();
		initializeCurrentSong();
		initializeProgress();

		container.add(center, BorderLayout.CENTER);
	}

	private void initializePlay() {
		Icon i = ImageScaler.loadImageIcon(playIconPath, iconSize, iconSize);
		ResponsiveButton play = new ResponsiveButton(i);
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
		ResponsiveButton pause = new ResponsiveButton(i);
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
		ResponsiveButton stop = new ResponsiveButton(i);
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
		ResponsiveButton next = new ResponsiveButton(i);
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
		ResponsiveButton last = new ResponsiveButton(i);
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
		currentPlaylist = new JLabel(currentPlaylistTemplate);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 6;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		GridBagConstraints d = new GridBagConstraints();
		d.gridx = 0;
		d.gridy = 1;
		d.gridwidth = 6;
		d.insets = new Insets(5, 0, 5, 0);
		d.fill = GridBagConstraints.BOTH;

		center.add(currentSong, c);
		center.add(currentPlaylist, d);
	}

	private void initializeRandom() {
		Icon i = ImageScaler.loadImageIcon(randomIconPath, iconSize, iconSize);
		random = new ResponsiveToggleButton(i);
		random.addActionListener(e -> Player.INSTANCE.setRandomMode(random.isSelected()));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(random, c);
	}

	private void initializeProgress() {
		progress = new JSlider();
		progress.setValue(0);
		progress.setMaximum(progressScaleFactor);
		progress.setMinimum(0);
		progress.addMouseListener(new MouseListener() {

			// private boolean shouldUpdate = false;

			@Override
			public void mouseReleased(MouseEvent e) {
				Player.INSTANCE.setReproductionProgress(progress.getValue() / (double) progress.getMaximum());
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 6;
		c.insets = new Insets(5, 0, 10, 0);
		c.fill = GridBagConstraints.BOTH;

		center.add(progress, c);
	}

	private void registerControllerListener() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {

			@Override
			public void onUserLogin(UserStatusEvent e) {
				currentSong.setText(currentSongTemplate);
				currentPlaylist.setText(currentPlaylistTemplate);
				random.setSelected(false);
			}

			@Override
			public void onUserLogout(UserStatusEvent e) {
				onUserLogin(e);
			}
		});

		Player.INSTANCE.registerPlayStatusListener(new PlayerStatusListener() {

			@Override
			public void onSongReproduction(PlayerStatusEvent e) {
				currentSong.setText(currentSongTemplate);
				e.getSong().ifPresent(
						s -> currentSong.setText(s.getName() + " - " + s.getAuthor() + " - " + s.getStyle()));
				e.getPlaylist().ifPresent(p -> currentPlaylist.setText("Playlist: " + p.getName()));
				progress.setValue((int) (progressScaleFactor * e.getProgress()));
			}

			@Override
			public void onSongProgress(PlayerStatusEvent e) {
				if (!progress.getValueIsAdjusting())
					progress.setValue((int) (progressScaleFactor * e.getProgress()));
			}
		});
	}
}
