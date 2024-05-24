package umu.tds.chord;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import umu.tds.chord.model.Playlist;
import umu.tds.chord.model.PlaylistFactory;
import umu.tds.chord.model.UserRepository;

public class UserRepositoryTest {
	
	private static final String transientTestUsername = "TransientTestUser";
	private static final String transientTestPassword = "TransientTestPassword";
	private static final String persistentTestUsername = "PersistentTestUsername";
	private static final String persistentTestPassword = "PersistentTestPassword";
	private static final Date birthday = Date.from(Instant.now());
	
	private static final Playlist testPlaylist = PlaylistFactory.createPlaylist("TestPlaylist", "TestDescription").get();
	
	@BeforeClass
	public static void beforeAll() {
		// Eliminar usuario efímero de pruebas si existía.
		UserRepository.INSTANCE.getUser(transientTestUsername, transientTestPassword).ifPresent(u -> 
			UserRepository.INSTANCE.removeUser(u)
		);
		
		// Registrar usuario persistente de pruebas si no existía.
		if (UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isEmpty())
			UserRepository.INSTANCE.addUser(persistentTestUsername, persistentTestPassword, birthday);
	}
	
	@AfterClass
	public static void afterAll() {
		// Eliminar el usuario efímero de pruebas si existía.
		UserRepository.INSTANCE.getUser(transientTestUsername, transientTestPassword).ifPresent(u -> 
			UserRepository.INSTANCE.removeUser(u)
		);
		
		// Eliminar usuario persistente de pruebas si existía.
		UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).ifPresent(u -> 
			UserRepository.INSTANCE.removeUser(u)
		);
		
	}
	
	@After
	public void after() {
		// Añade el usuario persistente si fue borrado.
		if (UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isEmpty())
			UserRepository.INSTANCE.addUser(persistentTestUsername, persistentTestPassword, birthday);
	}
	
	@Test
	public void testAddUser() {
		boolean res1 = UserRepository.INSTANCE.addUser(transientTestUsername, transientTestPassword, birthday);
		assertEquals(true, res1);
		boolean res2 = UserRepository.INSTANCE.getUser(transientTestUsername, transientTestPassword).isPresent();
		assertEquals(true, res2);
		boolean res3 = UserRepository.INSTANCE.addUser(transientTestUsername, transientTestPassword, birthday);
		assertEquals(false, res3);
	}

	@Test
	public void testGetUser() {
		boolean res1 = UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isPresent();
		assertEquals(true, res1);
	}

	@Test
	public void testRemoveUser() {
		boolean res1 = UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isPresent();
		assertEquals(true, res1);
		UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).ifPresent(u -> {
			boolean res2 = UserRepository.INSTANCE.removeUser(u);
			assertEquals(true, res2);
			boolean res3 = UserRepository.INSTANCE.removeUser(u);
			assertEquals(false, res3);
		});
		boolean res4 = UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isPresent();
		assertEquals(false, res4);
	}

	@Test
	public void testUpdateUser() {
		boolean res1 = UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isPresent();
		assertEquals(true, res1);
		UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).ifPresent(u -> {
			int res2 = u.getPlaylists().size();
			assertEquals(0, res2);
			u.asMut().addPlaylist(testPlaylist);
			boolean res3 = UserRepository.INSTANCE.updateUser(u);
			assertEquals(true, res3);
			int res4 = u.getPlaylists().size();
			assertEquals(1, res4);
		});
		UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).ifPresent(u -> {
			int res5 = u.getPlaylists().size();
			assertEquals(1, res5);
			u.asMut().removePlaylist(res5 - 1);
			boolean res6 = UserRepository.INSTANCE.updateUser(u);
			assertEquals(true, res6);
			int res7 = u.getPlaylists().size();
			assertEquals(0, res7);
		});
		UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).ifPresent(u -> {
			int res8 = u.getPlaylists().size();
			assertEquals(0, res8);
		});
	}
}
