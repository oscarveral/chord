package umu.tds.chord.model.discount;

import java.util.Date;

import umu.tds.chord.model.User;

/**
 * Clase base que representa un descuento.
 */
public class Discount {

	private Date start;
	private Date end;

	protected Discount(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	protected Discount() {
		this.start = new Date();
		this.end = new Date();
	}

	/**
	 * Obtiene el factor de descuento por el que se debe multiplicar el precio para
	 * obtener el precio final.
	 * 
	 * @return Factor de descuento.
	 */
	public double getDiscountFactor() {
		return 1.0;
	}

	/**
	 * Indica si el descuento es aplicable al usuario proporcionado.
	 * 
	 * @param u Usuario sobre el que se quiere aplicar el descuento.
	 * 
	 * @return {@code true} si el descuento es aplicable.
	 */
	public boolean aplicable(User u) {
		return true;
	}

	/**
	 * Obtiene el tipo de descuento.
	 * 
	 * @return Tipo de descuento.
	 */
	public DiscountFactory.Type getType() {
		return DiscountFactory.Type.NONE;
	}

	/**
	 * Obtiene la fecha de comienzo de aplicación del decuento.
	 * 
	 * @return Fecha de comienzo de aplicación.
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Obtiene la fecha de fin de aplicación del descuento.
	 * 
	 * @return Fecha de fin de aplicación.
	 */
	public Date getEnd() {
		return end;
	}
}
