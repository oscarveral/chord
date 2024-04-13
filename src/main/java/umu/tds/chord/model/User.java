package umu.tds.chord.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import umu.tds.chord.utils.StringHasher;

/**
 * Clase abstracta que representa un usuario. Expone sólo los métodos de lectura
 * de datos.
 */
public abstract sealed class User implements Mutable<User.Internal>{
	
	private final String username;
	private final String hashedPassword;
	private final LocalDate birthday;
	private final List<Playlist> playlists;
	private final List<Song> recentSongs;
	
	private boolean isPremium;
	
	/**
	 * Clase constructora de usuarios.
	 */
	public final static class Builder {
		
		private String username;
		private String hashedPassword;
		private LocalDate birthday;
		private List<Playlist> playlists;
		private List<Song> recentSongs;
		private boolean isPremium;
		
		/**
		 * Crea un nuevo builder de usuarios especificando el nombre de usuario.
		 * Se deberán establecer obligatoriamente contraseña y cumpleaños.
		 * Por defecto las listas de playlist y canciones recientes se
		 * inicializan vacías.
		 *
		 * @param username Nombre de usuario del nuevo usuario.
		 */
		public Builder(String username) {
			this.username = username;
			this.hashedPassword = null;
			this.birthday = null;
			this.playlists = new ArrayList<Playlist>();
			this.recentSongs = new ArrayList<Song>();
			this.isPremium = false;
		}
		
		/**
		 * Establece la contraseña del nuevo usuario.
		 * 
		 * @param password Contraseña en claro del usuario. Será hasheada. 
		 * Véase {@link StringHasher}.
		 * 
		 * @return Instancia acutal del builder.
		 */
		public Builder password(String password) {
			this.hashedPassword = StringHasher.hash(password);
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
		 * Establece el cumpleaños del usuario.
		 * 
		 * @param birthday Cumpleaños del usuario.
		 * 
		 * @return Instancia actual del builder.
		 */
		public Builder birthday(LocalDate birthday) {
			this.birthday = birthday;
			return this;
		}
		
		/**
		 * Establece la lista de playlist del usuario.
		 * 
		 * @param playlists Lista de playlists del usuario. Véase 
		 * {@link Playlist}.
		 * 
		 * @return Instancia actual del builder.
		 */
		public Builder playlists(List<Playlist> playlists) {
			this.playlists = playlists;
			return this;
		}
		
		/**
		 * Establece la lista de canciones recientes del usuario.
		 * 
		 * @param recentSongs Lista de canciones recientes del usuario. Véase
		 * {@link Song}.
		 * 
		 * @return Instancia actual del builder.
		 */
		public Builder recentSongs(List<Song> recentSongs) {
			this.recentSongs = recentSongs;
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
		
		private boolean validate() {
			// Se fuerza establecer contraseña y cumpleaños.
			if (this.hashedPassword == null)
				return false;
			if (this.birthday == null)
				return false;
			
			return true;
		}
		
		/**
		 * Construye un usuario a partir de la información actual.
		 * 
		 * @return Usuario construido o un opcional vacío si no se ha 
		 * proporcionado contraseña o cumpleaños. Véase {@link User}.
		 */
		public Optional<User> build() {
			if (!validate())
				return Optional.empty();
		
			return Optional.of(new User.Internal(this));
		}
	}
	
	/**
	 * Constructor de usuarios. 
	 * 
	 * @param builder Builder de usuarios que contiene la información 
	 * establecida para el nuevo usuario.
	 */
	private User(User.Builder builder) {
		this.username = builder.username;
		this.hashedPassword = builder.hashedPassword;
		this.birthday = builder.birthday;
		this.playlists = builder.playlists;
		this.recentSongs = builder.recentSongs;
		this.isPremium = builder.isPremium;
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
	 * Método para obtener el cumpleaños del usuario.
	 * 
	 * @return Cumpleaños del usuario.
	 */
	public LocalDate getBirthday() {
		return birthday;
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
	 * Método para obtener una lista no modificable de las canciones recientes
	 * del usuario.
	 * 
	 * @return Lista no modificable de las canciones recientes.
	 */
	public List<Song> getRecentSongs() {
		return Collections.unmodifiableList(recentSongs);
	}
	
	/**
	 * Método para obtener una canción reciente del usuario.
	 * 
	 * @param index Índice de la canción deseada en la lista de canciones 
	 * recientes.
	 * 
	 * @return Canción de la lista de canciones recientes correspondiente al
	 * índice dado.
	 */
	public Song getRecentSong(int index) {
		return recentSongs.get(index);
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
	 * Método para comprobar si una contraseña coincide con la del usuario.
	 * 
	 * @param password Contraseña en claro que se desea comprobar.
	 * 
	 * @return {@code true} si la contraseña coincide con la del usuario y 
	 * {@code false} en cualquier otro caso.
	 */
	public boolean checkPassword(String password) {
		String hashedPassword = StringHasher.hash(password);
		return this.hashedPassword.equals(hashedPassword);
	}
	
	/**
	 * Método para obtener una vista mutable del usuario. Permitirá acceder
	 * a los métodos que mutan los datos.
	 * 
	 * @return Vista mutable del usuario.
	 */
	public User.Internal asMut() {
		return (User.Internal) this;
	}
	
	/**
	 * Clase de representación interna de un usuario. Expone métodos que 
	 * permiten mutar los datos del usuario. Se exponen también los métodos
	 * necesarios para la persistencia {@link Persistent}.
	 */
	public final static class Internal extends User implements Persistent {		
		
		private int id;
		private boolean isRegistered;
		
		/**
		 * Constructor de usuarios. 
		 * 
		 * @param builder Builder de usuarios que contiene la información 
		 * establecida para el nuevo usuario.
		 */
		private Internal(User.Builder builder) {
			super(builder);
			
			this.id = 0;
			this.isRegistered = false;
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
		 * Añade la playlist proporcionada a la lista de playlists en la 
		 * posición especificada. Véase {@link Playlist}.
		 * 
		 * @param index Índice de la lista en el que se desea añadir la 
		 * playlist.
		 * @param playlist Playlist que se desea añadir a la lista.
		 */
		public void addPlaylist(int index, Playlist playlist) {
			super.playlists.add(index, playlist);
		}
		
		/**
		 * Elimina una playlist de la lista de playlists del usuario. Véase
		 * {@link Playlist}.
		 * 
		 * @param index Índice en la lista de playlists de la playlist que se 
		 * desea eliminar.
		 * 
		 * @return Playlist eliminada de la lista.
		 */
		public Playlist removePlaylist(int index) {
			return super.playlists.remove(index);
		}
		
		/**
		 * Añade una canción a la lista de canciones recientes. Véase 
		 * {@link Song}.
		 * 
		 * @param index Posicion en la que se desea añadir la canción 
		 * proprocionada.
		 * @param recentSong Canción que se desea añadir a la lista.
		 */
		public void addRecentSong(int index, Song recentSong) {
			super.recentSongs.add(index, recentSong);
		}
		
		/**
		 * Elimina una canción de la lista de canciones recientes. Véase
		 * {@link Song}.
		 * 
		 * @param index Índice de la canción que se desea eliminar de la lista
		 * de canciones recientes.
		 * @return
		 */
		public Song removeRecentSong(int index) {
			return super.recentSongs.remove(index);
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
			if (isRegistered())
				return;
			
			this.id = id;
			this.isRegistered = true;
		}
	}
}