package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;

import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

public class PasswordField extends JPasswordField {

	private static final long serialVersionUID = 283297421907688703L;
	private static final String error = "Unsupported";
	private static final char echo = '*';

	private final String text;
	private boolean empty;

	public PasswordField(String text) {
		this.text = text;
		this.empty = true;

		initializeText();
		initializeFocusListener();
		initializeDocumentListener();
		initializeActionListener();
	}

	private void initializeText() {
		empty = true;
		silentTextChange(text, Color.GRAY, (char) 0);
	}

	private void initializeFocusListener() {
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (empty)
					silentTextChange(text, Color.GRAY, (char) 0);
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (empty)
					silentTextChange(null, Color.BLACK, echo);
			}
		});
	}

	private void initializeDocumentListener() {
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				empty = getPassword().length == 0;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				removeUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException(error);
			}
		});
	}

	private void initializeActionListener() {
		addActionListener(e -> {
			if (isFocusOwner())
				transferFocus();
		});
	}

	private void silentTextChange(String text, Color color, char echo) {
		DocumentListener[] listeners = ((AbstractDocument) getDocument()).getDocumentListeners();
		Arrays.stream(listeners).forEach(((AbstractDocument) getDocument())::removeDocumentListener);
		setText(text);
		setForeground(color);
		setEchoChar(echo);
		setCaretPosition(0);
		Arrays.stream(listeners).forEach(((AbstractDocument) getDocument())::addDocumentListener);
	}

	public boolean isEmpty() {
		return empty;
	}

	public void reset() {
		initializeText();
	}
}
