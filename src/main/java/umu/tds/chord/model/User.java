package umu.tds.chord.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import umu.tds.chord.model.discount.Discount;
import umu.tds.chord.model.discount.DiscountFactory;
import umu.tds.chord.model.discount.DiscountFactory.Type;
import umu.tds.chord.utils.StringHasher;

/**
 * Clase abstracta que representa un usuario. Expone sólo los métodos de lectura
 * de datos.
 */
public abstract sealed class User implements Mutable<User.Internal> {
	
	private static final DateFormat printableDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final double premiumBaseCost = 30.0;
	private static final int maxRecentSongs = 100;
	
	/**
	 * Clase constructora de usuarios.
	 */
	public static final class Builder {

		private Date birthday;
		private Set<Song> favouriteSongs;
		private String hashedPassword;
		private boolean isPremium;
		private List<Playlist> playlists;
		private Deque<Song> recentSongs;
		private String username;
		private Discount discount;

		/**
		 * Crea un nuevo builder de usuarios especificando el nombre de usuario. Se
		 * deberán establecer obligatoriamente contraseña y cumpleaños. Por defecto las
		 * listas de playlist y canciones recientes se inicializan vacías.
		 *
		 * @param username Nombre de usuario del nuevo usuario.
		 */
		public Builder(String username) {
			this.username = username;
			this.hashedPassword = null;
			this.birthday = null;
			this.playlists = new ArrayList<>();
			this.recentSongs = new ArrayDeque<>();
			this.favouriteSongs = new HashSet<>();
			this.isPremium = false;
			this.discount = DiscountFactory.createDiscount(Type.NONE);
		}

		/**
		 * Establece el cumpleaños del usuario.
		 *
		 * @param birthday Cumpleaños del usuario.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder birthday(Date birthday) {
			this.birthday = birthday;
			return this;
		}

		/**
		 * Construye un usuario a partir de la información actual.
		 *
		 * @return Usuario construido o un opcional vacío si no se ha proporcionado
		 *         contraseña o cumpleaños. Véase {@link User}.
		 */
		public Optional<User> build() {
			if (!validate()) {
				return Optional.empty();
			}

			return Optional.of(new User.Internal(this));
		}

		/**
		 * Establece el set de canciones favoritas del usuario.
		 *
		 * @param recentSongs Set de canciones favoritas del usuario. Véase
		 *                    {@link Song}.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder favouriteSongs(Set<Song> favouriteSongs) {
			this.favouriteSongs = favouriteSongs;
			return this;
		}

		/**
		 * Establece la contraseña del nuevo usuario.
		 *
		 * @param hashedPassword Contraseña ya hasheada del usuario.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder hashedPassword(String hashedPassword) {
			this.hashedPassword = hashedPassword;
			return this;
		}

		/**
		 * Establece la contraseña del nuevo usuario.
		 *
		 * @param password Contraseña en claro del usuario. Será hasheada. Véase
		 *                 {@link StringHasher}.
		 *
		 * @return Instancia acutal del builder.
		 */
		public Builder password(String password) {
			this.hashedPassword = StringHasher.hash(password);
			return this;
		}

		/**
		 * Establece la lista de playlist del usuario.
		 *
		 * @param playlists Lista de playlists del usuario. Véase {@link Playlist}.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder playlists(List<Playlist> playlists) {
			this.playlists = playlists;
			return this;
		}

		/**
		 * Establece el estado premium del usuario.
		 *
		 * @param isPremium Estado premium del usuario.
		 *
		 * @return Instancia acutal del builer.
		 */
		public Builder premium(boolean isPremium) {
			this.isPremium = isPremium;
			return this;
		}

		/**
		 * Establece la lista de canciones recientes del usuario.
		 *
		 * @param recentSongs Lista de canciones recientes del usuario. Véase
		 *                    {@link Song}.
		 *
		 * @return Instancia actual del builder.
		 */
		public Builder recentSongs(List<Song> recentSongs) {
			recentSongs.forEach(s -> this.recentSongs.addLast(s));
			return this;
		}
		
		/**
		 * Establece el descuento que está utilizando el usuario.
		 * 
		 * @param discount Descuento que se aplicará para el usuario.
		 * 
		 * @return Instancia actual del builder.
		 */
		public Builder discount(Discount discount) {
			this.discount = discount;
			return this;
		}

		private boolean validate() {
			// Se fuerza establecer contraseña y cumpleaños.
			return (this.hashedPassword != null && this.birthday != null);
		}
	}

	/**
	 * Clase de representación interna de un usuario. Expone métodos que permiten
	 * mutar los datos del usuario. Se exponen también los métodos necesarios para
	 * la persistencia {@link Persistent}.
	 */
	public static final class Internal extends User implements Persistent {

		private int id;
		private boolean isRegistered;

		/**
		 * Constructor de usuarios.
		 *
		 * @param builder Builder de usuarios que contiene la información establecida
		 *                para el nuevo usuario.
		 */
		private Internal(User.Builder builder) {
			super(builder);

			this.id = 0;
			this.isRegistered = false;
		}

		/**
		 * Añade una canción al set de canciones favoritas. Véase {@link Song}.
		 *
		 * @param favSong Canción que se desea añadir a la lista.
		 */
		public void addFavouriteSong(Song favSong) {
			super.favouriteSongs.add(favSong);
		}

		/**
		 * Añade la playlist proporcionada a la lista de playlists en la posición
		 * especificada. Véase {@link Playlist}.
		 *
		 * @param index    Índice de la lista en el que se desea añadir la playlist.
		 * @param playlist Playlist que se desea añadir a la lista.
		 */
		public void addPlaylist(Playlist playlist) {
			super.playlists.add(playlist);
		}

		/**
		 * Añade una canción a la lista de canciones recientes. Véase {@link Song}.
		 *
		 * @param recentSong Canción que se desea añadir a la lista.
		 */
		public void addRecentSong(Song recentSong) {
			if (super.recentSongs.size() == maxRecentSongs) super.recentSongs.pollLast();
			super.recentSongs.addFirst(recentSong);
		}

		/**
		 * Método para obtener la contraseña hasheada del usuario.
		 *
		 * @return Hash de la contraseña del usuario.
		 */
		public String getHashedPassword() {
			return super.hashedPassword;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getId() {
			return id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isRegistered() {
			return isRegistered;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void registerId(int id) {
			if (isRegistered()) {
				return;
			}

			this.id = id;
			this.isRegistered = true;
		}

		/**
		 * Elimina una canción del set de canciones favoritas. Véase {@link Song}.
		 *
		 * @param index Índice de la canción que se desea eliminar del set de canciones
		 *              favoritas.
		 *
		 * @return {@code true} si se eliminó la canción favorita.
		 */
		public boolean removeFavouriteSong(Song favSong) {
			return super.favouriteSongs.remove(favSong);
		}

		/**
		 * Elimina una playlist de la lista de playlists del usuario. Véase
		 * {@link Playlist}.
		 *
		 * @param p Playlist que se desea eliminar de la lista.
		 *
		 * @return Resultado de la eliminación.
		 */
		public boolean removePlaylist(Playlist p) {
			return super.playlists.remove(p);
		}

		/**
		 * Elimina una canción de la lista de canciones recientes. Véase {@link Song}.
		 *
		 * @param index Índice de la canción que se desea eliminar de la lista de
		 *              canciones recientes.
		 *
		 * @return Canción que se ha eliminado.
		 */
		public boolean removeRecentSong(Song s) {
			return super.recentSongs.remove(s);
		}

		/**
		 * Método para cambiar el estado premium del usuario.
		 *
		 * @param premium Nuevo estado premium deseado.
		 *
		 * @return Estado premium que se le ha establecido al usuario.
		 */
		public boolean setPremium(boolean premium) {
			super.isPremium = premium;
			return super.isPremium;
		}
		
		/**
		 * Establece el descuento que aplicar al usuario.
		 * 
		 * @param discount Descuento que se desea asignar al usuario.
		 */
		public void setDiscount(Discount discount) {
			super.discount = discount;
		}
	}

	private final Date birthday;
	private final Set<Song> favouriteSongs;
	private final String hashedPassword;
	private boolean isPremium;
	private final List<Playlist> playlists;
	private final Deque<Song> recentSongs;
	private final String username;
	private Discount discount;

	/**
	 * Constructor de usuarios.
	 *
	 * @param builder Builder de usuarios que contiene la información establecida
	 *                para el nuevo usuario.
	 */
	private User(User.Builder builder) {
		this.username = builder.username;
		this.hashedPassword = builder.hashedPassword;
		this.birthday = builder.birthday;
		this.playlists = builder.playlists;
		this.recentSongs = builder.recentSongs;
		this.favouriteSongs = builder.favouriteSongs;
		this.isPremium = builder.isPremium;
		this.discount = builder.discount;
	}

	/**
	 * Método para obtener una vista mutable del usuario. Permitirá acceder a los
	 * métodos que mutan los datos.
	 *
	 * @return Vista mutable del usuario.
	 */
	@Override
	public User.Internal asMut() {
		return (User.Internal) this;
	}

	/**
	 * Método para comprobar si una contraseña coincide con la del usuario.
	 *
	 * @param password Contraseña en claro que se desea comprobar.
	 *
	 * @return {@code true} si la contraseña coincide con la del usuario y
	 *         {@code false} en cualquier otro caso.
	 */
	public boolean checkPassword(String password) {
		String hp = StringHasher.hash(password);
		return this.hashedPassword.equals(hp);
	}

	/**
	 * Método para obtener el cumpleaños del usuario.
	 *
	 * @return Cumpleaños del usuario.
	 */
	public Date getBirthday() {
		return birthday;
	}
	
	public String getPrintableBirthday() {
        return printableDateFormat.format(birthday);  
	}

	/**
	 * Método para obtener un set no modificable de las canciones favoritas del
	 * usuario.
	 *
	 * @return Set no modificable de las canciones favoritas.
	 */
	public Set<Song> getFavouriteSongs() {
		return Collections.unmodifiableSet(favouriteSongs);
	}

	/**
	 * Método para obtener una playlist del usuario.
	 *
	 * @param index Índice de la playlist deseada en la lista de playlists.
	 *
	 * @return Playlist de la lista correspondiente al índice dado.
	 */
	public Playlist getPlaylist(int index) {
		return playlists.get(index);
	}

	/**
	 * Método para obtener una lista no modificable de las playlist del usuario.
	 *
	 * @return Lista no modificable de playlists.
	 */
	public List<Playlist> getPlaylists() {
		return Collections.unmodifiableList(playlists);
	}

	/**
	 * Método para obtener una lista no modificable de las canciones recientes del
	 * usuario.
	 *
	 * @return Lista no modificable de las canciones recientes.
	 */
	public List<Song> getRecentSongs() {
		return Collections.unmodifiableList(new ArrayList<>(recentSongs));
	}

	/**
	 * Método para obtener el nombre de usuario.
	 *
	 * @return Nombre de usuario.
	 */
	public String getUserName() {
		return username;
	}

	/**
	 * Método para obtener el estado premium del usuario.
	 *
	 * @return Estado premium del usuario.
	 */
	public boolean isPremium() {
		return isPremium;
	}
	
	/**
	 * Obtiene el descuento actual aplicado al usuario.
	 * 
	 * @return Descuento del usuario.
	 */
	public Discount getDiscount() {
		return discount;
	}
	
	/**
	 * Obtiene el precio del premium para el usuario.
	 * 
	 * @return Precio del premium del usuario.
	 */
	public double getPremiumCost() {
		if (discount.aplicable(this)) return discount.getDiscountFactor() * premiumBaseCost;
		return premiumBaseCost;
	}
}