package umu.tds.chord.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;

public enum StateManager {

	INSTANCE;
	
	public enum UIEvents {
		SONG_SEARCH,
		PLAYLSITS_MNGMT,
		RECENT_SONGS,
		PLAYLIST,
		REGISTER,
	}
	
	private Optional<Playlist> selectedPlaylist;
	private List<Song> selectedSongs;
	
	private Optional<Interface> callbackInterface;
	private Optional<MainPanel> callbackMainPanel;
	
	private StateManager() {
		selectedPlaylist = Optional.empty();
		selectedSongs = new ArrayList<>();
		callbackMainPanel = Optional.empty();
		
		registerControllerListeners();
	}
	
	public void setSelectedPlaylist(Playlist p) {
		selectedPlaylist = Optional.ofNullable(p);
	}
	
	public void clearSelectedSongs() {
		selectedSongs.clear();
	}
	
	public void addSelectedSong(Song s) {
		selectedSongs.add(s);
	}
	
	public void removeSelectedSongs(String password) {
		Controller.INSTANCE.removeSongs(selectedSongs, password);
	}
	
	public void setCallbackMainPanel(MainPanel p) {
		callbackMainPanel = Optional.ofNullable(p);
	}
	
	public void setCallbackInterface(Interface i) {
		callbackInterface = Optional.ofNullable(i);
	}
	
	public void clearSelectedPlaylist() {
		selectedPlaylist = Optional.empty();
	}
	
	public void triggerEvent(UIEvents e) {
		switch (e) {
		case SONG_SEARCH:
			callbackMainPanel.ifPresent(t -> t.showSearch());
			break;
		case PLAYLIST:
			callbackMainPanel.ifPresent(t -> t.showPlaylist());
			break;
		case PLAYLSITS_MNGMT:
			callbackMainPanel.ifPresent(t -> t.showPlaylistsMngmt());
			break;
		case RECENT_SONGS:
			callbackMainPanel.ifPresent(t -> t.showRecentSongs());
			break;
		case REGISTER:
			callbackInterface.ifPresent(t -> t.showRegisterPanel());
			break;
		default:
			break;
		}
	}
	
	private void registerControllerListeners() {
		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogout(UserStatusEvent e) {
				clearSelectedPlaylist();
				clearSelectedSongs();
			}
			
			@Override
			public void onUserLogin(UserStatusEvent e) {
				onUserLogout(e);
			}
			
			@Override
			public void onPlaylistsListUpdate(UserStatusEvent e) {
				e.getUser().ifPresent(u -> {
					selectedPlaylist.ifPresent(p -> {
						if (!u.getPlaylists().contains(p))	selectedPlaylist = Optional.empty();	
					});
				});
			}
		});
		
		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
			@Override
			public void onSongDelete(SongStatusEvent e) {
				if (!e.isFailed()) selectedSongs.retainAll(e.getSongs());
			}
		});
	}
}
