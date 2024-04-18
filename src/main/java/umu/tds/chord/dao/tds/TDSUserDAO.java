package umu.tds.chord.dao.tds;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.chord.dao.DAO;
import umu.tds.chord.dao.DAOFactory;
import umu.tds.chord.dao.DAOFactory.DAOImplementation;
import umu.tds.chord.model.Persistent;
import umu.tds.chord.model.User;
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
	private enum UserProperties {
		USER_ENTITY_TYPE,
		USER_NAME,
		PASSWORD_HASH,
		BIRTHDAY,
		PLAYLISTS,
		RECENT_SONGS,
		PREMIUM,
	}
	
	private final ServicioPersistencia persistence = 
			FactoriaServicioPersistencia.getInstance()
										.getServicioPersistencia();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean register(User.Internal user) {
		
		// Checks iniciales obligatorios.
		if (user == null) return false;
		if (user.isRegistered()) return false;
				
		// Comporbar si ya estaba registrado.
		Entidad eUser = null;
		try {
			eUser = persistence.recuperarEntidad(user.getId());
		} 
		catch (NullPointerException e) {}
		if (eUser != null) return false;
		
		// Registro de playlists asociadas al usuario.
		user.getPlaylists().forEach(p -> {
			DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
				.getPlaylistDAO()
				// Debo registrar la versión interna de la playlist.
				.register(p.asMut());
		});

		// Creación de la entidad.
		eUser = new Entidad();
		eUser.setNombre(UserProperties.USER_ENTITY_TYPE.name());
		
		// Propiedades.
		eUser.setPropiedades(Arrays.asList(
			new Propiedad(UserProperties.USER_NAME.name(), 
					user.getUserName()),
			new Propiedad(UserProperties.PASSWORD_HASH.name(), 
					user.getHashedPassword()),
			new Propiedad(UserProperties.BIRTHDAY.name(), 
					user.getBirthday().toString()),
			new Propiedad(UserProperties.PLAYLISTS.name(),
					DAO.persistentsToString(
					// Necesito la versión interna de las playlist para
					// poder obtener sus ids en persistencia.
					user.getPlaylists().stream()
						.map(p -> p.asMut())
						.toList())
			),
			new Propiedad(UserProperties.RECENT_SONGS.name(), 
					DAO.persistentsToString(
					// Necesito la versión interna de las canciones para
					// poder obtener sus ids en persistencia.
					user.getRecentSongs().stream()
						.map(s-> s.asMut())
						.toList())
			),
			new Propiedad(UserProperties.PREMIUM.name(), 
					String.valueOf(user.isPremium()))
		));
		
		// Registro de la entidad y establecimiento del id.
		eUser = persistence.registrarEntidad(eUser);
				
		user.registerId(eUser.getId());
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return Devuelve el objeto identificado por id en persistencia en el caso
	 * de estar registrado y ser de tipo usuario.
	 */
	@Override
	public Optional<User.Internal> recover(int id) {
		
		// Comprobamos si la pool ya tenía el objeto y es del tipo usuario.
		if (TDSPoolDAO.contains(id)) {
			Persistent cached = TDSPoolDAO.getPersistent(id).get();
			if (cached.getClass() == User.Internal.class) 
				return Optional.of((User.Internal) cached);
			else return Optional.empty();
		}
		
		// Si la entidad recuperada no está en el pool.
		Entidad eUser = persistence.recuperarEntidad(id);
								
		// Asegurar que el tipo es de usuario.
		if (!eUser.getNombre().equals(UserProperties.USER_ENTITY_TYPE.name()))
			return Optional.empty();
				
		// Propiedades primitivas que recuperar.
		String userName = null;
		String passwordHash = null;
		LocalDate birthday = null;
		boolean premium = false;
		
		// Recuperación de primitivas.
		userName = persistence.recuperarPropiedadEntidad
				(eUser, UserProperties.USER_NAME.name());
		passwordHash = persistence.recuperarPropiedadEntidad
				(eUser, UserProperties.PASSWORD_HASH.name());
		
		try {
			birthday = LocalDate.parse(persistence.recuperarPropiedadEntidad
					(eUser, UserProperties.BIRTHDAY.name()));
		} 
		catch (DateTimeParseException e) {
			return Optional.empty();
		}
				
		premium = Boolean.valueOf(persistence.recuperarPropiedadEntidad
				(eUser,UserProperties.PREMIUM.name()));

		// Creación de la representación interna del usuario. 
		User.Internal user = new User.Builder(userName)
							.hashedPassword(passwordHash)
							.premium(premium)
							.birthday(birthday)
							.build()
							.get()
							.asMut();

		// Establecimiento de id y carga en la pool.
		user.registerId(id);
		TDSPoolDAO.addPersistent(user.asMut());
		
		// Obtención de las cadenas con los objetos referenciados.
		String playlistsStr = persistence.recuperarPropiedadEntidad(eUser, 
				UserProperties.PLAYLISTS.name());
		String recentSongsStr = persistence.recuperarPropiedadEntidad(eUser,
				UserProperties.RECENT_SONGS.name());
		
		// Añadir playlists.
		DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
			.getPlaylistDAO()
			.stringToPersistents(playlistsStr)
			.forEach(p -> user.addPlaylist(p));
		
		// Añadir cancioens recientes.
		DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
			.getSongDAO()
			.stringToPersistents(recentSongsStr)
			.forEach(s -> user.addRecentSong(s));
		
		// Retorno del objeto.
		return Optional.of(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User.Internal> recoverAll() {
		return persistence.recuperarEntidades(UserProperties.USER_ENTITY_TYPE.name())
				.stream()
				.map(entity -> this.recover(entity.getId()).orElse(null))
				.filter(u -> u!=null)
				.collect(Collectors.toUnmodifiableList());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el 
	 * objeto es {@code null}, no está registrado o existen inconsistencias en 
	 * el servicio de persistencia.
	 */
	@Override
	public boolean modify(User.Internal u) {
		
		// Checks obligatorios
		if (u == null) return false;
		if (!u.isRegistered()) return false;
		
		// Si la entidad recuperada no es de usuario hay inconsistencias.
		Entidad eUser = persistence.recuperarEntidad(u.getId());
		if (!eUser.getNombre().equals(UserProperties.USER_ENTITY_TYPE.name()))
			return false;
		
		eUser.getPropiedades().forEach(p -> {
			
			switch (UserProperties.valueOf(p.getNombre())) {
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
				p.setValor(u.getBirthday().toString());
				break;
			case PLAYLISTS:
				
				/* 
				 * Se deben eliminar las playlist que ya no se encuentren
				 * asociadas al usuario, registrar las nuevas playlists y
				 * actualizar el resto.
				 */
				
				p.setValor(DAO.persistentsToString(
						// Necesito la versión interna de las playlist para
						// poder obtener sus ids en persistencia.
						u.getPlaylists().stream()
							.map(l -> l.asMut())
							.toList()
					)
				);
				break;
			case RECENT_SONGS:
				p.setValor(DAO.persistentsToString(
						// Necesito la versión interna de las canciones para
						// poder obtener sus ids en persistencia.
						u.getRecentSongs().stream()
							.map(s-> s.asMut())
							.toList()
					)
				);
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
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el
	 * objeto es {@code null}, no está registrado o existen inconsistencias en
	 * el servicio de persistencia.
	 */
	@Override
	public boolean delete(User.Internal u) {
		
		// Checks obligatorios
		if (u == null) return false;
		if (!u.isRegistered()) return false;		
		
		// Si la entidad no es de usuario hay inconsistencias.
		Entidad eUser = persistence.recuperarEntidad(u.getId());
		if (eUser.getNombre() != UserProperties.USER_ENTITY_TYPE.name())
			return false;
		
		// Eliminación en cascada de las playlists asociadas al usuario.
		u.getPlaylists().forEach(p -> {
			DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
				.getPlaylistDAO()
				.delete(p.asMut());	
		});
		
		/* 
		 * Eliminar un usuario no debe suponer la eliminación de las canciones
		 * en su lista de recientes ya que podrían estar siendo referenciadas
		 * por otras entidades persistentes.
		 * 
			u.getRecentSongs().forEach(s -> {
				DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
					.getSongDAO()
					.delete(s.asMut());	
			});
		*/
		
		// Eliminación del usuario.
		return persistence.borrarEntidad(eUser);
	}
}
