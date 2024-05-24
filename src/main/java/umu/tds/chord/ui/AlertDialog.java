package umu.tds.chord.ui;

import javax.swing.JOptionPane;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;

public class AlertDialog {
	
	private static final String errorLoad= "Error de carga de canciones";
	private static final String errorLoadDesc = "Comprueba que el fichero XML utilizado sigue el formato correcto.";
	private static final String errorDelete = "Error de eliminación de canciones";
	private static final String errorDeleteDesc = "Se ha producido un error inesperado al intentar eliminar una canción.";
	
	public AlertDialog() {
		registerSongStatusListener();
	}
	
	// ---------- Interacción con el controlador. ----------
	
	private void registerSongStatusListener() {
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
			@Override
			public void onSongLoad(SongStatusEvent e) {
				if (e.isFailed()) JOptionPane.showMessageDialog(null, errorLoadDesc, errorLoad, JOptionPane.ERROR_MESSAGE);
			}
			
			@Override
			public void onSongDelete(SongStatusEvent e) {
				if (e.isFailed()) JOptionPane.showMessageDialog(null, errorDeleteDesc, errorDelete, JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
