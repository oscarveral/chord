package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.User;

/**
 * Panel que muestra la tabla de canciones y búsquedas sobre la mismas.
 * También permite eliminar canciones del repositorio.
 */
public final class ResultsPanel extends JPanel{
		
	private static final long serialVersionUID = 5228359293226694415L;

	private static final String deleteButtonText = "Eliminar canción";
	
	private SongTableModel datos;
	private JTable songTable;
	
	private Set<Song> currentFavourites;
	
	/**
	 * Constructor por defecto.
	 */
	public ResultsPanel() {
		super();
		// Sólo se llama al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		layout.setVgap(10);
		
		setLayout(layout);
		
		initializeSongTable();
		initializeDeleteButton();
		
		registerControllerListeners();
	}
	
	private void initializeSongTable() {
		// Creamos el modelo de tabla y la tabla final.
		datos= new SongTableModel();
		songTable = new JTable(datos);
		songTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		
		// Lo incluimos todo dentro de un panel deslizable.
		JScrollPane scrollPane = new JScrollPane(
				songTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void initializeDeleteButton() {
		JButton deleteButton = new JButton(deleteButtonText);
		
		// TODO: Acción de eliminar.
		
		add(deleteButton, BorderLayout.PAGE_END);
	}
	
	// -------- Interacción con el controlador --------
	
	private void registerControllerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() 
		{
			// En el inicio de sesión se debe cargar la lista actual de 
			// favoritos del usuario.
			@Override
			public void onLogin(User u) {
				currentFavourites = u.getFavouriteSongs();
			}
		});
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() 
		{
			// Al inicio y al añadir nuevas canciones se descarga la 
			// nueva lista de canciones.
			@Override
			public void onSongList(Set<Song> songs) {
				List<Song> l = new ArrayList<Song>(songs);
				datos.setSongList(l);
			}
			// Establecer en la tabla la búsqueda realizada.
			@Override
			public void onSongSearch(List<Song> searched) {
				datos.setSongList(searched);
			}
		});
	}
	
	// -------- Modelo de datos de la tabla --------
	
	/**
	 * Modelo de datos utilizado para la tabla de resultados.
	 */
	public final class SongTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 9064674061310096123L;

		private String[] columnNames = 
	    	{"Título", "Intérprete", "Estilo", "Favorito"};

	    private List<Song> songs;
	    
	    private SongTableModel() {
	    	this.songs = new ArrayList<Song>();
	    }

	    @Override
	    public Object getValueAt(int row, int col) {
	    	Song s = songs.get(row);
	    	switch (col) {
			case 0: 
				return s.getName();
			case 1:
				return s.getAuthor();
			case 2:
				return s.getStyle();
			case 3:
				return currentFavourites.contains(s);
			default:
				throw new IllegalArgumentException("Unexpected value: " + col);
			}
	    }
	    
	    @Override
	    public Class<?> getColumnClass(int columnIndex) {
	    	switch (columnIndex) {
			case 3:
				return Boolean.class;
			default: 
				return String.class;
			
	    	}
	    }
	    
	    @Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	    	if (columnIndex == 3) {
	    		// TODO: Modificar estado premium.
	    	}
	    }
	    
	    @Override
	    public boolean isCellEditable(int rowIndex, int columnIndex) {
	    	return columnIndex == 3;
	    }
	    
	    @Override
	    public int getRowCount() {
	        return songs.size();
	    }

	    public void setSongList(List<Song> songs) {
	        this.songs = songs;
	        fireTableDataChanged();
	    }

	    @Override 
	    public String getColumnName(int index) { 
	        return columnNames[index]; 
	    } 

	    @Override
	    public int getColumnCount() {
	        return columnNames.length;
	    } 
	}
}
