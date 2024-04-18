package umu.tds.chord.dao.tds;

import java.util.HashMap;
import java.util.Optional;

import umu.tds.chord.model.Persistent;

/**
 * Pool interna de objetos ya obtenidos del servidor de persistencia para la 
 * familia de adaptadores TDS.
 * 
 * Patrón Singleton.
 * 
 * Dado que nunca se necesitará obtener una instancia de la clase utilizando 
 * genericidad implementando interfaces, podemos declarar sus métodos estáticos 
 * por comodidad y no utilizar un enumerado.
 */
public final class TDSPoolDAO {

	private static HashMap<Integer, Persistent> pool = 
			new HashMap<Integer, Persistent>();

	
	/**
	 * Registra el objeto en la pool asociandolo a la id dada.
	 * 
	 * @param o Objeto {@link Persistent} que se desea almacenar en la pool.
	 * 
	 * @return {@code true} si se registró el objeto con éxito. {@code false}
	 *  si el objeto es {@code null} o si ya existía un mapeo para la id dada. 
	 */
	protected static boolean addPersistent(Persistent o) {
		if (o == null) return false;
		if (contains(o.getId())) return false;
		
		pool.put(o.getId(), o);
		
		return true;
	}
	
	/**
	 * Elimina el objeto de la pool.
	 * 
	 * @param o Objeto persistente que se desea eliminar de la pool.
	 * 
	 * @return {@code true} si se ha eliminado el objeto.
	 */
	protected static boolean removePersistent(Persistent o) {
		if (o == null) return false;
		if (!contains(o.getId())) return false;
		
		pool.remove(o.getId());
		
		return true;
	}
	
	/**
	 * Obtiene el objeto asociado a la id dada si exsite.
	 * 
	 * @param uuid Id del objeto deseado.
	 * @return Retorna el objeto asociado a la id en caso de que exista un 
	 * mapeo para la id.
	 */
	protected static Optional<Persistent> getPersistent(int id) {
		return Optional.ofNullable(pool.get(id));
	}
	
	/**
	 * Consulta si la pool tiene un mapeo para la id dada.
	 * 
	 * @param uuid Id que se desea consultar.
	 * @return {@code true} si existe el mapeo. {@code false} en otro caso. 
	 */
	protected static boolean contains(int id) {
		return pool.containsKey(id);
	}
}