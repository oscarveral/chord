package umu.tds.chord.dao.tds;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.chord.dao.DAO;
import umu.tds.chord.dao.DAOFactory;
import umu.tds.chord.dao.DAOFactory.DAOImplementation;
import umu.tds.chord.model.Mutable;
import umu.tds.chord.model.Persistent;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.User;
import umu.tds.chord.model.discount.Discount;
import umu.tds.chord.model.discount.DiscountFactory;

/**
 * Adaptador {@link DAO} para {@link User} utilizando el driver de persistencia
 * de TDS.
 */
public enum TDSUserDAO implements DAO<User.Internal> {

	/**
	 * Patrón Singleton. Instancia única de este adaptador.
	 */
	INSTANCE;

	/**
	 * Enumerado de todas las propiedades que tiene una entidad usuario.
	 */
	private enum Properties {
		BIRTHDAY, FAVOURITE_SONGS, PASSWORD_HASH, PLAYLISTS, PREMIUM, RECENT_SONGS, USER_ENTITY_TYPE, USER_NAME,
		DISCOUNT_TYPE, DISCOUNT_START, DISCOUNT_END
	}

	private final ServicioPersistencia persistence = FactoriaServicioPersistencia.getInstance()
			.getServicioPersistencia();

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el
	 *         objeto es {@code null}, no está registrado o existen inconsistencias
	 *         en el servicio de persistencia.
	 */
	@Override
	public boolean delete(User.Internal u) {

		// Checks obligatorios
		if ((u == null) || !u.isRegistered()) {
			return false;
		}

		// Si la entidad no es de usuario hay inconsistencias.
		Entidad eUser = persistence.recuperarEntidad(u.getId());
		if (!eUser.getNombre().equals(Properties.USER_ENTITY_TYPE.name())) {
			return false;
		}

		// Eliminación en cascada de las playlists asociadas al usuario.
		u.getPlaylists()
				.forEach(p -> DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getPlaylistDAO().delete(p.asMut()));

		// Eliminar de la pool.
		TDSPoolDAO.removePersistent(u);

		// Eliminación del usuario.
		return persistence.borrarEntidad(eUser);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el
	 *         objeto es {@code null}, no está registrado o existen inconsistencias
	 *         en el servicio de persistencia.
	 */
	@Override
	public boolean modify(User.Internal u) {

		// Checks obligatorios
		if ((u == null) || !u.isRegistered()) {
			return false;
		}

		// Si la entidad recuperada no es de usuario hay inconsistencias.
		Entidad eUser = persistence.recuperarEntidad(u.getId());
		if (!eUser.getNombre().equals(Properties.USER_ENTITY_TYPE.name())) {
			return false;
		}

		eUser.getPropiedades().forEach(p -> {

			switch (Properties.valueOf(p.getNombre())) {
			case USER_NAME:
				p.setValor(u.getUserName());
				break;
			case PASSWORD_HASH:
				p.setValor(u.getHashedPassword());
				break;
			case PREMIUM:
				p.setValor(String.valueOf(u.isPremium()));
				break;
			case BIRTHDAY:
				p.setValor(u.getBirthday().toInstant().toString());
				break;
			case PLAYLISTS:

				// Recuperar playlists guardadas.
				String playlistsStr = p.getValor();
				List<Playlist.Internal> oldPlaylists = new ArrayList<>(DAOFactory
						.getInstance(DAOImplementation.TDS_FAMILY).getPlaylistDAO().stringToPersistents(playlistsStr));

				// Crear lista con ids actuales de las playlists.
				List<Playlist.Internal> newPlaylists = new ArrayList<>(
						u.getPlaylists().stream().map(Mutable::asMut).toList());

				newPlaylists.forEach(playlist -> {
					// Las ya registradas son ignoradas por el dao.
					DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getPlaylistDAO().register(playlist);
					DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getPlaylistDAO().modify(playlist);
				});

				// Eliminar playlists que ya no están presentes.
				List<Playlist.Internal> removedPlaylists = new ArrayList<>(oldPlaylists);
				removedPlaylists.removeAll(newPlaylists);
				removedPlaylists.forEach(playlist -> DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
						.getPlaylistDAO().delete(playlist));

				p.setValor(DAO.persistentsToString(
						// Necesito la versión interna de las playlist para
						// poder obtener sus ids en persistencia.
						u.getPlaylists().stream().map(Mutable::asMut).toList()));
				break;
			case RECENT_SONGS:
				p.setValor(DAO.persistentsToString(
						// Necesito la versión interna de las canciones para
						// poder obtener sus ids en persistencia.
						u.getRecentSongs().stream().map(Mutable::asMut).toList()));
				break;
			case FAVOURITE_SONGS:
				p.setValor(DAO.persistentsToString(
						// Necesito la versión interna de las canciones para
						// poder obtener sus ids en persistencia.
						u.getFavouriteSongs().stream().map(Mutable::asMut).toList()));
				break;
			case DISCOUNT_TYPE:
				p.setValor(u.getDiscount().getType().name());
				break;
			case DISCOUNT_START:
				p.setValor(u.getDiscount().getStart().toInstant().toString());
				break;
			case DISCOUNT_END:
				p.setValor(u.getDiscount().getEnd().toInstant().toString());
				break;
			default:
				break;
			}

			persistence.modificarPropiedad(p);

		});

		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return Devuelve el objeto identificado por id en persistencia en el caso de
	 *         estar registrado y ser de tipo usuario.
	 */
	@Override
	public Optional<User.Internal> recover(int id) {

		// Comprobamos si la pool ya tenía el objeto y es del tipo usuario.
		if (TDSPoolDAO.contains(id)) {
			Persistent cached = TDSPoolDAO.getPersistent(id).get();
			if (cached.getClass() == User.Internal.class) {
				return Optional.of((User.Internal) cached);
			} else {
				return Optional.empty();
			}
		}

		// Si la entidad recuperada no está en el pool.
		Entidad eUser = persistence.recuperarEntidad(id);

		// Asegurar que el tipo es de usuario.
		if (!eUser.getNombre().equals(Properties.USER_ENTITY_TYPE.name())) {
			return Optional.empty();
		}

		// Propiedades primitivas que recuperar.
		String userName = null;
		String passwordHash = null;
		Date birthday = null;
		boolean premium = false;
		Date discountStart = null;
		Date discountEnd = null;
		DiscountFactory.Type discountType = null;

		// Recuperación de primitivas.
		userName = persistence.recuperarPropiedadEntidad(eUser, Properties.USER_NAME.name());
		passwordHash = persistence.recuperarPropiedadEntidad(eUser, Properties.PASSWORD_HASH.name());

		try {
			birthday = Date
					.from(Instant.parse(persistence.recuperarPropiedadEntidad(eUser, Properties.BIRTHDAY.name())));
			discountStart = Date.from(
					Instant.parse(persistence.recuperarPropiedadEntidad(eUser, Properties.DISCOUNT_START.name())));
			discountEnd = Date
					.from(Instant.parse(persistence.recuperarPropiedadEntidad(eUser, Properties.DISCOUNT_END.name())));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}

		premium = Boolean.valueOf(persistence.recuperarPropiedadEntidad(eUser, Properties.PREMIUM.name()));
		discountType = DiscountFactory.Type
				.valueOf(persistence.recuperarPropiedadEntidad(eUser, Properties.DISCOUNT_TYPE.name()));

		Discount discount = DiscountFactory.createDiscount(discountStart, discountEnd, discountType);

		// Obtención de las cadenas con los objetos referenciados.
		String playlistsStr = persistence.recuperarPropiedadEntidad(eUser, Properties.PLAYLISTS.name());
		String recentSongsStr = persistence.recuperarPropiedadEntidad(eUser, Properties.RECENT_SONGS.name());
		String favouriteSongsStr = persistence.recuperarPropiedadEntidad(eUser, Properties.FAVOURITE_SONGS.name());

		// Añadir cancioens recientes.
		List<Song> recentSongs = DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getSongDAO()
				.stringToPersistents(recentSongsStr).stream().map(s -> (Song) s).toList();

		// Creación de la representación interna del usuario.
		User.Internal user = new User.Builder(userName).hashedPassword(passwordHash).premium(premium).birthday(birthday)
				.discount(discount).recentSongs(recentSongs).build().get().asMut();

		// Establecimiento de id y carga en la pool.
		user.registerId(id);
		TDSPoolDAO.addPersistent(user.asMut());

		// Añadir playlists.
		DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getPlaylistDAO().stringToPersistents(playlistsStr)
				.forEach(user::addPlaylist);

		// Añadir canciones favoritas.
		DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getSongDAO().stringToPersistents(favouriteSongsStr)
				.forEach(user::addFavouriteSong);

		// Retorno del objeto.
		return Optional.of(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User.Internal> recoverAll() {
		return persistence.recuperarEntidades(Properties.USER_ENTITY_TYPE.name()).stream()
				.map(entity -> this.recover(entity.getId())).filter(Optional::isPresent).map(Optional::get).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean register(User.Internal user) {

		// Checks iniciales obligatorios.
		if ((user == null) || user.isRegistered()) {
			return false;
		}

		// Comporbar si ya estaba registrado.
		Entidad eUser = null;
		try {
			eUser = persistence.recuperarEntidad(user.getId());
		} catch (NullPointerException e) {
		}
		if (eUser != null) {
			return false;
		}

		// Registro de playlists asociadas al usuario.
		user.getPlaylists().forEach(p -> DAOFactory.getInstance(DAOImplementation.TDS_FAMILY).getPlaylistDAO()
				// Debo registrar la versión interna de la playlist.
				.register(p.asMut()));

		// Creación de la entidad.
		eUser = new Entidad();
		eUser.setNombre(Properties.USER_ENTITY_TYPE.name());

		// Propiedades.
		eUser.setPropiedades(Arrays.asList(new Propiedad(Properties.USER_NAME.name(), user.getUserName()),
				new Propiedad(Properties.PASSWORD_HASH.name(), user.getHashedPassword()),
				new Propiedad(Properties.BIRTHDAY.name(), user.getBirthday().toInstant().toString()),
				new Propiedad(Properties.PLAYLISTS.name(), DAO.persistentsToString(
						// Necesito la versión interna de las playlist para
						// poder obtener sus ids en persistencia.
						user.getPlaylists().stream().map(Mutable::asMut).toList())),
				new Propiedad(Properties.RECENT_SONGS.name(), DAO.persistentsToString(
						// Necesito la versión interna de las canciones para
						// poder obtener sus ids en persistencia.
						user.getRecentSongs().stream().map(Mutable::asMut).toList())),
				new Propiedad(Properties.FAVOURITE_SONGS.name(), DAO.persistentsToString(
						// Necesito la versión interna de las canciones para
						// poder obtener sus ids en persistencia.
						user.getFavouriteSongs().stream().map(Mutable::asMut).toList())),
				new Propiedad(Properties.PREMIUM.name(), String.valueOf(user.isPremium())),
				new Propiedad(Properties.DISCOUNT_TYPE.name(), user.getDiscount().getType().name()),
				new Propiedad(Properties.DISCOUNT_START.name(), user.getDiscount().getStart().toInstant().toString()),
				new Propiedad(Properties.DISCOUNT_END.name(), user.getDiscount().getEnd().toInstant().toString())));

		// Registro de la entidad y establecimiento del id.
		eUser = persistence.registrarEntidad(eUser);
		user.registerId(eUser.getId());
		TDSPoolDAO.addPersistent(user);

		return true;
	}
}
