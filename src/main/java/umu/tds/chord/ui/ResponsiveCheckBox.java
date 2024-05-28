package umu.tds.chord.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.KeyStroke;

public class ResponsiveCheckBox extends JCheckBox {

	private static final long serialVersionUID = 8613867903935552274L;

	public ResponsiveCheckBox(String text) {
		super(text);
		
		initializeActionMap();
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
