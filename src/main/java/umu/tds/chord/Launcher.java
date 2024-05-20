package umu.tds.chord;

import java.awt.EventQueue;

import umu.tds.chord.ui.Interface;

/**
 * Lanzador de la aplicación.
 */
public final class Launcher {
	/**
	 * Loop principal de la aplicación.
	 *
	 * @param args Argumentos del programa.
	 */
	public static void main(String[] _args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					@SuppressWarnings("unused")
					Interface v = new Interface();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
