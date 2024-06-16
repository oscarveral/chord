package umu.tds.chord.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.SongRepository;

public class SearchFormPanel extends JPanel {

	private static final long serialVersionUID = 815457888482089882L;
	private static final String searchText = "Buscar";
	private static final String interpreterFilterText = "Intérprete";
	private static final String titleFilterText = "Título";
	private static final String favouriteText = "Favoritos";

	private TextField titleFilter;
	private TextField interpreterFilter;
	private ResponsiveCheckBox favouriteFilter;
	private JComboBox<String> styleFilter;
	
	public SearchFormPanel() {
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
		titleFilter = new TextField(titleFilterText);		
		titleFilter.addActionListener(e -> search());
		titleFilter.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				search();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 10, 0, 5);
		
		add(titleFilter, constraints);
	}

	private void initializeInterpreterFilter() {
		interpreterFilter = new TextField(interpreterFilterText);
		interpreterFilter.addActionListener(e -> search());
		interpreterFilter.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				search();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 0, 10);
		
		add(interpreterFilter, constraints);	
	}
	
	private void initializeFavouriteFilter() {
		favouriteFilter = new ResponsiveCheckBox(favouriteText);
		favouriteFilter.addActionListener(e -> search());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 10, 0, 5);
		
		add(favouriteFilter, constraints);
	}
	
	private void initializeStyleFilter() {
		
		styleFilter = new JComboBox<>();		
		styleFilter.addActionListener(e -> search());
		styleFilter.addItem(SongRepository.ALL_STYLES);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 5, 0, 10);
		
		add(styleFilter, constraints);
	}
	
	private void initializeSearchButton() {
		ResponsiveButton searchButton = new ResponsiveButton(searchText);
		searchButton.addActionListener(e -> search());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 10, 5, 10);
		
		add(searchButton, constraints);
	}
	
	private void initializeResultsPanel() {
		SearchResultPanel resultsPanel = new SearchResultPanel();

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

		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			
			@Override
			public void onUserLogin(UserStatusEvent e) {
				titleFilter.reset();
				interpreterFilter.reset();
				favouriteFilter.setSelected(false);
				styleFilter.setSelectedItem(SongRepository.ALL_STYLES);
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				onUserLogin(e);
			}
			
			@Override
			public void onFavouriteSongsUpdate(UserStatusEvent e) {
				// Más dinámmico actualizar búsquedas en cambios de favoritos.
				search();
			}
		});
				
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {

			@Override
			public void onSongLoad(SongStatusEvent e) {
				if (e.isFailed()) return;
				e.getSongs().forEach(s -> styleFilter.addItem(s.getStyle()));
			}
						
			@Override
			public void onSongDelete(SongStatusEvent e) {
				if (e.isFailed()) return;
				e.getSongs().forEach(s -> styleFilter.removeItem(s.getStyle()));
				styleFilter.setSelectedItem(SongRepository.ALL_STYLES);
			}
		});
	}
	
	private void search() {
		Optional<String> name = Optional.empty();
		if (!titleFilter.isEmpty()) name = Optional.ofNullable(titleFilter.getText());
		Optional<String> author = Optional.empty();
		if (!interpreterFilter.isEmpty()) author = Optional.ofNullable(interpreterFilter.getText());
		Optional<String> style = Optional.ofNullable((String) styleFilter.getSelectedItem());
		boolean favourite = favouriteFilter.isSelected();
		
		Controller.INSTANCE.searchSongs(name, author, style, favourite);
	}
}
