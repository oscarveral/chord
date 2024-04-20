package umu.tds.chord.ui;

/**
 * Enumerado de eventos que pueden producirse en la interfaz de usuario.
 * Véase {@link InterfaceEventListener}.
 */
public enum InterfaceEvent {
	
	/**
	 * El emisor quiere que se muestre el panel de inicio de sesión de Github.
	 */
	GITHUB_PANEL_REQUEST,
	
	/**
	 * El emisor quiere que se muestre el panel de registro.
	 */
	REGISTER_PANEL_REQUEST,
	
	/**
	 * El emisor quiere que se muestre el panel de búsqueda.
	 */
	SEARCH_PANEL_REQUEST,
	
	/**
	 * El emisor quiere que se muestre el panel de gestión de playlists.
	 */
	PLAYLIST_MNGMT_PANEL_REQUEST,
	
	/**
	 * El emisor quiere que se muestre el panel de canciones recientes.
	 */
	RECENT_SONGS_PANEL_REQUEST,
	
	/**
	 * El emisor quiere que se muestre el panel de playlists.
	 */
	PLAYLISTS_PANEL_REQUEST,
	
	/**
	 * El emisor quiere que la interfaz regrese a un estado anterior al actual.
	 */
	RETURN,

}
