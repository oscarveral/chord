package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;
import java.util.Optional;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import pulsador.EncendidoEvent;
import pulsador.IEncendidoListener;
import pulsador.Luz;
import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.User;

/**
 * Panel principal con la funcionalidad de la aplicación. Proporciona acceso 
 * al usuario a todas las funcionalidades del sistema. Es el panel raiz dentro
 * del cual se mostrarán el resto de paneles con diferentes utilidades según 
 * lo solicite el usuario.
 */
public final class MainPanel extends JPanel{

	private static final long serialVersionUID = 1779179713763054840L;
	
	private static final String userPanelTitle = "Usuario";
	private static final String templateUserName = "<no user>";
	private static final String premiumText = "Premium";
	private static final String logoutButtonText = "Cerrar sesión";
	private static final String deleteAccButtonText = "Eliminar cuenta";
	private static final String invisibleTag = "invisible";
	private static final String searchTag = "search";
	private static final String invalidFrontendEventOnUtilityPanel = 
			"The utility panel sended an invalid event on this frontend: ";
	private static final String fileExtensionDesc = "Fichero XML (*.xml)";
	private static final String fileExtension = ".xml";
	private static final String openFileDialogTitle = "Abrir fichero de carga";
	private static final String badFormatFile = 
			"El fichero proporcionado no sigue el esquema XML adecuado";
	private static final String errorLoadFile = "Error de carga de canciones";
	
	private JPanel userPanel;
	private JLabel userName;
	private JButton logoutButton;
	private JButton deleteAccButton;
	private JToggleButton premiumToggle;
	
	private Luz pulsadorCarga;
	private JFileChooser fileChooser;
	
	private UtilityPanel utilityPanel;
	
	private JPanel centerContainer;
	private CardLayout centerLayout;
	private SearchContainerPanel searchPanel;
	
	/**
	 * Constructor por defecto.
	 */
	public MainPanel() {
		super();
		// Sólo se llama al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		
		layout.setHgap(10);
		setLayout(layout);
		
		initializeCenterContainer();
		initializeUtilityPanel();
		initializeUserPanel();
		
		registerControllerListeners();
				
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));		
	}
	
	private void initializeCenterContainer() {
		searchPanel = new SearchContainerPanel();
		
		centerLayout = new CardLayout();
		centerContainer = new JPanel(centerLayout);
		centerContainer.setFocusable(true);
		centerContainer.add(new JPanel(), invisibleTag);
		centerContainer.add(searchPanel, searchTag);
		centerLayout.show(centerContainer, invisibleTag);
		
		add(centerContainer, BorderLayout.CENTER);
	}
	
	private void initializeUtilityPanel() {
		utilityPanel = new UtilityPanel();
		utilityPanel.addInterfaceEventListener(new InterfaceEventListener() {
			
			@Override
			public void onEvent(InterfaceEvent e) {
				switch (e) {
				case SEARCH_PANEL_REQUEST:
					centerLayout.show(centerContainer, searchTag);
					break;
				case PLAYLIST_MNGMT_PANEL_REQUEST:
					centerLayout.show(centerContainer, invisibleTag);
					// TODO: Mostrar panel de gestión de playlists.
					break;
				case RECENT_SONGS_PANEL_REQUEST:
					centerLayout.show(centerContainer, invisibleTag);
					// TODO: Mostrar panel de canciones recientes.
					break;
				case PLAYLISTS_PANEL_REQUEST:
					centerLayout.show(centerContainer, invisibleTag);
					// TODO: Mostrar panel de playlists.
					break;
				default:
					throw new IllegalArgumentException
						(invalidFrontendEventOnUtilityPanel + e.name());
				}
			}
		});
		
		add(utilityPanel, BorderLayout.LINE_START);
	}
	
	private void initializeUserPanel() {
		
		GridBagLayout userPanelLayout = new GridBagLayout();
		
		GridBagConstraints userNameConstraints = new GridBagConstraints();
		userNameConstraints.gridx = 0;
		userNameConstraints.gridy = 0;
		userNameConstraints.weightx = 1.0;
		userNameConstraints.weighty = 1.0;
		userNameConstraints.fill = GridBagConstraints.HORIZONTAL;
		userNameConstraints.insets = new Insets(0, 10, 10, 10);
		
		GridBagConstraints premiumToggleConstraints = new GridBagConstraints();
		premiumToggleConstraints.gridx = 1;
		premiumToggleConstraints.gridy = 0;
		premiumToggleConstraints.insets = new Insets(0, 10, 10, 5);
		premiumToggleConstraints.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints logoutButtonConstraints = new GridBagConstraints();
		logoutButtonConstraints.gridx = 2;
		logoutButtonConstraints.gridy = 0;
		logoutButtonConstraints.insets = new Insets(0, 5, 10, 5);
		logoutButtonConstraints.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints deleteAccButtonConstraints = 
				new GridBagConstraints();
		deleteAccButtonConstraints.gridx = 3;
		deleteAccButtonConstraints.gridy = 0;
		deleteAccButtonConstraints.insets = new Insets(0, 5, 10, 5);
		deleteAccButtonConstraints.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints pulsadorCargaConstraints = 
				new GridBagConstraints();
		pulsadorCargaConstraints.gridx = 4;
		pulsadorCargaConstraints.gridy = 0;
		pulsadorCargaConstraints.insets = new Insets(0, 5, 10, 10);
		pulsadorCargaConstraints.fill = GridBagConstraints.BOTH;
		
		initializeUserName();
		initializePremiumToggle();
		initializeLogoutButton();
		initializeDeleteAccButton();
		initializePulsdadorCarga();
		
		userPanel = new JPanel(userPanelLayout);
		userPanel.setBorder(BorderFactory.createTitledBorder
				(BorderFactory.createLineBorder(Color.BLACK), userPanelTitle));
		userPanel.add(userName, userNameConstraints);
		userPanel.add(premiumToggle, premiumToggleConstraints);
		userPanel.add(logoutButton, logoutButtonConstraints);
		userPanel.add(deleteAccButton, deleteAccButtonConstraints);
		userPanel.add(pulsadorCarga, pulsadorCargaConstraints);
		
		add(userPanel, BorderLayout.PAGE_START);
	}
	
	private void initializeUserName() {
		userName = new JLabel(templateUserName);
		userName.setHorizontalAlignment(SwingConstants.LEFT);	
	}
	
	private void initializePremiumToggle() {
		premiumToggle = new JToggleButton(premiumText);
		
		Action action = new AbstractAction() {

			private static final long serialVersionUID = 6497727916674952602L;

			@Override
			public void actionPerformed(ActionEvent e) {
				togglePremium();
			}
		};
		
		premiumToggle.addActionListener(action);
		premiumToggle.setSelected(false);
		premiumToggle.getActionMap().put(premiumText, action);
		premiumToggle.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), premiumText);
	}
	
	private void initializeLogoutButton() {
		logoutButton = new JButton(logoutButtonText);
		
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 6497727916674952602L;

			@Override
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		};
		
		logoutButton.addActionListener(action);
		logoutButton.getActionMap().put(logoutButtonText, action);
		logoutButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), logoutButtonText);
	}
	
	private void initializeDeleteAccButton() {
		deleteAccButton = new JButton(deleteAccButtonText);
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 649772716674952602L;

			@Override
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		};
		
		deleteAccButton.addActionListener(action);
		deleteAccButton.getActionMap().put(deleteAccButtonText, action);
		deleteAccButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), deleteAccButtonText);
	}
	
	private void initializePulsdadorCarga() {
		
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setDialogTitle(openFileDialogTitle);
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return fileExtensionDesc;
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String filename = f.getName().toLowerCase();
				return filename.endsWith(fileExtension);
			}
		});
		
		pulsadorCarga = new Luz();
		pulsadorCarga.addEncendidoListener(new IEncendidoListener() {
			
			@Override
			public void enteradoCambioEncendido(EventObject arg0) {
				EncendidoEvent e = (EncendidoEvent) arg0;
				// Un cambio de encendido debe permitir solicitar la carga
				// de nuevas canciones.
				if (e.getNewEncendido() != e.getOldEncendido()) {
					int res = fileChooser.showOpenDialog(userPanel.getParent());
					if (res == JFileChooser.APPROVE_OPTION) {
						// Obtener la ruta del fichero seleccionado.
						File f = fileChooser.getSelectedFile();
						try {
							String path = f.getCanonicalPath();
							cargarCanciones(path);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
		
	// -------- Interacción con el controlador --------
	
	private void registerControllerListeners() {
		// Escuchar eventos para establecer la interfaz de forma acorde.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener()
		{
			
			@Override
			public void onLogin(Optional<User> u) {
				u.ifPresent(user -> {
					centerLayout.show(centerContainer, invisibleTag);
					userName.setText(user.getUserName());
					premiumToggle.setSelected(user.isPremium());
				});
				
			}
			
			@Override
			public void onLogout() {
				centerLayout.show(centerContainer, invisibleTag);
				userName.setText(templateUserName);
				premiumToggle.setSelected(false);
			}
			
			@Override
			public void onPremiumChange(boolean premium) {
				premiumToggle.setSelected(premium);
			}
		});
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener()
		{
			@Override
			public void onSongLoadFailure() {
				JOptionPane.showMessageDialog(
						userPanel.getParent(), 
						badFormatFile,
			            errorLoadFile, 
			            JOptionPane.ERROR_MESSAGE
			    );
			}
		});
	}
	
	private void logout() {
		Controller.INSTANCE.logout();
	}
	
	private void remove() {
		Controller.INSTANCE.remove();
	}
	
	private void togglePremium() {
		Controller.INSTANCE.togglePremium();
	}

	private void cargarCanciones(String path) {
		Controller.INSTANCE.cargarCanciones(path);
	}
}