package umu.tds.chord.ui;

import java.awt.Color;

import javax.swing.JTextArea;

public class AlertTextArea extends JTextArea {

	private static final long serialVersionUID = -4990483271621466859L;
	private static final int rows = 1;
	private static final int cols = 30;
	
	private static final Color red = Color.RED;
	private static final Color green = Color.GREEN.darker();
	
	public AlertTextArea() {
		super(rows, cols);
		setFocusable(false);
		setForeground(red);
		setOpaque(false);
	}
	
	public void setFail(String text) {
		setForeground(red);
		setText(text);
	}
	
	public void setSuccess(String text) {
		setForeground(green);
		setText(text);
	}
}
