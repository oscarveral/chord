package umu.tds.chord;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umu.tds.chord.model.Song;
import umu.tds.chord.model.SongRepository;

public class SongRepositoryTest {

	private static final String testSongName = "testSong";
	private static final String testAuthorName = "testAuthor";
	private static final String testPath = "testPath";
	private static final String testStyle = "testStyle";
	private static final Song testSong = new Song.Builder(testSongName).author(testAuthorName).path(testPath)
			.style(testStyle).build().get();

	@Before
	public void before() {
		// Limiar el repositorio.
		SongRepository.INSTANCE.clearSonRepositoryState();
	}

	@After
	public void after() {
		// Limiar el repositorio.
		SongRepository.INSTANCE.clearSonRepositoryState();
	}

	@Test
	public void testAddSong() {
		boolean res1 = SongRepository.INSTANCE.addSong(testSongName, testAuthorName, testPath, testStyle).isPresent();
		assertEquals(true, res1);
		boolean res2 = SongRepository.INSTANCE.existSong(testSongName, testAuthorName, testPath, testStyle);
		assertEquals(true, res2);
		boolean res3 = SongRepository.INSTANCE.existStyle(testStyle);
		assertEquals(true, res3);
		boolean res4 = SongRepository.INSTANCE.addSong(testSongName, testAuthorName, testPath, testStyle).isPresent();
		assertEquals(false, res4);
	}

	@Test
	public void testGetSearch() {
		Optional<String> name = Optional.of(testSongName);
		Optional<String> author = Optional.of(testAuthorName);
		Optional<String> style = Optional.of(testStyle);

		int size1 = SongRepository.INSTANCE.getSearch(name, author, style).size();
		assertEquals(0, size1);
		boolean res1 = SongRepository.INSTANCE.addSong(testSongName, testAuthorName, testPath, testStyle).isPresent();
		assertEquals(true, res1);
		int size2 = SongRepository.INSTANCE.getSearch(name, author, style).size();
		assertEquals(1, size2);
		boolean res2 = SongRepository.INSTANCE.removeSong(testSong);
		assertEquals(true, res2);
		int size3 = SongRepository.INSTANCE.getSearch(name, author, style).size();
		assertEquals(0, size3);
	}

	@Test
	public void testRemoveSong() {
		boolean res1 = SongRepository.INSTANCE.addSong(testSongName, testAuthorName, testPath, testStyle).isPresent();
		assertEquals(true, res1);
		boolean res2 = SongRepository.INSTANCE.removeSong(testSong);
		assertEquals(true, res2);
		boolean res3 = SongRepository.INSTANCE.existSong(testSongName, testAuthorName, testPath, testStyle);
		assertEquals(false, res3);
		boolean res4 = SongRepository.INSTANCE.existStyle(testStyle);
		assertEquals(false, res4);
		boolean res5 = SongRepository.INSTANCE.removeSong(testSong);
		assertEquals(false, res5);
	}

}
