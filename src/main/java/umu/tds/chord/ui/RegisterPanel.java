package umu.tds.chord.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import umu.tds.chord.controller.Controller;

public class RegisterPanel extends JPanel {

	private static final long serialVersionUID = -5702881625651122753L;
	private static final String registerText = "Registro de usuario";
	private static final String usernameText = "Nombre de usuario";
	private static final String passwordText = "Contraseña";
	private static final String confirmText = "Confirmar contraseña";
	private static final String birthdayText = "Seleccionar cumpleaños";
	private static final String createText = "Crear cuenta";
	private static final String repeatedUser = "Nombre de usuario no disponible.";
	private static final String sucessRegister = "Registro exitoso.";
	
	private TextField usernameField;
	private PasswordField passwordField;
	private PasswordField confirmField;
	private DateField calendarField;
	private ResponsiveButton create;
	private AlertTextArea error;
	
	private RegisterVerifier verifier;
		
	public RegisterPanel() {		
		setLayout(new GridBagLayout());
				
		initializeLabel();
		initializeUsernameField();
		initializePasswordField();
		initializeConfirmField();
		initializeBirthday();
		initializeCalendarField();
		initializeCreate();
		initializeError();
		
		initializeVerifier();
		
		setSize(getPreferredSize());
	}

	// ---------- Interfaz. ----------
	
	private void initializeLabel() {
		JLabel label = new JLabel(registerText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 5, 10);
		
		add(label, constraints);
	}
	
	private void initializeUsernameField() {
		usernameField = new TextField(usernameText);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);
		
		add(usernameField, constraints);
	}
	
	private void initializePasswordField() {
		passwordField = new PasswordField(passwordText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(passwordField, constraints);
	}
	
	private void initializeConfirmField() {
		confirmField = new PasswordField(confirmText);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(confirmField, constraints);
	}
	
	private void initializeBirthday() {
		JLabel birthday = new JLabel(birthdayText);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(birthday, constraints);
	}
	
	private void initializeCalendarField() {
		calendarField = new DateField();
		
		JDateChooser chooser = new JDateChooser(calendarField);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(chooser, constraints);
	}
	
	private void initializeCreate() {
		create = new ResponsiveButton(createText);
		create.addActionListener(e -> register());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 5, 10);

		add(create, constraints);
	}
	
	private void initializeError() {
		error = new AlertTextArea();
				
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 10, 10, 10);

		add(error, constraints);
	}
	
	// ---------- Validación. ----------

	private void initializeVerifier() {
		verifier = new RegisterVerifier(create, error);
		verifier.setUsernameField(usernameField);
		verifier.setPasswordField(passwordField);
		verifier.setConfirmField(confirmField);
		verifier.setCalendarField(calendarField);
	}
	
	
	// ---------- Comunicación con el controlador. ----------
		
	private void register() {
		if (verifier.verify()) {
			String username = verifier.getUsername();
			String password = verifier.getPassword();
			Date birthday = verifier.getBirthday();
			
			boolean res = Controller.INSTANCE.register(username, password, birthday);
						
			if (!res) error.setFail(repeatedUser);
			else error.setSuccess(sucessRegister);
		}		
	}
}
