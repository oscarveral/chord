package umu.tds.chord.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.Player;
import umu.tds.chord.controller.SongStatusEvent;
import umu.tds.chord.controller.SongStatusListener;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;
import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;

public enum StateManager {

	INSTANCE;

	public enum UIEvents {
		SONG_SEARCH, PLAYLSITS_MNGMT, RECENT_SONGS, PLAYLIST, MAS_REPRODUCIDAS, REGISTER,
	}

	private Optional<Playlist> selectedPlaylist;
	private List<Song> selectedSongs;

	private Optional<Interface> callbackInterface;
	private Optional<MainPanel> callbackMainPanel;
	private Optional<PlaylistInfoPanel> callbackPlaylistInfo;
	private Optional<PlaylistFormPanel> callbackPlaylistForm;

	private StateManager() {
		selectedPlaylist = Optional.empty();
		selectedSongs = new ArrayList<>();
		callbackMainPanel = Optional.empty();
		callbackPlaylistInfo = Optional.empty();
		callbackPlaylistForm = Optional.empty();

		registerControllerListeners();
	}

	public void setCallbackMainPanel(MainPanel p) {
		callbackMainPanel = Optional.ofNullable(p);
	}

	public void setCallbackInterface(Interface i) {
		callbackInterface = Optional.ofNullable(i);
	}

	public void setCallbackPlaylistInfo(PlaylistInfoPanel p) {
		callbackPlaylistInfo = Optional.ofNullable(p);
	}

	public void setCallbackPlaylistForm(PlaylistFormPanel p) {
		callbackPlaylistForm = Optional.ofNullable(p);
	}

	public void setSelectedPlaylist(Playlist p) {
		selectedPlaylist = Optional.ofNullable(p);
		selectedPlaylist.ifPresent(s -> {
			callbackPlaylistInfo.ifPresent(c -> c.setSelectedPlaylist(s));
			callbackPlaylistForm.ifPresent(c -> c.setSelectedPlaylist(s));
		});
	}

	public void clearSelectedPlaylist() {
		selectedPlaylist = Optional.empty();
		callbackPlaylistInfo.ifPresent(c -> c.clearSelectedPlaylist());
	}

	public void removeSelectedPlaylist() {
		selectedPlaylist.ifPresent(p -> {
			Controller.INSTANCE.removePlaylist(p);
			callbackPlaylistInfo.ifPresent(c -> c.clearSelectedPlaylist());
		});
	}

	public void reproduceSelectedPlaylist() {
		selectedPlaylist.ifPresent(Player.INSTANCE::loadPlaylist);
	}

	public void updateSelectedPlaylist(String name, String desc) {
		selectedPlaylist.ifPresent(p -> Controller.INSTANCE.updatePlaylist(p, name, desc));
	}

	public void addSelectedSong(Song s) {
		selectedSongs.add(s);
	}

	public void clearSelectedSongs() {
		selectedSongs.clear();
	}

	public void removeSelectedSongs(String password) {
		Controller.INSTANCE.removeSongs(selectedSongs, password);
	}

	public void addSelelectedSongsToQueue() {
		Player.INSTANCE.addCola(selectedSongs);
	}

	public void reproduceFirstSelectedSong() {
		if (selectedSongs.isEmpty())
			return;
		Player.INSTANCE.pushReproduce(selectedSongs.get(0));
	}

	public void addSelectedSongsToSelectedPlaylist() {
		selectedPlaylist.ifPresent(p -> Controller.INSTANCE.addSongsPlaylist(p, selectedSongs));
	}

	public void removeSelectedSongsFromSelectedPlaylist() {
		selectedPlaylist.ifPresent(p -> Controller.INSTANCE.removeSongsPlaylist(p, selectedSongs));
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
		case MAS_REPRODUCIDAS:
			callbackMainPanel.ifPresent(t -> t.showMasReproducidas());
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
				if (e.getUser().isPresent())
					selectedPlaylist.ifPresent(p -> callbackPlaylistInfo.ifPresent(c -> c.setSelectedPlaylist(p)));
			}
		});

		Controller.INSTANCE.registerSongStatusListener(new SongStatusListener() {
			@Override
			public void onSongDelete(SongStatusEvent e) {
				if (!e.isFailed())
					selectedSongs.retainAll(e.getSongs());
			}
		});
	}
}