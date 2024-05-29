package umu.tds.chord.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.toedter.calendar.JTextFieldDateEditor;

public class DateField extends JTextFieldDateEditor {

	private static final long serialVersionUID = 1678160303721410909L;
	private static final String format = "dd/MM/yyyy";
	private static final char dash = '-';
	private static final char enter = '\n';
	private static final String empty = "";
	
	public DateField() {
		super(format, format, dash);
		
		initializeActionListener();
		initializeKeyListener();
	}
	
	private void initializeActionListener() {
		addActionListener(e -> {
			if (isFocusOwner())
				transferFocus();
		});
	}
	
	private void initializeKeyListener() {
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == enter && isFocusOwner())
					transferFocus();
			}
		});
	}
	
	public void reset() {
		setText(empty);
	}
}
