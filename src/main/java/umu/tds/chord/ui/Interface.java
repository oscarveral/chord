package umu.tds.chord.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public final class Interface {

	JFrame ventana;
	
	public Interface() {
		initialize();
	}
	
	private void initialize() {
		ventana = new JFrame("A");
		JPanel d = new JPanel();
		d.setLayout(new BorderLayout());
		d.add(new MainPanel(), BorderLayout.CENTER);
		AlertDialog e = new AlertDialog();
		ventana.setLayout(new BorderLayout());
		ventana.add(d, BorderLayout.CENTER);
	    ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Diff con exit on close
	    ventana.setSize(ventana.getPreferredSize());
	    ventana.setLocationRelativeTo(null);
	    ventana.pack();
	}
	
	public void show() {
		ventana.setVisible(true);
	}
	
}