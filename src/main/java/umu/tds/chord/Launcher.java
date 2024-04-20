package umu.tds.chord;

import java.awt.EventQueue;

import umu.tds.chord.ui.Interface;

/**
 * Lanzador de la aplicación.
 */
final public class Launcher 
{
	/**
	 * Loop principal de la aplicación.
	 * @param args Argumentos del programa.
	 */
    public static void main(String[] _args)
    {
    	EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					Interface v = new Interface();
			    	v.show();			
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}
