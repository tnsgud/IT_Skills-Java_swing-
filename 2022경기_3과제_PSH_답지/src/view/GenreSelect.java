package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GenreSelect extends BaseDialog {
	HashMap<String, JLabel> lbl = new HashMap<>();
	ArrayList<String> list = new ArrayList<>();
	GameInfo gameInfo;
	ChartPage chartPage;
	InfoEditPage infoEditPage;

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var lbl = super.lbl(c, a, st, sz);
		lbl.setForeground(Color.white);
		return lbl;
	}

	public GenreSelect() {
		super("장르 선택", 200, 250);

		var c = new JPanel(new GridLayout(0, 2));
		var s = new JPanel();

		add(c);
		add(s, "South");

		for (int i = 1; i < g_genre.length; i++) {
			var l = lbl(g_genre[i], 0);
			lbl.put(i + "", l);
			c.add(l);
		}

		s.add(btn("닫기", a -> {
			dispose();
		}));
		

		setJPanelOpaque((JComponent) getContentPane());
	}

	public GenreSelect(GameInfo gameInfo) {
		this();
		this.gameInfo = gameInfo;
		
		for (var entry : lbl.entrySet()) {
			entry.getValue().addMouseListener(new MouseAdapter() {
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
					
					gameInfo.genreLbl.setText(String.join(",", list.toArray(String[]::new)));
				}
			});
		}
	}

	public GenreSelect(ChartPage chartPage) {
		this();
		this.chartPage = chartPage;

		for (var key : lbl.keySet()) {
			var l = lbl.get(key);
			l.setForeground(Color.gray);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();

					if (me.getForeground() == Color.white) {
						list.remove(key);
						me.setForeground(Color.gray);
					} else {
						list.add(key);
						me.setForeground(Color.white);
					}
					
					chartPage.genre = list;
					chartPage.repaint();
				}
			});
		}
		
		lbl.get("1").setForeground(Color.white);
		list.add("1");
	}

	public GenreSelect(InfoEditPage infoEditPage) {
		this();
		this.infoEditPage = infoEditPage;

		for (var key : lbl.keySet()) {
			var l = lbl.get(key);
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
					
					infoEditPage.setFilter();
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
