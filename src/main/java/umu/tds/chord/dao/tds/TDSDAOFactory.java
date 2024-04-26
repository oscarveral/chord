package umu.tds.chord.dao.tds;

import umu.tds.chord.dao.DAO;
import umu.tds.chord.dao.DAOFactory;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.User;

/**
 * Factoria para la familia de adaptadores DAO que utilizan el driver de
 * persistencia de TDS.
 */
public class TDSDAOFactory extends DAOFactory {

	/**
	 * {@inheritDoc}
	 *
	 * Implementación para la familia TDS. {@link TDSPlaylistDAO}.
	 */
	@Override
	public DAO<Playlist.Internal> getPlaylistDAO() {
		return TDSPlaylistDAO.INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Implementación para la familia TDS. {@link TDSSongDAO}.
	 */
	@Override
	public DAO<Song.Internal> getSongDAO() {
		return TDSSongDAO.INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Implementación para la familia TDS. {@link TDSUserDAO}.
	 */
	@Override
	public DAO<User.Internal> getUserDAO() {
		return TDSUserDAO.INSTANCE;
	}
}
