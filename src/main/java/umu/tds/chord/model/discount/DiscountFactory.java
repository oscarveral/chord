package umu.tds.chord.model.discount;

import java.util.Date;

/**
 * Clase utilizada para la construcción de descuentos.
 */
public class DiscountFactory {

	/**
	 * Enumerado que recoge todos los tipos de descuentos.
	 */
	public enum Type {
		NONE, ELDER, TEMPORARY;
	}

	/**
	 * Método para crear descuentos de forma más específica.
	 * 
	 * @param start Fecha de comienzo de aplicación del descuento.
	 * @param end   Fecha de fin de aplicación del descuento.
	 * @param t     Tipo de descuento que se desea crear.
	 * 
	 * @return Descuento creado.
	 */
	public static Discount createDiscount(Date start, Date end, Type t) {
		switch (t) {
		case NONE:
			return new Discount(start, end);
		case ELDER:
			return new ElderDiscount(start, end);
		case TEMPORARY:
			return new TemporaryDiscount(start, end);
		default:
			throw new IllegalArgumentException("Unexpected value: " + t);
		}
	}

	/**
	 * Crea un nuevo descuento basandose en el tipo de descuento proporcionado. El
	 * descuento será nuevo completamente, de forma que si tiene una caducidad de
	 * dos meses, caducará a los 2 meses de su cración mediante este método.
	 * 
	 * @param t Tipo de descuento que se desea crear.
	 * 
	 * @return Descuento creado.
	 */
	public static Discount createDiscount(Type t) {
		switch (t) {
		case NONE:
			return new Discount();
		case ELDER:
			return new ElderDiscount();
		case TEMPORARY:
			return new TemporaryDiscount();
		default:
			throw new IllegalArgumentException("Unexpected value: " + t);
		}
	}
}
