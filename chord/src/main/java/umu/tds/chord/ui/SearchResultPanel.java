package umu.tds.chord.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import umu.tds.chord.ui.SongTable.Mode;

public class SearchResultPanel extends JPanel {

	private static final long serialVersionUID = 1281782779628796147L;

	public SearchResultPanel() {
		setLayout(new BorderLayout(10, 10));
		initializeTable();
	}

	private void initializeTable() {
		SongTable table = new SongTable(Mode.SEARCH);
		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
	}
}
