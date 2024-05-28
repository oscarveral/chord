package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

public class TextField extends JTextField {

	private static final long serialVersionUID = -3748838447365458226L;
	private static final String error = "Unsupported";

	private final String text;
	private boolean empty;

	public TextField(String text) {
		this.text = text;
		this.empty = true;

		initializeText();
		initializeFocusListener();
		initializeDocumentListener();
		initializeActionListener();
	}

	private void initializeText() {
		empty = true;
		silentTextChange(text, Color.GRAY);
	}

	private void initializeFocusListener() {
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (empty) silentTextChange(text, Color.GRAY);
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (empty) silentTextChange(null, Color.BLACK);
			}
		});
	}

	private void initializeDocumentListener() {
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				empty = getText().isEmpty() || getText().isBlank();
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
	
	private void silentTextChange(String text, Color color) {
		DocumentListener[] listeners = ((AbstractDocument) getDocument()).getDocumentListeners();
		Arrays.stream(listeners).forEach(((AbstractDocument) getDocument())::removeDocumentListener);
		setText(text);
		setForeground(color);
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