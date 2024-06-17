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
	public static void main(String[] args) {

		EventQueue.invokeLater(() -> {
			try {
				Interface v = new Interface();
				v.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
