package umu.tds.chord.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.ui.StateManager.UIEvents;
import umu.tds.chord.utils.ImageScaler;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = -6517596456695550881L;
	private static final String usernameText = "Nombre de usuario";
	private static final String passwordText = "Contraseña";
	private static final String loginText = "Iniciar sersión";
	private static final String githubText = "Iniciar sesión con Github";
	private static final String registerText = "Registro de usuarios";
	private static final String logoPath = "/images/logo.png";
	private static final String empty = "";
	private static final String loginError= "Fallo de inicio de sesión.";
	private static final String openFileDialogTitle = "Selecciona credenciales de GitHub";
	private static final String fileExtensionDesc = "Fichero Properties (*.properties)";
	private static final String fileExtension = ".properties";
	private static final int logoWidth = 648;
	private static final int logoHeight = 205;
	
	private TextField usernameField;
	private PasswordField passwordField;
	private ResponsiveButton login;
	private AlertTextArea error;
	private LoginVerifier verifier;
	private JFileChooser chooser;
	
	public LoginPanel() {
		setLayout(new GridBagLayout());
		
		initializeLogo();
		initializeUsernameField();
		initializePasswordField();
		initializeLogin();
		initializeGithub();
		initializeRegister();
		initializeError();
		
		initializeLoginVerifier();
		
		setSize(getPreferredSize());
	}
	
	// ---------- Interfaz. ----------
	
	private void initializeLogo() {
		JLabel logo= new JLabel(ImageScaler.loadImageIcon(logoPath, logoWidth, logoHeight));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 5, 10);
		
		add(logo, constraints);
	}
	
	private void initializeUsernameField() {
		usernameField = new TextField(usernameText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);
		
		add(usernameField, constraints);
	}
	
	private void initializePasswordField() {
		passwordField = new PasswordField(passwordText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(passwordField, constraints);
	}
	
	private void initializeLogin() {
		login = new ResponsiveButton(loginText);
		login.addActionListener(e -> login());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(login, constraints);
	}
	
	private void initializeGithub() {
		initializeFileChooser();
		
		ResponsiveButton github = new ResponsiveButton(githubText);
		github.addActionListener(e -> githubLogin());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 5);

		add(github, constraints);
	}
	
	private void initializeRegister() {
		ResponsiveButton register = new ResponsiveButton(registerText);
		register.addActionListener(e -> StateManager.INSTANCE.triggerEvent(UIEvents.REGISTER));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 4;
		//constraints.weightx = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 5, 5, 10);

		add(register, constraints);
	}
	
	private void initializeError() {
		error = new AlertTextArea();
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 10, 10);

		add(error, constraints);
	}
	
	private void initializeFileChooser() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileHidingEnabled(false);
		chooser.setDialogTitle(openFileDialogTitle);
		chooser.setFileFilter(new FileFilter() {
			
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
	}
	
	// ---------- Validación. ----------
	
	private void initializeLoginVerifier() {
		verifier = new LoginVerifier(login, error);
		
		verifier.setUsernameField(usernameField);
		verifier.setPasswordField(passwordField);
	}
	
	public void refresh() {
		verifier.refresh();
		usernameField.requestFocus();
	}
	
	// ---------- Comunicación con el controlador. ----------

	private void login() {
		if (verifier.verify()) {
			String username = verifier.getUsername();
			String password = verifier.getPassword();
			
			boolean res = Controller.INSTANCE.login(username, password);
						
			if (res) {
				error.setSuccess(empty);
				refresh();
			}
			else error.setFail(loginError);
		}
	}
	
	private void githubLogin() {
		int res = chooser.showOpenDialog(SwingUtilities.getWindowAncestor(this));
		if (res == JFileChooser.APPROVE_OPTION) {
			// Obtener la ruta del fichero seleccionado.
			File f = chooser.getSelectedFile();
			try {
				String path = f.getCanonicalPath();
				if(!Controller.INSTANCE.githubLogin(path)) error.setFail(loginError);
				else { 
					error.setSuccess(empty);
					refresh();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
