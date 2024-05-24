package umu.tds.chord.ui;

import java.awt.Color;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RegisterVerifier {

	private static final String emptyString = "";
	private static final String errorText = "Unsupported";
	private static final String emptyUsername = "Introduce un nombre de usuario.";
	private static final String emptyPassword = "Introduce la contraseña.";
	private static final String emptyConfirm = "Confirma la contraseña.";
	private static final String diffPassword = "Las contraseñas introducidas no coinciden.";
	private static final String badDate = "Introduce una fecha válida.";
	
	private JButton create;
	private AlertTextArea error;
	
	private TextField usernameField;
	private PasswordField passwordField;
	private PasswordField confirmField;
	private DateField calendarField;
	
	public RegisterVerifier(JButton create, AlertTextArea error) {
		this.create = create;
		this.error = error;
		
		create.setEnabled(false);
		create.setFocusable(false);
		error.setText(emptyString);
		error.setForeground(Color.RED);
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
	
	public void setConfirmField(PasswordField confirmField) {
		if (this.confirmField != null) return;
		this.confirmField = confirmField;
		this.confirmField.getDocument().addDocumentListener(new DocumentListener() {
			
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
	
	public void setCalendarField(DateField calendarField) {
		if (this.calendarField != null) return;
		this.calendarField = calendarField;
		this.calendarField.getDocument().addDocumentListener(new DocumentListener() {
			
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
		
		String password = String.valueOf(passwordField.getPassword());
		String confirm = String.valueOf(confirmField.getPassword());
		
		boolean res = false;
		error.setForeground(Color.RED);
		if (usernameField.isEmpty())
			error.setFail(emptyUsername);
		else if (passwordField.isEmpty())
			error.setFail(emptyPassword);
		else if (confirmField.isEmpty())
			error.setFail(emptyConfirm);
		else if (!password.equals(confirm))
			error.setFail(diffPassword);
		else if (calendarField.getDate() == null)
			error.setFail(badDate);
		else {
			error.setSuccess(emptyString);
			res = true;
		}
		
		create.setEnabled(res);
		create.setFocusable(res);
		
		return res;
	}
	
	public String getUsername() {
		return usernameField.getText();
	}
	
	public String getPassword() {
		return String.valueOf(passwordField.getPassword());
	}
	
	public Date getBirthday() {
		return calendarField.getDate();
	}
}
