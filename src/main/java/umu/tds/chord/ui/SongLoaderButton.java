package umu.tds.chord.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import pulsador.EncendidoEvent;
import pulsador.Luz;
import umu.tds.chord.controller.Controller;

public class SongLoaderButton extends Luz {

	private static final long serialVersionUID = 5506228270829859873L;
	private static final String fileExtensionDesc = "Fichero XML (*.xml)";
	private static final String fileExtension = ".xml";
	private static final String openFileDialogTitle = "Abrir fichero de carga";

	private JFileChooser fileChooser;

	public SongLoaderButton() {
		initializeFileChooser();
		initializeEncendidoListener();
	}

	// ---------- Interfaz. ----------

	private void initializeFileChooser() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setDialogTitle(openFileDialogTitle);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return fileExtensionDesc;
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String filename = f.getName().toLowerCase();
				return filename.endsWith(fileExtension);
			}
		});
	}

	private void initializeEncendidoListener() {
		addEncendidoListener(ev -> {
			EncendidoEvent e = (EncendidoEvent) ev;
			// Un cambio de encendido debe permitir solicitar la carga
			// de nuevas canciones.
			if (e.getNewEncendido() != e.getOldEncendido()) {
				int res = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(this));
				if (res == JFileChooser.APPROVE_OPTION) {
					// Obtener la ruta del fichero seleccionado.
					File f = fileChooser.getSelectedFile();
					try {
						String path = f.getCanonicalPath();
						cargarCanciones(path);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	// ---------- Interacción con el controlador. ----------

	private void cargarCanciones(String path) {
		Controller.INSTANCE.cargarCanciones(path);
	}
}
