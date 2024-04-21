package umu.tds.chord.dao.tds;

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
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;

/**
 * Adaptador {@link DAO} para {@link Playlist.Internal} utilizando el driver de 
 * persistencia de TDS.
 */
public enum TDSPlaylistDAO implements DAO<Playlist.Internal>{
	
	/**
	 * Patrón Singleton. Instancia única de este adaptador.
	 */
	INSTANCE;

	/**
	 * Enumerado de todas las propiedades que tiene una entidad playlist.
	 */
	private enum Properties {
		PLAYLIST_ENTITY_TYPE,
		NAME,
		DESCRIPTION,
		SONGS,
	}
	
	private final ServicioPersistencia persistence = 
			FactoriaServicioPersistencia.getInstance()
										.getServicioPersistencia();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean register(Playlist.Internal p) {
		
		// Checks obligatorios.
		if (p == null || p.isRegistered()) return false;
		
		// Comprobar si ya está registrado.
		Entidad ePlaylist = null;
		try {
			ePlaylist = persistence.recuperarEntidad(p.getId());
		} 
		catch (NullPointerException e) {}
		if (ePlaylist != null) return false;
		
		// Creación de la entidad.
		ePlaylist = new Entidad();
		ePlaylist.setNombre(Properties.PLAYLIST_ENTITY_TYPE.name());
		
		// Propiedades.
		ePlaylist.setPropiedades(Arrays.asList(
			new Propiedad(Properties.NAME.name(), p.getName()),
			new Propiedad(Properties.DESCRIPTION.name(), p.getDescription()),
			new Propiedad(Properties.SONGS.name(), 
				DAO.persistentsToString(
					// Necesito la versión interna de las cancions para 
					// leer las ids en persistencia.
					p.getSongs().stream()
						.map(pl -> pl.asMut())
						.toList()
				)
			)
		));
		
		// Registro.
		ePlaylist = persistence.registrarEntidad(ePlaylist);
		p.registerId(ePlaylist.getId());
		TDSPoolDAO.addPersistent(p);
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return Devuelve el objeto identificado por id en persistencia en el caso
	 * de estar registrado y ser de tipo playlist.
	 */
	@Override
	public Optional<Playlist.Internal> recover(int id) {
		
		// Comprobamos si la pool ya tenía el objeto y es del tipo canción.
		if (TDSPoolDAO.contains(id)) {
			Persistent cached = TDSPoolDAO.getPersistent(id).get();
			if (cached.getClass() == Song.Internal.class) 
				return Optional.of((Playlist.Internal) cached);
			else return Optional.empty();
		}
		
		// Recuperación de la entidad.
		Entidad ePlaylist = persistence.recuperarEntidad(id);
		// Asegurar que el tipo es de usuario.
		if (!ePlaylist.getNombre().equals(Properties.PLAYLIST_ENTITY_TYPE.name()))
			return Optional.empty();
		
		// Recuperación de propiedades.
		String name = null;
		String description = null;
		
		name = persistence.recuperarPropiedadEntidad
				(ePlaylist, Properties.NAME.name());
		description = persistence.recuperarPropiedadEntidad
				(ePlaylist, Properties.DESCRIPTION.name());
		
		// Recuperación de la lista de canciones.
		String recentSongsStr = persistence.recuperarPropiedadEntidad(ePlaylist,
				Properties.SONGS.name());
		
		// Añadir canciones.
		List<Song> songs = DAOFactory.getInstance(DAOImplementation.TDS_FAMILY)
					.getSongDAO()
					.stringToPersistents(recentSongsStr)
					.stream()
					.map(s -> (Song) s)
					.toList();
		
		// Creación de la playlist
		Playlist.Internal p = new Playlist.Builder(name)
				.description(description)
				.songs(songs)
				.build()
				.get()
				.asMut();
				
		p.registerId(id);
		TDSPoolDAO.addPersistent(p);
		
		return Optional.of(p);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Playlist.Internal> recoverAll() {
		return persistence.recuperarEntidades
				(Properties.PLAYLIST_ENTITY_TYPE.name())
				.stream()
				.map(entity -> this.recover(entity.getId()).orElse(null))
				.filter(u -> u!=null) // Ignorar errores.
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
	public boolean modify(Playlist.Internal p) {
		// Checks obligatorios
		if (p == null || !p.isRegistered()) return false;
		
		// Si la entidad recuperada no es de canción hay inconsistencias.
		Entidad ePlaylist = persistence.recuperarEntidad(p.getId());
		if (!ePlaylist.getNombre()
				.equals(Properties.PLAYLIST_ENTITY_TYPE.name()))
			return false;
		
		// Modificación de propiedades de la entidad.
		ePlaylist.getPropiedades().forEach(prop -> {
			
			switch (Properties.valueOf(prop.getNombre())) {
			case NAME:
				prop.setValor(p.getName());
				break;
			case DESCRIPTION:
				prop.setValor(p.getDescription());
				break;
			case SONGS:
				prop.setValor(DAO.persistentsToString(
					// Necesito la versión interna de las canciones.
					p.getSongs().stream()
						.map(s -> s.asMut())
						.toList()
				));
				break;
			default:
				break;
			}
			persistence.modificarPropiedad(prop);
		});
		persistence.modificarEntidad(ePlaylist);
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @return {@code true} si la eliminación fue exitosa. {@code false} si el
	 * objeto es {@code null}, no está registrado o existen inconsistencias en
	 * el servicio de persistencia.
	 */
	@Override
	public boolean delete(Playlist.Internal p) {
		// Checks obligatorios
		if (p == null || !p.isRegistered()) return false;		
		
		// Si la entidad no es de canción hay inconsistencias.
		Entidad ePlaylist = persistence.recuperarEntidad(p.getId());
		if (!ePlaylist.getNombre().equals(Properties.PLAYLIST_ENTITY_TYPE.name()))
			return false;
				
		// Eliminar de la pool
		TDSPoolDAO.removePersistent(p);
		
		// Eliminación de la canción.
		return persistence.borrarEntidad(ePlaylist);
	}
}
