package umu.tds.chord.dao.tds;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.chord.dao.DAO;
import umu.tds.chord.model.Persistent;
import umu.tds.chord.model.Song;

/**
 * Adaptador {@link DAO} para {@link Song.Internal} utilizando el driver de
 * persistencia de TDS.
 */
public enum TDSSongDAO implements DAO<Song.Internal> {

	/**
	 * Patrón Singleton. Instancia única de este adaptador.
	 */
	INSTANCE;

	/**
	 * Enumerado de todas las propiedades que tiene una entidad canción.
	 */
	private enum Properties {
		AUTHOR, NAME, PATH, SONG_ENTITY_TYPE, STYLE, REPRODUCCIONES
	}

	private final ServicioPersistencia persistence = FactoriaServicioPersistencia.getInstance()
			.getServicioPersistencia();

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code true} si la eliminación fue exitosa. {@code false} si el
	 *         objeto es {@code null}, no está registrado o existen inconsistencias
	 *         en el servicio de persistencia.
	 */
	@Override
	public boolean delete(Song.Internal s) {
		// Checks obligatorios
		if (s == null || !s.isRegistered()) {
			return false;
		}

		// Si la entidad no es de canción hay inconsistencias.
		Entidad eSong = persistence.recuperarEntidad(s.getId());
		if (!eSong.getNombre().equals(Properties.SONG_ENTITY_TYPE.name())) {
			return false;
		}

		// Eliminar de la pool.
		TDSPoolDAO.removePersistent(s);

		// Eliminación de la canción.
		return persistence.borrarEntidad(eSong);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el
	 *         objeto es {@code null}, no está registrado o existen inconsistencias
	 *         en el servicio de persistencia.
	 */
	@Override
	public boolean modify(Song.Internal s) {
		// Checks obligatorios
		if (s == null || !s.isRegistered()) {
			return false;
		}

		// Si la entidad recuperada no es de canción hay inconsistencias.
		Entidad eSong = persistence.recuperarEntidad(s.getId());
		if (!eSong.getNombre().equals(Properties.SONG_ENTITY_TYPE.name())) {
			return false;
		}

		// Modificación de propiedades de la entidad.
		eSong.getPropiedades().forEach(p -> {

			switch (Properties.valueOf(p.getNombre())) {
			// No se puden mutar las canciones.
			case NAME, AUTHOR, PATH, STYLE:
				break;
			case REPRODUCCIONES:
				p.setValor(String.valueOf(s.getReproducciones()));
				break;
			default:
				break;
			}
			persistence.modificarPropiedad(p);
		});
		persistence.modificarEntidad(eSong);

		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return Devuelve el objeto identificado por id en persistencia en el caso de
	 *         estar registrado y ser de tipo canción.
	 */
	@Override
	public Optional<Song.Internal> recover(int id) {

		// Comprobamos si la pool ya tenía el objeto y es del tipo canción.
		if (TDSPoolDAO.contains(id)) {
			Persistent cached = TDSPoolDAO.getPersistent(id).get();
			if (cached.getClass() == Song.Internal.class) {
				return Optional.of((Song.Internal) cached);
			} else {
				return Optional.empty();
			}
		}

		// Recuperación de la entidad.
		Entidad eSong = persistence.recuperarEntidad(id);
		// Asegurar que el tipo es de usuario.
		if (!eSong.getNombre().equals(Properties.SONG_ENTITY_TYPE.name())) {
			return Optional.empty();
		}

		// Recuperación de propiedades.
		String name = null;
		String author = null;
		String path = null;
		String style = null;
		int reproducciones = 0;

		name = persistence.recuperarPropiedadEntidad(eSong, Properties.NAME.name());
		author = persistence.recuperarPropiedadEntidad(eSong, Properties.AUTHOR.name());
		path = persistence.recuperarPropiedadEntidad(eSong, Properties.PATH.name());
		style = persistence.recuperarPropiedadEntidad(eSong, Properties.STYLE.name());
		reproducciones = Integer
				.valueOf(persistence.recuperarPropiedadEntidad(eSong, Properties.REPRODUCCIONES.name()));

		// Construcción y registro.
		Song.Internal s = new Song.Builder(name).author(author).path(path).style(style).reproducciones(reproducciones)
				.build().get().asMut();

		s.registerId(id);
		TDSPoolDAO.addPersistent(s);

		return Optional.of(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Song.Internal> recoverAll() {
		return persistence.recuperarEntidades(Properties.SONG_ENTITY_TYPE.name()).stream()
				.map(entity -> this.recover(entity.getId())).filter(Optional::isPresent).map(Optional::get) // Ignorar
																											// errores.
				.toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean register(Song.Internal s) {

		// Checks obligatorios.
		if (s == null || s.isRegistered()) {
			return false;
		}

		// Comprobar si ya está registrado.
		Entidad eSong = null;
		try {
			eSong = persistence.recuperarEntidad(s.getId());
		} catch (NullPointerException e) {
		}
		if (eSong != null) {
			return false;
		}

		// Creación de la entidad.
		eSong = new Entidad();
		eSong.setNombre(Properties.SONG_ENTITY_TYPE.name());

		// Propiedades.
		eSong.setPropiedades(Arrays.asList(new Propiedad(Properties.NAME.name(), s.getName()),
				new Propiedad(Properties.AUTHOR.name(), s.getAuthor()),
				new Propiedad(Properties.PATH.name(), s.getPath()),
				new Propiedad(Properties.STYLE.name(), s.getStyle()),
				new Propiedad(Properties.REPRODUCCIONES.name(), String.valueOf(s.getReproducciones()))));

		// Registro.
		eSong = persistence.registrarEntidad(eSong);
		s.registerId(eSong.getId());
		TDSPoolDAO.addPersistent(s);

		return true;
	}
}
