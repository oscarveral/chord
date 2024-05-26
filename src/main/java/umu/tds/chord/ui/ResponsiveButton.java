package umu.tds.chord.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

public class ResponsiveButton extends JButton {
	
	private static final long serialVersionUID = 6309429743716264399L;

	public ResponsiveButton(String text) {
		super(text);
		
		initializeActionMap();
	}
	
	public ResponsiveButton(String text, ImageIcon icon) {
		this(text);
		setIcon(icon);
	}
	
	private void initializeActionMap() {
		getActionMap().put(this, new AbstractAction() {

			private static final long serialVersionUID = 1204330540753885352L;

			@Override
			public void actionPerformed(ActionEvent e) {
				for (ActionListener a : getActionListeners())
					a.actionPerformed(e);
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), this);
	}
}
