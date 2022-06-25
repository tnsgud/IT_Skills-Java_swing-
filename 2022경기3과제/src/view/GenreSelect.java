package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import tool.Tool;

public class GenreSelect extends JDialog implements Tool {
	ArrayList<String> list = new ArrayList<>();
	GameInfo gameInfo;

	public GenreSelect() {
		var c = new JPanel(new GridLayout(0, 2));
		var s = new JPanel();

		setModal(true);
		setSize(200, 250);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		add(c);
		add(s, "South");

		for (int i = 1; i < g_genre.length; i++) {
			var l = lbl(g_genre[i], 0);
			l.setForeground(Color.white);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (l.getForeground() == Color.white) {
						list.add(l.getText());
						l.setForeground(Color.gray);
					} else {
						list.remove(l.getText());
						l.setForeground(Color.white);
					}
				}
			});
			c.add(l);
		}

		s.add(btn("닫기", a -> {
			if (gameInfo != null) {
				gameInfo.genreLbl.setText(String.join(",", list.toArray(String[]::new)));
			} else {

			}
			
			dispose();
		}));
	}

	public GenreSelect(GameInfo gameInfo) {
		this();
		this.gameInfo = gameInfo;
	}
}
