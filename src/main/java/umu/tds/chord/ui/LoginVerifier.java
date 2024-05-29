package umu.tds.chord.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginVerifier {
	
	protected static final String emptyString = "";
	protected static final String errorText = "Unsupported";
	protected static final String emptyUsername = "Introduce un nombre de usuario.";
	protected static final String emptyPassword = "Introduce la contraseña.";

	private ResponsiveButton button;
	private AlertTextArea error;
	
	private TextField usernameField;
	private PasswordField passwordField;
	
	public LoginVerifier(ResponsiveButton button, AlertTextArea error) {
		this.button= button;
		this.error = error;
		
		button.setEnabled(false);
		button.setFocusable(false);
		error.setFail(emptyString);
	}
	
	public void setUsernameField(TextField usernameField) {
		if (this.usernameField != null) return;
		this.usernameField = usernameField;
		this.usernameField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				verify();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException(errorText);
			}
		});
	}
	
	public void setPasswordField(PasswordField passwordField) {
		if (this.passwordField != null) return;
		this.passwordField= passwordField;
		this.passwordField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				verify();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				removeUpdate(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException(errorText);
			}
		});
	}
	
	public boolean verify() {
		
		boolean res = false;
		if (usernameField.isEmpty())
			error.setFail(emptyUsername);
		else if (passwordField.isEmpty())
			error.setFail(emptyPassword);
		else {
			error.setSuccess(emptyString);
			res = true;
		}
		
		button.setEnabled(res);
		button.setFocusable(res);
		
		return res;
	}
	
	public ResponsiveButton getButton() {
		return button;
	}

	public AlertTextArea getError() {
		return error;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}
	
	public String getUsername() {
		return usernameField.getText();
	}
	
	public String getPassword() {
		return String.valueOf(passwordField.getPassword());
	}
	
	public void refresh() {
		usernameField.reset();
		passwordField.reset();
		button.setEnabled(false);
		button.setFocusable(false);
		error.setSuccess(emptyString);
	}
}
