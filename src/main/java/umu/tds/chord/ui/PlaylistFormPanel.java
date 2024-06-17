package umu.tds.chord.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;

public class PlaylistFormPanel extends JPanel {

	private static final long serialVersionUID = 9136929470452018404L;
	private static final String templateName = "Nombre de la playlist";
	private static final String templateDesc = "Descripción de la playlist";
	private static final String submitText = "Crear playlist";
	private static final String updateText = "Actualizar playlist seleccionada";
	private static final String deleteText = "Eliminar playlist seleccionada";

	private TextField name;
	private ResponsiveTextArea desc;
	private ResponsiveButton submit;
	private ResponsiveButton update;
	private PlaylistFormVerifier verifier;

	public PlaylistFormPanel() {
		setLayout(new GridBagLayout());

		initializeName();
		initializeDesc();
		initializeSubmit();
		initializeUpdate();
		initializeDelete();

		initializeVerifier();

		registerContollerListeners();

		StateManager.INSTANCE.setCallbackPlaylistForm(this);
	}

	private void initializeName() {
		name = new TextField(templateName);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 5, 10);
		c.fill = GridBagConstraints.BOTH;

		add(name, c);
	}

	private void initializeDesc() {
		desc = new ResponsiveTextArea(templateDesc);
		desc.setColumns(30);
		desc.setRows(4);

		JScrollPane j = new JScrollPane(desc);
		j.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.BOTH;

		add(j, c);
	}

	private void initializeSubmit() {
		submit = new ResponsiveButton(submitText);
		submit.addActionListener(e -> createPlaylist());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.BOTH;

		add(submit, c);
	}

	private void initializeUpdate() {
		update = new ResponsiveButton(updateText);
		update.addActionListener(e -> updatePlaylist());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.BOTH;

		add(update, c);
	}

	private void initializeDelete() {
		ResponsiveButton delete = new ResponsiveButton(deleteText);
		delete.addActionListener(e -> deletePlaylist());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(5, 10, 10, 10);
		c.fill = GridBagConstraints.BOTH;

		add(delete, c);
	}

	private void initializeVerifier() {
		verifier = new PlaylistFormVerifier(submit, update);
		verifier.setNameField(name);
		verifier.setDescField(desc);
	}

	private void registerContollerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogout(UserStatusEvent e) {
				verifier.refresh();
			}

			@Override
			public void onUserLogin(UserStatusEvent e) {
				onUserLogout(e);
			}
		});
	}

	private void createPlaylist() {
		if (verifier.verify()) {
			String n = name.getText();
			String d = desc.getText();
			Controller.INSTANCE.createPlaylist(n, d);
		}
	}

	private void updatePlaylist() {
		if (verifier.verify()) {
			String n = name.getText();
			String d = desc.getText();
			StateManager.INSTANCE.updateSelectedPlaylist(n, d);
		}
	}

	private void deletePlaylist() {
		StateManager.INSTANCE.removeSelectedPlaylist();
	}

	protected void setSelectedPlaylist(Playlist p) {
		name.setText(p.getName());
		desc.setText(p.getDescription());
		verifier.verify();
	}
}
