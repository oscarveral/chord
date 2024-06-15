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
import umu.tds.chord.model.discount.DiscountFactory.Type;

public class ToolBar extends JMenuBar {

	private static final long serialVersionUID = 6159305177776118751L;
	private static final String utilidadesText = "Utilidades";
	private static final String genPDF = "Generar PDF";
	private static final String openFileDialogTitle = "Seleccionar ruta de destino";
	private static final String discountsText = "Aplicar descuento";
	private static final String noneDiscountText = "Ninguno";
	private static final String elderDiscountText = "Descuento del 50% para mayores de 65 años";
	private static final String temporaryDiscountText = "Descuento del 20% durante 3 meses";
	
	private JMenu utilidades;
	private JMenuItem pdf;
	private JMenu discounts;
	private JMenuItem noneDiscount;
	private JMenuItem elderDiscount;
	private JMenuItem temporaryDiscount;
	
	public ToolBar() {
		initializeUtilities();
		initializeDiscounts();
		
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
	
	private void initializeDiscounts() {
		discounts = new JMenu(discountsText);
		noneDiscount = new JMenuItem(noneDiscountText);
		noneDiscount.addActionListener(e -> applyDiscount(Type.NONE));
		elderDiscount = new JMenuItem(elderDiscountText);
		elderDiscount.addActionListener(e -> applyDiscount(Type.ELDER));
		temporaryDiscount = new JMenuItem(temporaryDiscountText);
		temporaryDiscount.addActionListener(e -> applyDiscount(Type.TEMPORARY));
		discounts.add(noneDiscount);
		discounts.add(elderDiscount);
		discounts.add(temporaryDiscount);
		add(discounts);
	}
	
	private void registerControllerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogin(UserStatusEvent e) {
				resetDiscounts();
				e.getUser().ifPresent(u -> {
					setPDFGenerationStatus(u.isPremium());	
					switch (u.getDiscount().getType()) {
					case NONE:
						noneDiscount.setEnabled(false);
						break;
					case ELDER:
						elderDiscount.setEnabled(false);
						break;
					case TEMPORARY:
						temporaryDiscount.setEnabled(false);
						break;
					default:
						throw new IllegalArgumentException("Unexpected value.");
					}
				});
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
		
		int res = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(this));
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
	
	private void applyDiscount(Type t) {
		Controller.INSTANCE.changeUserDiscount(t);
	}
	
	private void resetDiscounts() {
		noneDiscount.setEnabled(true);
		elderDiscount.setEnabled(true);
		temporaryDiscount.setEnabled(true);
	}
}
