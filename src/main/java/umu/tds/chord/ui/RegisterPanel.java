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
import java.time.LocalDate;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusListener;

/**
 * Panel utilizado para el registro de usuarios.
 */
final public class RegisterPanel extends JPanel {

	private static final long serialVersionUID = -7957892588362665674L;
	
	private static final char passwordCharacter = '*';
	
	private static final String defaultInfo = "Formulario de registro";
	private static final String nameDefaultInput = "Usuario";
	private static final String passwordDefaultInput = "Contraseña";
	private static final String confirmPasswordDefaultInput = 
			"Confirmar contraseña";
	private static final String registerButtonText = "Crear cuenta";
	private static final String documentPropertyChangeExceptionMsg = 
			"Unexpected change on properties for documment listener: ";
	private static final String spaceText = "\n";
	private static final String succesfullRegister = "Registro exitoso";
	private static final String errorRegister = 
			"Nombre de usuario \nno disponible";
	
	private boolean userInputEmpty;
	private boolean passwordInputEmpty;
	private boolean confirmPasswordInputEmpty;
	
	private JLabel information;
	private JTextField nameInput;
	private JPasswordField passwordInput;
	private JPasswordField confirmPasswordInput;
	private JTextArea statusLabel;
	
	private JButton registerButton;
	
	/**
	 * Contructor por defecto.
	 */
	public RegisterPanel() {
		super();
		// Sólo se llama a la función de inicialización.
		initialize();
	}
	
	private void initialize() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		initializeInformation();
		initializeNameInput();
		initializePasswordInput();
		initializeConfirmPasswordInput();
		initializeRegisterButton();
		initializeStatusLabel();
		
		initializeVisibilityListener();
		
		registerControllerListener();
		
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}
	
	private void initializeInformation() {
		information = new JLabel(defaultInfo);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(information, constraints);
	}
	
	private void initializeNameInput() {
		userInputEmpty = true;
		nameInput = new JTextField(nameDefaultInput);
		nameInput.setForeground(Color.GRAY);
		
		// Para hacer más bonita la interfaz.
		nameInput.addFocusListener(new FocusListener() {
						
			@Override
			public void focusLost(FocusEvent e) {
				if (userInputEmpty) {
					userInputEmpty = false;
					nameInput.setText(nameDefaultInput);
					nameInput.setForeground(Color.GRAY);
					userInputEmpty = true;
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (userInputEmpty) {
					nameInput.setText(null);
					nameInput.setForeground(Color.BLACK);
				}
			}
		});
		nameInput.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (nameInput.getText().isEmpty()) {
					userInputEmpty = true;
					registerButton.setEnabled(isValidInput());
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (userInputEmpty) {
					userInputEmpty = false;
					registerButton.setEnabled(isValidInput());
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException
					(documentPropertyChangeExceptionMsg + nameDefaultInput);
			}
		});
		// Se transfiere el foco al campo de contraseña para cualquier acción.
		nameInput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (nameInput.isFocusOwner())
					nameInput.transferFocus();
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(nameInput, constraints);
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
				}
				registerButton.setEnabled(isValidInput());				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (passwordInputEmpty) {
					passwordInputEmpty = false;
				}
				registerButton.setEnabled(isValidInput());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException
					(documentPropertyChangeExceptionMsg + passwordDefaultInput);
			}
		});
		
		// Se transfiere el foco al campo de confirmar contraseña.
		passwordInput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (passwordInput.isFocusOwner())
					passwordInput.transferFocus();
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(passwordInput, constraints);
	}
	
	private void initializeConfirmPasswordInput() {
		confirmPasswordInputEmpty = true;
		confirmPasswordInput = new JPasswordField(confirmPasswordDefaultInput);	
		confirmPasswordInput.setEchoChar((char) 0);
		confirmPasswordInput.setForeground(Color.GRAY);
		
		// Para hacer más bonita la interfaz.
		confirmPasswordInput.addFocusListener(new FocusListener() {
						
			@Override
			public void focusLost(FocusEvent e) {
				if (confirmPasswordInputEmpty) {
					confirmPasswordInputEmpty = false;
					confirmPasswordInput.setEchoChar((char) 0);
					confirmPasswordInput.setText(confirmPasswordDefaultInput);
					confirmPasswordInput.setForeground(Color.GRAY);
					confirmPasswordInputEmpty = true;
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (confirmPasswordInputEmpty) {
					confirmPasswordInput.setEchoChar(passwordCharacter);
					confirmPasswordInput.setText(null);
					confirmPasswordInput.setForeground(Color.BLACK);
				}
				
			}
		});
		confirmPasswordInput.getDocument().addDocumentListener
		(
			new DocumentListener() {
			
				@Override
				public void removeUpdate(DocumentEvent e) {
					if (confirmPasswordInput.getPassword().length == 0) {
						confirmPasswordInputEmpty = true;
					}
					registerButton.setEnabled(isValidInput());
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					if (confirmPasswordInputEmpty) {
						confirmPasswordInputEmpty = false;
					}
					registerButton.setEnabled(isValidInput());
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					throw new RuntimeException
						(documentPropertyChangeExceptionMsg 
						+ confirmPasswordDefaultInput);
				}
			}
		);
		
		// Cualquier acción provoca un intento de registro.
		confirmPasswordInput.addActionListener(e -> {
			register();
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);
		
		add(confirmPasswordInput, constraints);
	}
	
	private void initializeRegisterButton() {
		Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 737815404353999034L;

			@Override
			public void actionPerformed(ActionEvent e) {
				register();
			}
		};
		
		registerButton = new JButton(registerButtonText);
		registerButton.addActionListener(action);
		registerButton.getActionMap().put(registerButtonText, action);
		registerButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), registerButtonText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 10, 0);
		
		add(registerButton, constraints);	
		
	}
	
	private void initializeStatusLabel() {
		
		statusLabel = new JTextArea(spaceText);
		statusLabel.setForeground(Color.RED);
		statusLabel.setEditable(false);
		statusLabel.setCursor(null);
		statusLabel.setOpaque(false);
		statusLabel.setFocusable(false);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 10, 0);
		
		add(statusLabel, constraints);
	}
	
	private void initializeVisibilityListener() {
		// Limpiar la interfaz si se esconde este compoonente.
		addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				refreshInterface();
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {}
		});
	}
	
	private boolean isValidInput() {
		String password = String.valueOf
				(passwordInput.getPassword());
		String confirmPassword = String.valueOf
				(confirmPasswordInput.getPassword());
		boolean samePassword = password.equals(confirmPassword);
		// Validar inputs antes de intentar el registro.
		return (!userInputEmpty && 
				!passwordInputEmpty && 
				!confirmPasswordInputEmpty &&
				samePassword);
	}
	
	private void refreshInterface() {
		nameInput.setText(nameDefaultInput);
		nameInput.setForeground(Color.GRAY);
		nameInput.requestFocusInWindow();
		
		passwordInput.setText(passwordDefaultInput);
		passwordInput.setEchoChar((char) 0);
		passwordInput.setForeground(Color.GRAY);
		
		confirmPasswordInput.setText(confirmPasswordDefaultInput);
		confirmPasswordInput.setEchoChar((char) 0);
		confirmPasswordInput.setForeground(Color.GRAY);
		
		userInputEmpty = true;
		passwordInputEmpty = true;
		confirmPasswordInputEmpty = true;
				
		registerButton.setEnabled(isValidInput());
	
		statusLabel.setText(spaceText);
	}
	
	// -------- Interacción con el controlador --------
	
	private void registerControllerListener() {
		// Se deben escuchar los intentos de registro.
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() 
		{
			// Actualizar UI según resultado
			@Override
			public void onRegister(boolean success) {
				if (success) {
					refreshInterface();
					statusLabel.setForeground(new Color(0x73a657));
					statusLabel.setText(succesfullRegister);
				}
				else {
					statusLabel.setForeground(Color.RED);
					statusLabel.setText(errorRegister);
				}
			}
		});
	}
	
	private void register() {
		// Volvemos a validar el input.
		if (!isValidInput()) return;
		
		// Recuperar usuario y contraseña introducidos.
		String user = nameInput.getText();
		String pass = String.valueOf(passwordInput.getPassword());
		
		// TODO: Obtener la fecha mediante un calendario.
		Controller.INSTANCE.register(user, pass, LocalDate.now());
	}
}
