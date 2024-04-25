package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.User;

/**
 * Panel con la interfaz de inicio de sesión o registro. Es el panel que se
 * muestra al abrir la aplicación.
 */
public final class LoginPanel extends JPanel{

	private static final long serialVersionUID = 5363840226169510119L;

	private static final char passwordCharacter = '*';
	
	private static final String userDefaultInput = "Usuario";
	private static final String passwordDefaultInput = "Contraseña";
	private static final String appLogoPath = "/images/logo.png";
	private static final String loginButtonText = "Iniciar sesión";
	private static final String githubButtonText = "Iniciar sesión con Github";
	private static final String registerButtonText = "Crear cuenta";
	private static final String documentPropertyChangeExceptionMsg = 
			"Unexpected change on properties for documment listener: ";
	private static final String failedLoginText = "Inicio de sesión fallido.";
	private static final String emptyText ="";
		
	private boolean userInputEmpty;
	private boolean passwordInputEmpty;

	private JLabel appLogo;
	
	private JTextField userInput;
	private JPasswordField passwordInput;
	
	private JButton loginButton;
	private JButton githubButton;
	private JButton registerButton;
	
	private JLabel failedLoginWarn;
	
	// Listeners para los eventos que pueda producir este panel.
	private Set<InterfaceEventListener> listeners;
	
	/**
	 * Constructor por defecto.
	 */
	public LoginPanel() {
		super();
		// Sólo llamamos al método de inicialización.
		initialize();
	}
	
	private void initialize() {
		GridBagLayout layout = new GridBagLayout();
		
		setLayout(layout);
		
		initializeAppLogo();
		initializeUserInput();
		initializePasswordInput();
		initializeLoginButton();
		initializeGithubButton();
		initializeRegisterButton();
		initializeFailedLoginWarn();
		
		registerControllerListener();
		
		listeners = new HashSet<InterfaceEventListener>();
		
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}
	
	private void initializeAppLogo() {
		
		ImageIcon logoImage = new ImageIcon
				(LoginPanel.class.getResource(appLogoPath));
		appLogo = new JLabel(logoImage);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		
		add(appLogo, constraints);
	}
	
	private void initializeUserInput() {
		userInputEmpty = true;
		userInput = new JTextField(userDefaultInput);
		userInput.setForeground(Color.GRAY);
		
		// Para hacer más bonita la interfaz.
		userInput.addFocusListener(new FocusListener() {
						
			@Override
			public void focusLost(FocusEvent e) {
				if (userInputEmpty) {
					userInputEmpty = false;
					userInput.setText(userDefaultInput);
					userInput.setForeground(Color.GRAY);
					userInputEmpty = true;
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (userInputEmpty) {
					userInput.setText(null);
					userInput.setForeground(Color.BLACK);
				}
			}
		});
		userInput.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (userInput.getText().isEmpty()) {
					userInputEmpty = true;
					loginButton.setEnabled(isValidInput());
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (userInputEmpty) {
					userInputEmpty = false;
					loginButton.setEnabled(isValidInput());
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException
					(documentPropertyChangeExceptionMsg + userDefaultInput);
			}
		});
		// Se transfiere el foco al campo de contraseña para cualquier acción.
		userInput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (userInput.isFocusOwner())
					userInput.transferFocus();
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(userInput, constraints);
	}
	
	private void initializePasswordInput() {
		passwordInputEmpty = true;
		passwordInput = new JPasswordField(passwordDefaultInput);	
		passwordInput.setEchoChar((char) 0);
		passwordInput.setForeground(Color.GRAY);
		
		// Para hacer más bonita la interfaz.
		passwordInput.addFocusListener(new FocusListener() {
						
			@Override
			public void focusLost(FocusEvent e) {
				if (passwordInputEmpty) {
					passwordInputEmpty = false;
					passwordInput.setEchoChar((char) 0);
					passwordInput.setText(passwordDefaultInput);
					passwordInput.setForeground(Color.GRAY);
					passwordInputEmpty = true;
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (passwordInputEmpty) {
					passwordInput.setEchoChar(passwordCharacter);
					passwordInput.setText(null);
					passwordInput.setForeground(Color.BLACK);
				}
				
			}
		});
		passwordInput.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (passwordInput.getPassword().length == 0) {
					passwordInputEmpty = true;
					loginButton.setEnabled(isValidInput());
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (passwordInputEmpty) {
					passwordInputEmpty = false;
					loginButton.setEnabled(isValidInput());
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException(documentPropertyChangeExceptionMsg + passwordDefaultInput);
			}
		});
		
		// Cualquier acción provoca un intento de inicio de sesión.
		passwordInput.addActionListener(e -> {
			login();
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(passwordInput, constraints);
	}
	
	private void initializeLoginButton() {
		
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 8076205565164732621L;

			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		};
		
		loginButton = new JButton(loginButtonText);
		loginButton.setEnabled(false);
		loginButton.addActionListener(action);
		loginButton.getActionMap().put(loginButtonText, action);
		loginButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), loginButtonText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(loginButton, constraints);
	}
	
	private void initializeGithubButton() {
		
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 862642580961964743L;

			@Override
			public void actionPerformed(ActionEvent e) {
				trigger(InterfaceEvent.GITHUB_PANEL_REQUEST);
			}
		};
		
		githubButton = new JButton(githubButtonText);
		githubButton.addActionListener(action);
		githubButton.getActionMap().put(githubButtonText, action);
		githubButton.getInputMap().put
			(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), githubButtonText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 0, 10, 0);
		
		add(githubButton, constraints);	
	}
	
	private void initializeRegisterButton() {
		
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 7378154054353999034L;

			@Override
			public void actionPerformed(ActionEvent e) {
				trigger(InterfaceEvent.REGISTER_PANEL_REQUEST);
			}
		};
		
		registerButton = new JButton(registerButtonText);
		registerButton.addActionListener(action);
		registerButton.getActionMap().put(registerButtonText, action);
		registerButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), registerButtonText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 10, 10, 0);
		
		add(registerButton, constraints);	
	}
	
	private boolean isValidInput() {
		// Validar inputs antes de intentar inicio de sesión.
		return (!userInputEmpty && !passwordInputEmpty);
	}
	
	private void trigger(InterfaceEvent e) {
		// Emitir un evento para todos los listeners.
		listeners.forEach(l -> l.onEvent(e));
	}
	
	private void initializeFailedLoginWarn() {
		
		failedLoginWarn = new JLabel(emptyText);
		failedLoginWarn.setForeground(Color.RED);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 10, 0);
		
		add(failedLoginWarn, constraints);
	}
	
	/**
	 * Añade un listener para los eventos producidos por este panel.
	 * 
	 * @param l Listener que se desea añadir.
	 */
	public void addInterfaceEventListener(InterfaceEventListener l) {
		listeners.add(l);
	}
	
	/**
	 * Elimina un listener de los eventos del panel.
	 * 
	 * @param l Listener que se desea eliminar.
	 */
	public void removeInterfaceEventListener(InterfaceEventListener l) {
		listeners.remove(l);
	}
	
	// -------- Interacción con el controlador --------

	private void registerControllerListener() {
		// Refrescar la interfaz después de un inicio de sesión exitoso.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			
			@Override
			public void onLogin(User u) {
				userInput.setText(userDefaultInput);
				userInput.setForeground(Color.GRAY);
				
				passwordInput.setText(passwordDefaultInput);
				passwordInput.setEchoChar((char) 0);
				passwordInput.setForeground(Color.GRAY);
				
				userInputEmpty = true;
				passwordInputEmpty = true;
				
				loginButton.setEnabled(isValidInput());
				
				failedLoginWarn.setText(emptyText);
			}
			
			@Override
			public void onFailedLogin() {
				// Se avisa del fallo.
				failedLoginWarn.setText(failedLoginText);
			}
		});
	}
	
	private void login() {
		
		if (!isValidInput()) return;
		
		// Recuperar usuario y contraseña introducidos.
		String user = userInput.getText();
		String pass = String.valueOf(passwordInput.getPassword());
		
		// Intenar login.
		Controller.INSTANCE.login(user, pass);
	}	
}
