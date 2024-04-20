package umu.tds.chord.controller;

import umu.tds.chord.model.User;

public interface UserStatusListener {
	
	default void onLogin(User u) {
	};

	default void onLogout() {
	};

	default void onPremiumChange(boolean premium) {
	};
}