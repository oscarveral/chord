package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Set;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusListener;

/**
 * Panel utilizado para la búsqueda y gestión de canciones.
 */
final public class SongSearchPanel extends JPanel {

	private static final long serialVersionUID = 2422691492816036797L;
	
	private static final String searchText = "Buscar";
	private static final String interpreterFilterText = "Intérprete";
	private static final String titleFilterText = "Título";
	private static final String favouriteText = "Favoritos";
	private static final String documentPropertyChangeErrorMsg = 
			"Unexpected change on properties for documment listener: ";
	private static final String emptyFilter = "";

	private JTextField titleFilter;
	private JTextField interpreterFilter;
	private JCheckBox favouriteFilter;
	private JComboBox<String> styleFilter;

	private boolean interpreterEmpty;
	private boolean titleEmpty;
	
	private JButton searchButton;
	
	private SearchResultsPanel resultsPanel;
	
	private String currentWildcardStyle;
	
	/**
	 * Constructor por defecto.
	 */
	public SongSearchPanel() {
		super();
		// Sólo se llama al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		GridBagLayout layout = new GridBagLayout();
		
		setLayout(layout);
		
		initializeTitleFilter();
		initializeInterpreterFilter();
		initializeFavouriteFilter();
		initializeStyleFilter();
		initializeSearchButton();
		initializeResultsPanel();
		
		registerControllerListeners();
	}
	
	private void initializeTitleFilter() {
		
		titleEmpty = true;
		titleFilter = new JTextField(titleFilterText);
		titleFilter.setForeground(Color.GRAY);
		
		// Hacer más bonita la interfaz.
		titleFilter.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (titleEmpty) {
					titleEmpty = false;
					titleFilter.setText(titleFilterText);
					titleFilter.setForeground(Color.GRAY);
					titleEmpty = true;
				}
				search();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (titleEmpty) {
					titleFilter.setText(null);
					titleFilter.setForeground(Color.BLACK);
				}
				search();
			}
		});
		titleFilter.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (titleFilter.getText().isEmpty()) {
					titleEmpty = true;
				}
				search();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (titleEmpty) {
					titleEmpty = false;
				}
				search();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException
					(documentPropertyChangeErrorMsg + titleFilterText);
			}
		});
		
		titleFilter.addActionListener(e -> search());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 10, 0, 5);
		
		add(titleFilter, constraints);
	}
	
	private void initializeInterpreterFilter() {
		
		interpreterEmpty = true;
		interpreterFilter = new JTextField(interpreterFilterText);
		interpreterFilter.setForeground(Color.GRAY);
		
		// Listeners para hacer más bonita la interfaz.
		interpreterFilter.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (interpreterEmpty) {
					interpreterEmpty = false;
					interpreterFilter.setText(interpreterFilterText);
					interpreterFilter.setForeground(Color.GRAY);
					interpreterEmpty = true;
				}
				search();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (interpreterEmpty) {
					interpreterFilter.setText(null);
					interpreterFilter.setForeground(Color.BLACK);
				}
				search();
			}
		});
		interpreterFilter.getDocument().addDocumentListener
		(
			new DocumentListener() {
			
				@Override
				public void removeUpdate(DocumentEvent e) {
					if (interpreterFilter.getText().isEmpty()) {
						interpreterEmpty = true;
					}
					search();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					if (interpreterEmpty) {
						interpreterEmpty = false;
					}
					search();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					throw new RuntimeException
						(documentPropertyChangeErrorMsg + 
						interpreterFilterText);
				}
			}
		);
		
		interpreterFilter.addActionListener(e -> search());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 0, 10);
		
		add(interpreterFilter, constraints);	
	}
	
	private void initializeFavouriteFilter() {
		
		Action action = new AbstractAction() {

			private static final long serialVersionUID = -1530861938199922275L;

			@Override
			public void actionPerformed(ActionEvent e) {
				favouriteFilter.setSelected(!favouriteFilter.isSelected());	
				search();
			}
		};

		favouriteFilter = new JCheckBox(favouriteText);
		favouriteFilter.getActionMap().put(favouriteText, action);
		favouriteFilter.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), favouriteText);
		
		favouriteFilter.addActionListener(e -> search());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 10, 0, 5);
		
		add(favouriteFilter, constraints);
	}
	
	private void initializeStyleFilter() {
		
		styleFilter = new JComboBox<String>();
		
		currentWildcardStyle = emptyFilter;
		
		styleFilter.addActionListener(e -> search());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 5, 0, 10);
		
		add(styleFilter, constraints);
	}
	
	private void initializeSearchButton() {
		
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 6383232080564074692L;

			@Override
			public void actionPerformed(ActionEvent e) {
				search();
			}
		};
		
		searchButton = new JButton(searchText);
		searchButton.addActionListener(action);
		searchButton.getActionMap().put(searchText, action); 
		searchButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), searchText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 10, 5, 10);
		
		add(searchButton, constraints);
	}
	
	private void initializeResultsPanel() {
		
		resultsPanel = new SearchResultsPanel();

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_END;

		add(resultsPanel, constraints);
	}
	
	// -------- Interacción con el controlador --------
	
	private void registerControllerListeners() {
		// Escuchar eventos para establecer la interfaz de forma acorde.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener()
		{
			// Resetear filtros de búsqueda.
			@Override
			public void onLogout() {
				interpreterFilter.setText(interpreterFilterText);
				interpreterFilter.setForeground(Color.GRAY);
				interpreterEmpty = true;
				
				titleFilter.setText(titleFilterText);
				titleFilter.setForeground(Color.GRAY);
				titleEmpty = true;
				
				favouriteFilter.setSelected(false);

				styleFilter.setSelectedItem(currentWildcardStyle);
			}
		});
				
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
		
			// Establecer las opciones del filtro de estilo.
			@Override
			public void onStyleList(Set<String> styles, String wildcard) {
				
				currentWildcardStyle = wildcard;
		
				// Resetear el filtro de búsqueda.
				styleFilter.removeAllItems();
				styles.forEach( s-> styleFilter.addItem(s));
				styleFilter.setSelectedItem(currentWildcardStyle);
			}
		});
	}
	
	private void search() {
		// Recuperación de filtros;
		String name = emptyFilter;
		if (!titleEmpty) name = titleFilter.getText();
		
		String author = emptyFilter;
		if (!interpreterEmpty) author = interpreterFilter.getText();
		
		String style = (String) styleFilter.getSelectedItem();
	
		boolean favourite = favouriteFilter.isSelected();
		
		// Búsqueda.
		Controller.INSTANCE.searchSongs(name, author, favourite, style);
	}
}
