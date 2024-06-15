package umu.tds.chord.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.utils.ImageScaler;

public final class Interface {

	private static final String title = "Chord";
	private static final String iconPath = "/images/icon.png";
	private static final String loginTag = "login";
	private static final String mainTag = "main";
	private static final String registerTitle = "Registro";
	
	private JFrame ventana;
	
	private CardLayout layout;
	private JPanel container;
	
	private LoginPanel login;
	private MainPanel main;
	
	private RegisterPanel register;
	private JDialog registerDialog;
	
	private ToolBar tools;
	
	public Interface() {
		
		StateManager.INSTANCE.setCallbackInterface(this);
		
		layout = new CardLayout();
		container = new JPanel(layout);
		
		initializeFrame();		
		initializeLogin();
		initializeMain();
		initializeRegister();
		
		ventana.setContentPane(container);

		registerControllerListener();
		
		ventana.setSize(ventana.getPreferredSize());
		ventana.setMinimumSize(ventana.getPreferredSize());
		
		Controller.INSTANCE.ready();
	}
	
	private void initializeFrame() {
		ventana = new JFrame();
		
		Dimension minSize = new Dimension(800, 600);
		Rectangle defaultBounds = new Rectangle(100, 100, 800, 600);
		ImageIcon icono = ImageScaler.loadImageIcon(iconPath, 20, 20);
		
		tools = new ToolBar();
		
		ventana.setTitle(title);
		ventana.setBounds(defaultBounds);
		ventana.setMinimumSize(minSize);
		ventana.setIconImage(icono.getImage());
		ventana.setSize(ventana.getPreferredSize());
		ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		ventana.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e)
		    {
		        JFrame frame = (JFrame)e.getSource();
		 
		        int result = JOptionPane.showConfirmDialog(
		            frame,
		            "¿Desea salir de la aplicación?",
		            "Salir",
		            JOptionPane.YES_NO_OPTION);
		 
		        if (result == JOptionPane.YES_OPTION) {
		        	forceLogout();
		            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		        }
		    }
		});	
	}
	
	private void initializeLogin() {
		login = new LoginPanel();
		container.add(login, loginTag);
	}
	
	private void initializeRegister() {
		register = new RegisterPanel();
		registerDialog = new JDialog(ventana, true);
		registerDialog.setAlwaysOnTop(true);				
		registerDialog.setTitle(registerTitle);
		registerDialog.setContentPane(register);
		registerDialog.setIconImage(ImageScaler.loadImageIcon(iconPath, 20, 20).getImage());
		registerDialog.pack();
		registerDialog.setResizable(false);
		registerDialog.setVisible(false);
	}
	
	private void initializeMain() {
		main = new MainPanel();
		container.add(main, mainTag);
	}
	
	public void show() {
		ventana.setVisible(true);
	}
	
	public void showMainPanel() {		
		ventana.setJMenuBar(tools);
		ventana.validate();
		layout.show(container, mainTag);
		main.requestFocus();
	}
	
	public void showLoginPanel() {
		ventana.setJMenuBar(null);
		ventana.validate();
		layout.show(container, loginTag);
		login.requestFocus();
	}
	
	public void showRegisterPanel() {
		registerDialog.setLocationRelativeTo(ventana);
		registerDialog.setVisible(true);
	}
	
	private void registerControllerListener() {
		// Escuchar inicios y fin de sesiones para establecer el panel
		// que se debe mostrar.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			
			@Override
			public void onUserLogin(UserStatusEvent e) {
				showMainPanel();
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				showLoginPanel();
			}
		});
	}
	
	private void forceLogout() {
		// Utilizado para forzar el cierre de sesión al cerrar la app.
		Controller.INSTANCE.logout();
	}
}