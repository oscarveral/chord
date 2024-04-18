package umu.tds.chord.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import umu.tds.chord.model.Persistent;

/**
 * Interfaz DAO genérica
 * 
 * @param <T> Tipo sobre el que se implementará la interfaz DAO. Debe 
 * implementar la interfaz {@link Persistent}.
 */
public interface DAO<T extends Persistent> {
	
	/**
	 * Separador utilizado en las representaciones de listas de ids de objetos 
	 * persistentes {@link DAO#persistentsToString(List)}.
	 */
	public static final String REPRESENTATION_STRING_SEPARATOR = " ";
	
	/**
	 * Dada una lista de objetos persistentes obtiene una cadena de texto con 
	 * las ids de los objetos de la lista.
	 * 
	 * @param list Lista de la que se desea obtener la representacion en texto.
	 * 
	 * @return Cadena de texto con ids de los objetos persistentes.
	 */
	public static <T extends Persistent> String persistentsToString(List<T> list) {
		return list.stream()
				.map(p -> String.valueOf(p.getId()))
				.collect(Collectors.joining(REPRESENTATION_STRING_SEPARATOR));
	}
	
	/**
	 * Obtiene una lista de objetos persistentes a partir de una cadena de 
	 * texto que contiene la ids de los objetos a recuperar.
	 * 
	 * @param representation Cadena que indica los objetos que recuperar. La 
	 * cadena debe haberse generado mediante 
	 * {@link DAO#persistentsToString(List)}
	 * 
	 * @return Lista de objetos recuperados. Si algun id de la cadena de texto 
	 * no estaba registrado en persistencia o se falla en obtención es ignorado.
	 */
	public default List<T> stringToPersistents(String representation) {
		return Arrays.stream(representation.split(REPRESENTATION_STRING_SEPARATOR))
				.filter(s -> !s.isEmpty())
				.map(id -> recover(Integer.valueOf(id)))
				.filter(o -> o.isPresent()) // Ignorar fallos de obtención.
				.map(o -> o.get())
				.collect(Collectors.toList());
	}
	
	/**
	 * Registra un nuevo objeto persistente y establece la id persistente del 
	 * objeto proporcionado.
	 * 
	 * @param t Objeto que se desea registrar y que recibirá una id.
	 * 
	 * @return {@code true} si el registro es exitoso. {@code false} si t es 
	 * {@code null}, su id es mayor a 0 (solo se permite registrar objetos sin 
	 * id positivas) o ya se encontraba registrado.
	 * 
	 * @implNote La implementación por defecto proporciona la comprobación de
	 *  requisitos básicos comunes que, en su mayoría, comprobarían todas las 
	 *  implementaciones de esta interfaz. 
	 */
	public default boolean register(T t) {
		
		// Checks iniciales obligatorios.
		if (t == null) return false;
		if (t.isRegistered()) return false;
		
		// Continuar checks y registro en implementaciones.
		return true;
	}
	
	/**
	 * Obtiene el objeto identificado mediante el id dado.
	 * 
	 * @param id Identificador único del objeto que se desea recuperar.
	 * 
	 * @return Devuelve el objeto identificado por id en persistencia en el 
	 * caso de estar registrado.
	 */
	public Optional<T> recover(int id);
	
	/**
	 * Obtiene una lista con todos los objetos registrados.
	 * 
	 * @return Lista de solo lectura de todos los objetos de este tipo 
	 * registrados.
	 */
	public List<T> recoverAll();
	
	/**
	 * Modifica el objeto registrado para actualizar los datos persistentes.
	 * 
	 * @param t Objeto que se desea modificar en persistencia.
	 * 
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el 
	 * objeto es {@code null} o no está registrado.
	 * 
	 * @implNote La implementación por defecto proporciona la comprobación de 
	 * requisitos básicos comunes que, en su mayoría, comprobarían todas las 
	 * implementaciones de esta interfaz. 
	 */
	public default boolean modify(T t) {
		
		// Checks obligatorios
		if (t == null) return false;
		if (!t.isRegistered()) return false;
		
		// Continuar en las implementaciones.
		return true;
	}
	
	/**
	 * Elimina una entidad registrada en persistencia.
	 * 
	 * @param t Objeto que se desea eliminar de persistencia.
	 * 
	 * @return {@code true} si la eliminación fue exitosa. {@code false} si el
	 *  objeto es {@code null} o no está registrado.
	 * 
	 * @implNote La implementación por defecto proporciona la comprobación de 
	 * requisitos básicos comunes que, en su mayoría, comprobarían todas las 
	 * implementaciones de esta interfaz. 
	 */
	public default boolean delete(T t) {
		
		// Checks obligatorios
		if (t == null) return false;
		if (!t.isRegistered()) return false;
		
		// Continuar en las implementaciones.
		return true;
	}
}
