package umu.tds.chord.ui;

import java.awt.Frame;

public final class Interface {

	Frame ventana;
	
	public Interface() {
		initialize();
	}
	
	private void initialize() {
		ventana = new Frame("A");
	}
	
	public void show() {
		ventana.setVisible(true);
	}
	
}