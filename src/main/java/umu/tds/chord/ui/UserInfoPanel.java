package umu.tds.chord.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import umu.tds.chord.controller.Controller;
import umu.tds.chord.controller.UserStatusEvent;
import umu.tds.chord.controller.UserStatusListener;

public class UserInfoPanel extends JPanel {

	private static final long serialVersionUID = -8244977576836372669L;
	private static final String userPanelTitle = "Usuario";
	private static final String templateUserName = "<no user>";
	private static final String userPre = "Usuario: ";
	private static final String templateBirthday = "<no birthday>";
	private static final String birthdayPre = "Cumpleaños: ";
	private static final String premiumText = "Premium";
	private static final String logoutButtonText = "Cerrar sesión";
	private static final String deleteAccButtonText = "Eliminar cuenta";

	private JLabel userName;
	private JLabel birthday;
	private ResponsiveToggleButton premiumToggle;

	public UserInfoPanel() {

		setLayout(new GridBagLayout());

		initializeUserName();
		initializeBirthday();
		initializePremiumToggle();
		initializeLogoutButton();
		initializeDeleteAccountButton();

		registerListeners();

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), userPanelTitle));
	}

	// ---------- Interfaz. ----------

	private void initializeUserName() {
		userName = new JLabel(templateUserName);
		userName.setHorizontalAlignment(SwingConstants.LEFT);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 10, 10, 10);

		add(userName, constraints);
	}

	private void initializeBirthday() {
		birthday = new JLabel(templateBirthday);
		birthday.setHorizontalAlignment(SwingConstants.LEFT);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 10, 10, 10);

		add(birthday, constraints);
	}

	private void initializePremiumToggle() {
		premiumToggle = new ResponsiveToggleButton(premiumText);
		premiumToggle.setSelected(false);
		premiumToggle.addActionListener(e -> togglePremium());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 10, 10, 5);
		constraints.fill = GridBagConstraints.BOTH;

		add(premiumToggle, constraints);
	}

	private void initializeLogoutButton() {
		ResponsiveButton logoutButton = new ResponsiveButton(logoutButtonText);
		logoutButton.addActionListener(e -> logout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 5, 10, 5);
		constraints.fill = GridBagConstraints.BOTH;

		add(logoutButton, constraints);
	}

	private void initializeDeleteAccountButton() {
		ResponsiveButton deleteAccountButton = new ResponsiveButton(deleteAccButtonText);
		deleteAccountButton.addActionListener(e -> remove());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 5, 10, 5);
		constraints.fill = GridBagConstraints.BOTH;

		add(deleteAccountButton, constraints);
	}

	// ---------- Comunicación con el controlador. ----------

	private void registerListeners() {

		Controller.INSTANCE.registerUserStatusListener(new UserStatusListener() {
			@Override
			public void onUserLogin(UserStatusEvent e) {
				e.getUser().ifPresentOrElse(u -> {
					userName.setText(userPre + u.getUserName());
					birthday.setText(birthdayPre + u.getPrintableBirthday());
					premiumToggle.setSelected(u.isPremium());
					premiumToggle.setText("<html>" + premiumText + "<br />" + u.getPremiumCost() + "€</html>");
				}, () -> onUserLogout(e));
			}

			@Override
			public void onUserMetadataChange(UserStatusEvent e) {
				onUserLogin(e);
			}

			@Override
			public void onUserLogout(UserStatusEvent e) {
				userName.setText(templateUserName);
				birthday.setText(templateBirthday);
				premiumToggle.setText(premiumText);
				premiumToggle.setSelected(false);
			}
		});
	}

	private void logout() {
		Controller.INSTANCE.logout();
	}

	private void remove() {
		Controller.INSTANCE.remove();
	}

	private void togglePremium() {
		Controller.INSTANCE.togglePremium();
	}

}
