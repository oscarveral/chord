package umu.tds.chord;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.model.SongRepository;
import umu.tds.chord.model.UserRepository;

public class ControllerTest {

	private static final String transientTestUsername = "TransientTestUser";
	private static final String transientTestPassword = "TransientTestPassword";
	private static final String persistentTestUsername = "PersistentTestUsername";
	private static final String persistentTestPassword = "PersistentTestPassword";
	private static final LocalDate birthday = LocalDate.now();
	
	private static final String songsXMLPath = "xml/canciones.xml";
	private static final int numberSongs = 6;
	private static final int numberStyles = 7;
	
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
	
	@Before
	public void before() {
		// Limpiar el estado del controlador.
		Controller.INSTANCE.clearControllerState();
		// Eliminar todas las canciones del repositorio de canciones.
		SongRepository.INSTANCE.clearSonRepositoryState();
	}
	
	@After
	public void after() {
		// Limpiar el estado del controlador.
		Controller.INSTANCE.clearControllerState();
		// Eliminar todas las canciones del repositorio de canciones.
		SongRepository.INSTANCE.clearSonRepositoryState();
		
		// Añade el usuario persistente si fue borrado.
		if (UserRepository.INSTANCE.getUser(persistentTestUsername, persistentTestPassword).isEmpty())
			UserRepository.INSTANCE.addUser(persistentTestUsername, persistentTestPassword, birthday);
	}
		
	@Test
	public void testRegister() {
		boolean res1 = Controller.INSTANCE.register(transientTestUsername, transientTestPassword, birthday);	
		assertEquals(true, res1);
		boolean res3 = Controller.INSTANCE.register(transientTestUsername, transientTestPassword, birthday);
		assertEquals(false, res3);
	}

	@Test
	public void testLogin() {
		boolean res1 = Controller.INSTANCE.login(persistentTestUsername, persistentTestPassword);
		assertEquals(true, res1);
		boolean res2 = Controller.INSTANCE.login(persistentTestUsername, persistentTestPassword);
		assertEquals(false, res2);
	}

	@Test
	public void testLogout() {
		boolean res1 = Controller.INSTANCE.logout();
		assertEquals(true, res1);
		boolean res2 = Controller.INSTANCE.login(persistentTestUsername, persistentTestPassword);
		assertEquals(true, res2);
		boolean res3 = Controller.INSTANCE.logout();
		assertEquals(true, res3);
	}

	@Test
	public void testRemove() {
		boolean res1 = Controller.INSTANCE.remove();
		assertEquals(true, res1);
		boolean res2 = Controller.INSTANCE.login(persistentTestUsername, persistentTestPassword);
		assertEquals(true, res2);
		boolean res3 = Controller.INSTANCE.remove();
		assertEquals(true, res3);
		boolean res4 = Controller.INSTANCE.login(persistentTestUsername, persistentTestPassword);
		assertEquals(false, res4);
	}

	@Test
	public void testCargarCanciones() {
		Controller.INSTANCE.cargarCanciones(songsXMLPath);
		int numCanciones = SongRepository.INSTANCE.getSongs().size();
		int numStilos = SongRepository.INSTANCE.getStyles().size();
		assertEquals(numberSongs, numCanciones);
		assertEquals(numberStyles, numStilos);
	}

}
