package umu.tds.chord.ui;

import java.util.Date;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RegisterVerifier extends LoginVerifier {

	private static final String emptyConfirm = "Confirma la contraseña.";
	private static final String diffPassword = "Las contraseñas introducidas no coinciden.";
	private static final String badDate = "Introduce una fecha válida.";

	private PasswordField confirmField;
	private DateField calendarField;

	public RegisterVerifier(ResponsiveButton button, AlertTextArea error) {
		super(button, error);
	}

	public void setConfirmField(PasswordField confirmField) {
		if (this.confirmField != null)
			return;
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
		if (this.calendarField != null)
			return;
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

	@Override
	public boolean verify() {

		String password = super.getPassword();
		String confirm = String.valueOf(confirmField.getPassword());

		boolean res = false;

		if (super.getUsernameField().isEmpty())
			super.getError().setFail(emptyUsername);
		else if (super.getPasswordField().isEmpty())
			super.getError().setFail(emptyPassword);
		else if (confirmField.isEmpty())
			super.getError().setFail(emptyConfirm);
		else if (!password.equals(confirm))
			super.getError().setFail(diffPassword);
		else if (calendarField.getDate() == null)
			super.getError().setFail(badDate);
		else {
			super.getError().setSuccess(emptyString);
			res = true;
		}

		super.getButton().setEnabled(res);
		super.getButton().setFocusable(res);

		return res;
	}

	public Date getBirthday() {
		return calendarField.getDate();
	}

	@Override
	public void refresh() {

		super.refresh();

		confirmField.reset();
		calendarField.reset();
	}
}
