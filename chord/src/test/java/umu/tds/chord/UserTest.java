package umu.tds.chord;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.Song;
import umu.tds.chord.model.User;
import umu.tds.chord.model.discount.DiscountFactory;
import umu.tds.chord.model.discount.DiscountFactory.Type;

public class UserTest {

	private static final String username = "test";
	private static final String pass = "pass";
	private static final Date birth = Date.from(Instant.now());
	private static final String playlistName = "testPlaylist";
	private static final String playlistDesc = "testDesc";
	private static final String songName = "testSong";
	private static final String testAuthor = "testAuthor";
	private static final String testStyle = "testStyle";
	private static final String testPath = "testPath";
	
	private static User user = null;
	private static Playlist playlist = null;
	private static Song song = null;			
	
	@BeforeClass
	public static void buildUser() {
		// Construcción de datos de prueba.
		user = new User.Builder(username).password(pass).birthday(birth).build().get();
		playlist = new Playlist.Builder(playlistName).description(playlistDesc).build().get();
		song = new Song.Builder(songName).author(testAuthor).path(testPath).style(testStyle).build().get();
	}
	
	@Test
	public void testCreation() {
		assertEquals(true, user.getUserName().equals(username));
		assertEquals(true, user.checkPassword(pass));
		assertEquals(true, user.getBirthday().equals(birth));
	}
	
	@Test
	public void testPremium() {
		assertEquals(false, user.isPremium());
		user.asMut().setPremium(true);
		assertEquals(true, user.isPremium());
		user.asMut().setPremium(false);
		assertEquals(false, user.isPremium());
	}
	
	@Test
	public void testFavouriteSongs() {
		assertEquals(0, user.getFavouriteSongs().size());
		assertEquals(false, user.getFavouriteSongs().contains(song));
		user.asMut().addFavouriteSong(song);
		assertEquals(1, user.getFavouriteSongs().size());
		assertEquals(true, user.getFavouriteSongs().contains(song));
		user.asMut().removeFavouriteSong(song);
		assertEquals(0, user.getFavouriteSongs().size());
		assertEquals(false, user.getFavouriteSongs().contains(song));
	}
	
	@Test
	public void testRecentSong() {
		assertEquals(0, user.getRecentSongs().size());
		assertEquals(false, user.getRecentSongs().contains(song));
		user.asMut().addRecentSong(song);
		assertEquals(1, user.getRecentSongs().size());
		assertEquals(true, user.getRecentSongs().contains(song));
		user.asMut().removeRecentSong(song);
		assertEquals(0, user.getRecentSongs().size());
		assertEquals(false, user.getRecentSongs().contains(song));
		user.asMut().addRecentSong(song);
		user.asMut().addRecentSong(song);
		assertEquals(2, user.getRecentSongs().size());
		assertEquals(true, user.getRecentSongs().contains(song));
		user.asMut().removeRecentSong(song);
		assertEquals(0, user.getRecentSongs().size());
		assertEquals(false, user.getRecentSongs().contains(song));
	}
	
	@Test
	public void testPlaylist() {
		assertEquals(0, user.getPlaylists().size());
		assertEquals(false, user.getPlaylists().contains(playlist));
		user.asMut().addPlaylist(playlist);
		assertEquals(1, user.getPlaylists().size());
		assertEquals(true, user.getPlaylists().contains(playlist));
		user.asMut().removePlaylist(playlist);
		assertEquals(0, user.getPlaylists().size());
		assertEquals(false, user.getPlaylists().contains(playlist));
	}
	
	@Test
	public void testDiscount() {
		assertEquals(DiscountFactory.Type.NONE, user.getDiscount().getType());
		user.asMut().setDiscount(DiscountFactory.createDiscount(Type.TEMPORARY));
		assertEquals(DiscountFactory.Type.TEMPORARY, user.getDiscount().getType());
	}
}
