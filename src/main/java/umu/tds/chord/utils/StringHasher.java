package umu.tds.chord.utils;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

/**
 * Clase de utilidad utilizada para el calculo de hash de contraseñas.
 */
public final class StringHasher {

	/**
	 * Calcula el hash de la cadena de texto proporcionada.
	 * 
	 * @param s Cadena de texto de la que se desea obtener el hash.
	 * @return Cadena de texto con la representación hexadecimal del hash.
	 */
	public static String hash(String s) {
		return Hashing.sha256()
				.hashString(s, StandardCharsets.UTF_8)
				.toString();
	}
}
