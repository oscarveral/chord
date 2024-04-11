package umu.tds.chord.dao;

/**
 * Clase abstracta que implementan aquellos elementos del modelo que son 
 * persistentes.
 */
public abstract class DAOPersistent {
	
	private int uuid;
	private boolean registered;
	
	protected DAOPersistent() {
		uuid = 0;
		registered = false;
	}
	
	/**
	 * Obtiene la id actual.
	 * 
	 * @return Id actual del objeto.
	 */
	public int getId() {
		return uuid;
	}
	
	/**
	 * Establece la id del objeto y establece que ha sido registrado.
	 * 
	 * @param uuid Id que se desea utilizar para el objeto.
	 * 
	 * @implNote Función peligrosa. No usar en objetos ya registrados en 
	 * persistencia. Solo deberán usar este método los adaptadores DAO en el
	 * registro de una nuevo elemento.
	 */
	public void registerId(int uuid) {
		this.uuid = uuid;
		registered = true;
	}
	
	/**
	 * Indica si al objeto se le ha asignado anteriormente alguna id 
	 * persistente. Es decir, si se ha registrado mediante un DAO.
	 * 
	 * @return {@code true} si se le asignó una id en algun momento. 
	 * {@code false} en otro caso.
	 */
	public boolean isRegistered() {
		return registered;
	}
}
