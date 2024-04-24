package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Panel conenedor de búsqueda de canciones utilizado para la gestión de 
 * canciones y decoración de la interfaz.
 */
public final class SearchContainerPanel extends JPanel {
	
	private static final long serialVersionUID = -8607940445694235286L;

	private static final String panelTitle = "Buscar";
	private static final String deleteText = "Eliminar canción";
	
	private SongSearchPanel searchPanel;
	
	private JPanel buttonsPanel;
	private JButton deleteButton;
	
	public SearchContainerPanel() {
		
		initialize();
	}
	
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		initializeSearchPanel();
		initializeButtons();
		
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
	
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.insets = new Insets(0, 10, 10, 10);
		constraints.fill = GridBagConstraints.BOTH;
		
		buttonsPanel.add(deleteButton, constraints);
	}
}
