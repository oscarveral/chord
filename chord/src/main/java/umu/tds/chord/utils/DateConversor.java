package umu.tds.chord.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Clase de utilidad para el tratamiento de fechas.
 */
public class DateConversor {

	/**
	 * Método de conversión de fechas de {@link Date} a {@link LocalDateTime}.
	 * 
	 * @param date Fecha que se desea convertir.
	 * 
	 * @return Fecha convertida.
	 */
	public static LocalDateTime covertToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * Método de conversión de fechas de {@link LocalDateTime} a {@link Date}.
	 * 
	 * @param date Fecha que se desea convertir.
	 * 
	 * @return Fecha convertida.
	 */
	public static Date convertToDate(LocalDateTime date) {
		return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
	}
}
