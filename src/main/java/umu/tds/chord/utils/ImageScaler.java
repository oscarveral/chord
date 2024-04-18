package umu.tds.chord.utils;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Clase de utilidad utilizada para la carga y escalado de imágenes.
 */
public final class ImageScaler {

	/**
	 * Crea un icono del tamaño dado con la imagen en la ruta especificada.
	 * 
	 * @param path Ruta de la imagen desde el directorio raiz de recursos 
	 * /src/main/resources del proyecto Maven.
	 * @param width Ancho deseado para la imagen.
	 * @param height Alto deseado para la imagen.
	 * 
	 * @return El icono con la imagen y tamaño deseados.
	 */
	public static ImageIcon loadImageIcon(String path, int width, int height) {
		ImageIcon icon = new ImageIcon(ImageScaler.class.getResource(path));
		Image iconImage = icon.getImage();
		Image newIconImage = iconImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		icon.setImage(newIconImage);
		return icon;
	}
}
