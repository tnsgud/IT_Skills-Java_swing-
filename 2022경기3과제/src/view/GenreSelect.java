package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tool.Tool;

public class GenreSelect extends BaseDialog {
	HashMap<String, JLabel> lbl = new HashMap<>();
	ArrayList<String> list = new ArrayList<>();
	GameInfo gameInfo;
	InfoEditPage infoEditPage;
	MouseAdapter ma = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			var me = (JLabel) e.getSource();

			if (me.getForeground() == Color.white) {
				list.add(me.getText());
				me.setForeground(Color.gray);
			} else {
				list.remove(me.getText());
				me.setForeground(Color.white);
			}
		}
	};

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var lbl = super.lbl(c, a, st, sz);
		lbl.setForeground(Color.white);
		return lbl;
	}

	public GenreSelect() {
		super(200, 250);
		
		var c = new JPanel(new GridLayout(0, 2));
		var s = new JPanel();

		add(c);
		add(s, "South");

		for (int i = 1; i < g_genre.length; i++) {
			var l = lbl(g_genre[i], 0);
			lbl.put(i + "", l);
			l.addMouseListener(ma);
			c.add(l);
		}

		s.add(btn("닫기", a -> {
			if (gameInfo != null) {
				gameInfo.genreLbl.setText(String.join(",", list.toArray(String[]::new)));
			} else {
				infoEditPage.setFilter();
			}

			dispose();
		}));

		setJPanelOpaque((JComponent) getContentPane());
	}

	public GenreSelect(GameInfo gameInfo) {
		this();
		this.gameInfo = gameInfo;
	}

	public GenreSelect(InfoEditPage infoEditPage) {
		this();
		this.infoEditPage = infoEditPage;

		for (var key : lbl.keySet()) {
			var l = lbl.get(key);
			l.removeMouseListener(ma);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();

					if (me.getForeground() == Color.white) {
						infoEditPage.filters.add(Arrays.asList(g_genre).indexOf(me.getText()) + "");
						me.setForeground(Color.gray);
					} else {
						infoEditPage.filters.remove(Arrays.asList(g_genre).indexOf(me.getText()) + "");
						me.setForeground(Color.white);
					}
				}
			});
		}

		for (var filter : infoEditPage.filters) {
			if (filter.equals("12") || filter.equals("0")) {
				continue;
			}

			lbl.get(filter).setForeground(Color.gray);
			list.add(filter);
		}
	}
}
