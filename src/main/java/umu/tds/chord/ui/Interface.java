package umu.tds.chord.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.User;
import umu.tds.chord.utils.ImageScaler;

/**
 * Clase de la interfaz que contiene toda la funcionalidad principal que permite
 * el funcionamiento de la interfaz de la aplicación. Contiene la ventana
 * principal de la interfaz, que permitirá al usuario interactuar con la 
 * aplicación.
 */
public final class Interface {

	private static final String appName = "Chord";
	private static final String iconPath = "/images/icon.png";
	private static final String loginTag = "login";
	private static final String mainTag = "main";
	private static final String registerTitle = "Registro";
	private static final String invalidFrontendEventOnLoginPanel = 
			"Error. El panel de inicio de sesión ha enviado el evento: ";
	
	private JFrame ventana;
	
	private CardLayout layout;
	private JPanel container;
	
	private LoginPanel loginPanel;
	private MainPanel mainPanel;
	
	private JDialog registerDialog;
	private RegisterPanel registerPanel;
	
	/**
	 * Constructor por defecto.
	 */
	public Interface() {	
		// Sólo llama al método de inicialización.
		initialize();
	}
	
	/**
	 *	Establece la ventana principal como visible.
	 */
	public void show() {
		ventana.setVisible(true);
	}
	
	private void initialize() {	
		
		layout = new CardLayout();
		container = new JPanel(layout);
		
		initializeFrame();
		initializeLoginPanel();
		initializeMainPanel();
		initializeRegisterPanel();
						
		registerControllerListener();
				
		ventana.setContentPane(container);
	}
	
	private void initializeFrame() {
		ventana = new JFrame();
		
		Dimension minSize = new Dimension(800, 600);
		Rectangle defaultBounds = new Rectangle(100, 100, 800, 600);
		ImageIcon icono = ImageScaler.loadImageIcon(iconPath, 20, 20);
		
		ventana.setTitle(appName);
		ventana.setBounds(defaultBounds);
		ventana.setMinimumSize(minSize);
		ventana.setIconImage(icono.getImage());
		ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		ventana.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e)
		    {
		        JFrame frame = (JFrame)e.getSource();
		 
		        int result = JOptionPane.showConfirmDialog(
		            frame,
		            "¿Desea salir de la aplicación?",
		            "Salir",
		            JOptionPane.YES_NO_OPTION);
		 
		        if (result == JOptionPane.YES_OPTION)
		        	forceLogout();
		            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    }
		});
	}
	
	private void initializeLoginPanel() {
		loginPanel = new LoginPanel();
		loginPanel.addInterfaceEventListener(new InterfaceEventListener() {
			
			@Override
			public void onEvent(InterfaceEvent e) {
				switch (e) {		
				case GITHUB_PANEL_REQUEST:
					// TODO: Change panel.
					break;
					
				case REGISTER_PANEL_REQUEST:
					registerDialog.setLocationRelativeTo(ventana);
					registerDialog.setVisible(true);
					break;
					
				default:
					throw new IllegalArgumentException
						(invalidFrontendEventOnLoginPanel + e.name());
				}
			}
		});
			
		container.add(loginPanel, loginTag);
	}
	
	private void initializeMainPanel() {
		mainPanel = new MainPanel();
		container.add(mainPanel, mainTag);
	}
	
	private void initializeRegisterPanel() {
		registerPanel = new RegisterPanel();

		registerDialog = new JDialog(ventana, true);
		registerDialog.setAlwaysOnTop(true);				
		registerDialog.setTitle(registerTitle);
		registerDialog.setContentPane(registerPanel);
		registerDialog.pack();
		registerDialog.setResizable(false);
		registerDialog.setVisible(false);
	}
	
	// -------- Interacción con el controlador --------
	
	private void registerControllerListener() {
		// Escuchar inicios y fin de sesiones para establecer el panel
		// que se debe mostrar.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			
			@Override
			public void onLogin(User u) {
				layout.show(container, mainTag);
				mainPanel.requestFocus();
			}
			
			@Override
			public void onLogout() {
				layout.show(container, loginTag);
				loginPanel.requestFocus();
			}
			
		});
	}
	
	private void forceLogout() {
		// Utilizado para forzar el cierre de sesión al cerrar la app.
		Controller.INSTANCE.logout();
	}
}
