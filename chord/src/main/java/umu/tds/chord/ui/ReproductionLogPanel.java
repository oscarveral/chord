package umu.tds.chord.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import umu.tds.chord.ui.SongTable.Mode;

public class ReproductionLogPanel extends JPanel {

	private static final long serialVersionUID = -4746920264323689567L;
	private static final String title = "Canciones más reproducidas";

	public ReproductionLogPanel() {
		setLayout(new BorderLayout(10, 10));
		initializeTable();
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
	}

	private void initializeTable() {
		SongTable table = new SongTable(Mode.EXTENDED);
		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
	}
}
