package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.model.Song;

/**
 * Panel conenedor de búsqueda de canciones utilizado para la gestión de 
 * canciones y decoración de la interfaz.
 */
public final class SearchContainerPanel extends JPanel {
	
	private static final long serialVersionUID = -8607940445694235286L;

	private static final String panelTitle = "Buscar";
	private static final String deleteText = "Eliminar canción";
	private static final String passInputText = "Introduce tu contraseña";
	private static final String deleteConfirmTitle = 
			"Permiso requerido";
	private static final String confirmDelete = "Confirmar eliminación";
	private static final String emptyText = "";
	private static final String failText = "No disponible";
	
	private SongSearchPanel searchPanel;
	
	private JPanel buttonsPanel;
	private JButton deleteButton;
	
	private JDialog deleteDialog;
	private JPanel panel;
	private JLabel label;
	private JPasswordField passInput;
	private JButton confirm;
	private JLabel failLabel;
	
	/**
	 * Constructor por defecto.
	 */
	public SearchContainerPanel() {
		super();
		// Se llama sólo al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		initializeSearchPanel();
		initializeButtons();
		
		registerControllerListeners();
		
		setBorder(BorderFactory.createTitledBorder
				(BorderFactory.createLineBorder(Color.BLACK), panelTitle));
	}
	
	private void initializeSearchPanel() {
		searchPanel = new SongSearchPanel();
		add(searchPanel, BorderLayout.CENTER);
	}
	
	private void initializeButtons() {
		GridBagLayout layout = new GridBagLayout();
		buttonsPanel = new JPanel(layout);
		
		initializeDeleteButton();
		
		add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	private void initializeDeleteButton() {
		deleteButton = new JButton(deleteText);
		
		initializeDeleteDialog();
		
		deleteButton.addActionListener(e -> {
			deleteDialog.setLocationRelativeTo
				(SwingUtilities.getWindowAncestor(this));
			deleteDialog.setVisible(true);
		});
	
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.BOTH;
		
		buttonsPanel.add(deleteButton, constraints);
	}
	
	private void initializeDeleteDialog() {
		
		deleteDialog = new JDialog(
				(JFrame)SwingUtilities.getWindowAncestor(this), true);
		
		panel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		
		panel.addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				passInput.setText(emptyText);
				failLabel.setText(emptyText);
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {}
		});
		
		label = new JLabel(passInputText);
		passInput = new JPasswordField();
		confirm = new JButton(confirmDelete);
		failLabel = new JLabel();
		
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 73781540435399034L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Obtener contraseña y eliminar.
				char[] p = passInput.getPassword();
				deleteSelectedSongs(new String(p));
			}
		};
		
		passInput.addActionListener(action);
		confirm.addActionListener(action);
		
		confirm.addActionListener(action);
		confirm.getActionMap().put(confirmDelete, action);
		confirm.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), confirmDelete);
		
		failLabel.setForeground(Color.RED);
		
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
		confirmConstraints.insets = new Insets(5, 10, 0, 10);
		
		GridBagConstraints failConstraints = new GridBagConstraints();
		failConstraints.gridx = 0;
		failConstraints.gridy = 3;
		failConstraints.fill = GridBagConstraints.HORIZONTAL;
		failConstraints.insets = new Insets(5, 10, 15, 10);
		
		panel.add(label, labelConstraints);
		panel.add(passInput, passConstraints);
		panel.add(confirm, confirmConstraints);
		panel.add(failLabel, failConstraints);
				
		deleteDialog.setAlwaysOnTop(true);				
		deleteDialog.setTitle(deleteConfirmTitle);
		deleteDialog.setContentPane(panel);
		deleteDialog.pack();
		deleteDialog.setResizable(false);
		deleteDialog.setVisible(false);	
	}
	
	// -------- Interacción con el controlador --------
	
	private void deleteSelectedSongs(String password) {
		Controller.INSTANCE.removeSelectedSongs(password);
	}
	
	private void registerControllerListeners() {
		// Escuchar fallos y éxitos de eliminación.
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() 
		{
			@Override
			public void onSongDeleteFailure() {
				failLabel.setText(failText);
			}
			
			@Override
			public void onSongList(Set<Song> songs) {
				// Si hay eliminación exitosa cambia la lista de canciones.
				passInput.setText(emptyText);
				deleteDialog.setVisible(false);
			}
		});
	}
}
