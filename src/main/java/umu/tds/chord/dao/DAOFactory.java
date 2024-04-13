package umu.tds.chord.dao;

import umu.tds.chord.dao.tds.TDSDAOFactory;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.User;

/**
 * Interfaz de las factorias de creación de las familias de adaptadores DAO.
 */
public abstract class DAOFactory {

	private static final String unimplemented = "Unimplemented family factory "
			+ "on getInstance method.";

	/**
	 * Enumerado que lista las implementaciones de factorias para las diferentes
	 * familias de adaptadores DAO.
	 */
	public enum DAOImplementation {
		/**
		 * Familia de adapatadores DAO que utiliza el driver de persistencia 
		 * de TDS.
		 * 
		 * {@link TDSDAOFactory}
		 */
		TDS_FAMILY
	}
	
	private static DAOFactory instance = null;
	
	/**
	 * Retorna la instancia única de la factoria de adaptadores o 
	 * establece una instacia de la familia de adaptadores deseada
	 * como instancia única en caso de no haber sido inicializada.
	 * 
	 * @param implementation Familia de adaptadores DAO que se desea que 
	 * produzca la fábrica obtenida.
	 * 
	 * @return Fábrica de adaptadores actual o la fábrica de adaptadores de la
	 *  familia especificada en el caso de que la instancia única no esté 
	 *  inicializada.
	 * 
	 */
	public static DAOFactory getInstance(DAOImplementation implementation) {
		
		if (instance != null) return instance;
		
		switch (implementation) {
		case TDS_FAMILY:
			instance = new TDSDAOFactory();
			break;
		default:
			throw new RuntimeException(unimplemented);
		}
		
		return instance;
	}
	
	/**
	 * Obtiene la instancia única de la factoria de adaptadores. 
	 * Si es la primera vez que se invoca se inicializará la instancia única utilizando
	 * la familia {@link DAOImplementation#TDS_FAMILY}.
	 * 
	 * @return Instancia única de la fabrica de adaptadores.
	 */
	public static DAOFactory getInstance() {
		if (instance != null) return instance;
		return getInstance(DAOImplementation.TDS_FAMILY);
	}
	
	/**
	 * Obtiene el adaptador DAO para usuarios.
	 * 
	 * @return Implementación de {@link DAO} para {@link User}.
	 */
	public abstract DAO<User.Internal> getUserDAO();
	
	/**
	 * Obtiene el adaptador DAO para playlists.
	 * 
	 * @return Implementación de {@link DAO} para {@link Playlist}.
	 */
	public abstract DAO<Playlist.Internal> getPlaylistDAO();
	
	/**
	 * Obtiene el adaptador DAO para canciones.
	 * 
	 * @return Implementación de {@link DAO} para {@link Song}.
	 */
	public abstract DAO<Song.Internal> getSongDAO();
}
