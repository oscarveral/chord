package umu.tds.chord.controller;

import java.util.EventObject;
import java.util.Optional;

import umu.tds.chord.model.User;

/**
 * Clase que representa los eventos de cambio de estado del usuario con la 
 * sesión iniciada.
 */
public class UserStatusEvent extends EventObject {

	private static final long serialVersionUID = -2215463958884119904L;

	private Optional<User> user;
	
	/**
	 * Constructor del evento.
	 * 
	 * @param source Origen del evento.
	 * @param user Usuario vinculado al evento.
	 */
	public UserStatusEvent(Object source, User user) {
		super(source);
		
		this.user = Optional.ofNullable(user);
	}
	
	/**
	 * Obtiene el usuario vinculado a este evento.
	 * 
	 * @return Usuario vinculado al evento.
	 */
	public Optional<User> getUser() {
		return user;
	}
}
