package umu.tds.chord.dao;

/**
 * Clase abstracta que implementan aquellos elementos del modelo que son 
 * persistentes.
 */
public interface Persistent {
	
	/**
	 * Obtiene la id actual.
	 * 
	 * @return Id actual del objeto.
	 */
	public int getId();
	
	/**
	 * Indica si al objeto se le ha asignado anteriormente alguna id 
	 * persistente. Es decir, si se ha registrado mediante un DAO.
	 * 
	 * @return {@code true} si se le asignó una id en algun momento mediante
	 * {@link Persistent#registerId(int)}. 
	 * {@code false} en otro caso.
	 */
	public boolean isRegistered();
	
	/**
	 * Establece la id del objeto. Por defecto si el objeto ya estaba registrado
	 * no se hace nada.
	 * 
	 * @param uuid Id que se desea utilizar para el objeto.
	 */
	public default void registerId(int id) {
		if (isRegistered())
			return;
	}
}
