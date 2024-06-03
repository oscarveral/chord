package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;

public class SearchContainer extends JPanel {

	private static final long serialVersionUID = -8607940445694235286L;

	private static final String panelTitle = "Buscar";
	private static final String deleteText = "Eliminar canción";
	private static final String passInputText = "Introduce tu contraseña";
	private static final String deleteConfirmTitle = "Permiso requerido";
	private static final String confirmDelete = "Confirmar eliminación";
	private static final String addToPlaylistText = "Añadir canciones seleccionadas a la playlist seleccionada";

	private SearchFormPanel searchPanel;
	private JPanel buttonsPanel;
	private ResponsiveButton deleteButton;
	private JDialog deleteDialog;
	private JPanel panel;
	private JLabel label;
	private PasswordField passInput;
	private ResponsiveButton confirm;
	private ResponsiveButton addToPlaylist;

	public SearchContainer() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		initializeSearchPanel();
		initializeButtons();
		initializeAddToPlaylist();

		registerControllerListeners();

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), panelTitle));
	}

	private void initializeSearchPanel() {
		searchPanel = new SearchFormPanel();
		add(searchPanel, BorderLayout.CENTER);
	}

	private void initializeButtons() {
		GridBagLayout layout = new GridBagLayout();
		buttonsPanel = new JPanel(layout);

		initializeDeleteButton();

		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void initializeDeleteButton() {
		deleteButton = new ResponsiveButton(deleteText);

		initializeDeleteDialog();

		deleteButton.addActionListener(e -> {
			deleteDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
			deleteDialog.setVisible(true);
		});

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.insets = new Insets(0, 10, 5, 10);
		constraints.fill = GridBagConstraints.BOTH;

		buttonsPanel.add(deleteButton, constraints);
	}

	private void initializeDeleteDialog() {

		deleteDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);

		panel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		panel.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				passInput.reset();
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
			}
		});

		label = new JLabel(passInputText);
		passInput = new PasswordField(passInputText);
		confirm = new ResponsiveButton(confirmDelete);

		Action action = new AbstractAction() {

			private static final long serialVersionUID = 7582651531298538187L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Obtener contraseña y eliminar.
				char[] p = passInput.getPassword();
				deleteSelectedSongs(new String(p));
			}
		};

		passInput.addActionListener(action);
		confirm.addActionListener(action);

		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx = 0;
		labelConstraints.gridy = 0;
		labelConstraints.fill = GridBagConstraints.BOTH;
		labelConstraints.insets = new Insets(10, 10, 5, 10);

		GridBagConstraints passConstraints = new GridBagConstraints();
		passConstraints.gridx = 0;
		passConstraints.gridy = 1;
		passConstraints.fill = GridBagConstraints.BOTH;
		passConstraints.insets = new Insets(5, 10, 5, 10);

		GridBagConstraints confirmConstraints = new GridBagConstraints();
		confirmConstraints.gridx = 0;
		confirmConstraints.gridy = 2;
		confirmConstraints.fill = GridBagConstraints.BOTH;
		confirmConstraints.insets = new Insets(5, 10, 10, 10);

		panel.add(label, labelConstraints);
		panel.add(passInput, passConstraints);
		panel.add(confirm, confirmConstraints);

		deleteDialog.setTitle(deleteConfirmTitle);
		deleteDialog.setContentPane(panel);
		deleteDialog.pack();
		deleteDialog.setResizable(false);
		deleteDialog.setVisible(false);
	}
	
	private void initializeAddToPlaylist() {
		addToPlaylist = new ResponsiveButton(addToPlaylistText);
		addToPlaylist.addActionListener(e -> addSongs());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(5, 10, 10, 10);
		constraints.fill = GridBagConstraints.BOTH;
		
		buttonsPanel.add(addToPlaylist, constraints);
	}

	private void registerControllerListeners() {
		// Escuchar fallos y éxitos de eliminación.
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
			@Override
			public void onSongDelete(SongStatusEvent e) {
				if (!e.isFailed()) deleteDialog.setVisible(false);
				passInput.reset();
			}
		});
	}
	
	private void deleteSelectedSongs(String password) {
		if (!passInput.isEmpty())
			StateManager.INSTANCE.removeSelectedSongs(password);
		passInput.reset();
	}
	
	private void addSongs() {
		StateManager.INSTANCE.addSelectedSongsToSelectedPlaylist();
	}
}
