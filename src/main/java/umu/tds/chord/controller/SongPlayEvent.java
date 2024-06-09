package umu.tds.chord.controller;

import java.util.EventObject;
import java.util.Optional;

import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;

/**
 * Evento de escucha de canciones.
 */
public class SongPlayEvent extends EventObject {

	private static final long serialVersionUID = -6460593865001084008L;

	private Optional<Song> song;
	private Optional<Playlist> playlist;
	private boolean failed;
	
	public SongPlayEvent(Object source) {
		super(source);
		this.song = Optional.empty();
		this.playlist = Optional.empty();
		this.failed = false;
	}
	
	public void setSong(Song s) {
		song = Optional.ofNullable(s);
	}
	
	public Optional<Song> getSong() {
		return song;
	}
	
	public void setPlaylist(Playlist p) {
		playlist = Optional.ofNullable(p);
	}
	
	public Optional<Playlist> getPlaylist() {
		return playlist;
	}
	
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public boolean isFailed() {
		return failed;
	}
}
