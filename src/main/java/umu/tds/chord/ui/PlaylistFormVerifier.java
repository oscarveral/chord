package umu.tds.chord.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PlaylistFormVerifier {

	protected static final String errorText = "Unsupported";
	
	private TextField name;
	private ResponsiveTextArea desc;
	private ResponsiveButton submit;

	public PlaylistFormVerifier(ResponsiveButton button) {
		submit = button;
		
		submit.setEnabled(false);
		submit.setFocusable(false);
	}
	
	public void setNameField(TextField name) {
		if (this.name != null) return;
		this.name = name;
		this.name.getDocument().addDocumentListener(new DocumentListener() {
			
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
	
	public void setDescField(ResponsiveTextArea desc) {
		if (this.desc != null) return;
		this.desc = desc;
		this.desc.getDocument().addDocumentListener(new DocumentListener() {
			
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
		boolean res = !name.isEmpty() && !desc.isEmpty();
		submit.setEnabled(res);
		submit.setFocusable(res);
		return res;
	}
	
	public void refresh() {
		name.reset();
		desc.reset();
		submit.setEnabled(false);
		submit.setFocusable(false);
	}
}
