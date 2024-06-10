package umu.tds.chord.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;

public class ToolBar extends JMenuBar {

	private static final long serialVersionUID = 6159305177776118751L;
	private static final String utilidadesText = "Utilidades";
	private static final String genPDF = "Generar PDF";
	private static final String openFileDialogTitle = "Seleccionar ruta de destino";
	
	private JMenu utilidades;
	private JMenuItem pdf;
	
	public ToolBar() {
		initializeUtilities();
		
		registerControllerListeners();
	}
	
	private void initializeUtilities() {
		utilidades = new JMenu(utilidadesText);
		pdf = new JMenuItem(genPDF);
		setPDFGenerationStatus(false);
		pdf.addActionListener(e -> genPDF());
		utilidades.add(pdf);
		add(utilidades);
	}
	
	private void registerControllerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogin(UserStatusEvent e) {
				e.getUser().ifPresent(u -> setPDFGenerationStatus(u.isPremium()));
			}
			
			@Override
			public void onUserLogout(UserStatusEvent e) {
				setPDFGenerationStatus(false);
			}
			
			@Override
			public void onUserMetadataChange(UserStatusEvent e) {
				onUserLogin(e);
			}
		
		});
	}
	
	private void setPDFGenerationStatus(boolean available) {
		pdf.setFocusable(available);
		pdf.setEnabled(available);
	}
	
	private void genPDF() {
		UIManager.put("FileChooser.readOnly", Boolean.FALSE);
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setDialogTitle(openFileDialogTitle);
		
		int res = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(fileChooser));
		if (res == JFileChooser.APPROVE_OPTION) {
			// Obtener la ruta del directorio seleccionado.
			File f = fileChooser.getSelectedFile();
			try {
				String path = f.getCanonicalPath();
				Controller.INSTANCE.genPDF(path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}		
	}
}
