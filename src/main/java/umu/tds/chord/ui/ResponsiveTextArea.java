package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

public class ResponsiveTextArea extends JTextArea {

	private static final long serialVersionUID = -5208136966223256316L;
	private static final String error = "Unsupported";

	private final String text;
	private boolean empty;

	public ResponsiveTextArea(String text) {
		this.text = text;
		this.empty = true;

		initializeText();
		initializeFocusListener();
		initializeDocumentListener();
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
				// Prioridad de ejecución de este listener sobre el resto. Refrescar resultados.
				Arrays.stream(((AbstractDocument) getDocument()).getDocumentListeners()).filter(l -> !l.equals(this)).forEach(l -> l.removeUpdate(e));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				empty = getText().isEmpty() || getText().isBlank();
				// Prioridad de ejecución de este listener sobre el resto. Refrescar resultados.
				Arrays.stream(((AbstractDocument) getDocument()).getDocumentListeners()).filter(l -> !l.equals(this)).forEach(l -> l.insertUpdate(e));
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new RuntimeException(error);
			}
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
	
	@Override
	public void setText(String t) {
		super.setText(t);
		setForeground(Color.GRAY);
		if (!empty) setForeground(Color.BLACK);
	}
	
	public void reset() {
		initializeText();
	}
}
